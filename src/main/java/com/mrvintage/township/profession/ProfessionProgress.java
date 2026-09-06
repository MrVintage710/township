package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.Township;
import com.mrvintage.township.networking.UpdatePlayerProfessionProgress;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.registry.DataAttachments;
import com.mrvintage.township.ui.PlayerOverlayPatch;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class ProfessionProgress {

    @OnlyIn(Dist.CLIENT)
    public static ProfessionProgress ClientProfessionProgress;

    public static final Codec<ProfessionProgress> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.unboundedMap(Merit.Path.CODEC, MeritProgress.CODEC).fieldOf("in_progress").forGetter(ProfessionProgress::progress),
            Merit.Path.CODEC.listOf().fieldOf("done").forGetter(ProfessionProgress::doneList)
        ).apply(instance, ProfessionProgress::new)
    );

    private final Map<Merit.Path, MeritProgress> inProgress;
    private final Set<Merit.Path> doneList;

    public ProfessionProgress(Map<Merit.Path, MeritProgress> inProgress) {
        this.inProgress = new HashMap<>(inProgress);
        this.doneList = new HashSet<>();
    }

    public ProfessionProgress(Map<Merit.Path, MeritProgress> inProgress, List<Merit.Path> done) {
        this.inProgress = new HashMap<>(inProgress);
        this.doneList = new HashSet<>(done);
    }

    public ProfessionProgress() {
        this(new HashMap<>());
    }

    public static void incrementProgress(ServerPlayer player, Event event) {
        if (event == null) { return; }
        ProfessionProgress progress = player.getData(DataAttachments.PROFESSION_PROGRESS);
        var merits = progress.inProgress.keySet();

        boolean isDirty = false;

        HashSet<Merit.Path> completeMerits = new HashSet<>();

        for (Merit.Path path : merits) {
            Merit merit = Profession.findMerit(player.serverLevel(), path);
            var meritProgress = progress.inProgress.get(path);
            if (meritProgress.isDone()) continue;
            for (Goal goal : merit.goals()) {
                if (!goal.accepts(event)) continue;
                int xp = goal.calcXp(event, merit);
                PlayerOverlayPatch.enqueueXpNotification(path, xp);
                meritProgress.add(xp);
                isDirty = true;
                if (meritProgress.isDone()) {
                    completeMerits.add(path);
                }
            }
        }

        for (Merit.Path path : completeMerits) {
            Merit merit = Profession.findMerit(player.serverLevel(), path);
            if(merit == null) continue;
            progress.awardMerit(path, player, merit);
        }

        if(isDirty) {
            PacketDistributor.sendToPlayer(player, new UpdatePlayerProfessionProgress(progress));
            player.setData(DataAttachments.PROFESSION_PROGRESS, progress);
        }
    }

    /// This method will check if there are any merits that a player can start on, and adds them to in-progress merits.
    /// Should be called on entering and whenever a merit is achieved.
    public static void refreshPlayerMerits(ServerPlayer player) {
        var merits = Profession.allMerits(player.serverLevel());
        var progress = player.getData(DataAttachments.PROFESSION_PROGRESS);
        for(var meritEntry : merits.entrySet()) {
            if(progress.canAdd(meritEntry.getKey()) && meritEntry.getValue().prereqs().stream().allMatch(progress::isDone)) {
                progress.inProgress.put(meritEntry.getKey(), new MeritProgress(meritEntry.getValue().xp()));
            }
        }
        player.setData(DataAttachments.PROFESSION_PROGRESS, progress);
        PacketDistributor.sendToPlayer(player, new UpdatePlayerProfessionProgress(progress));
    }

    public static ProfessionProgress progressOf(ServerPlayer player) {
        return player.getData(DataAttachments.PROFESSION_PROGRESS);
    }

    public static Optional<ProfessionProgress> progressOf(MinecraftServer server, UUID uuid) {
        ServerPlayer onlinePlayer = server.getPlayerList().getPlayer(uuid);

        if(onlinePlayer == null) {
            File playerFile = new File(server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), uuid + ".dat");
            if (playerFile.exists()) {
                CompoundTag nbt = null;
                try {
                    nbt = NbtIo.readCompressed(playerFile.toPath(), NbtAccounter.unlimitedHeap());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (nbt.contains("neoforge:attachments", Tag.TAG_COMPOUND)) {
                    CompoundTag attachments = nbt.getCompound("NeoForge.Attachments");
                    // Read your specific attachment key namespace path
                    String id = Township.MODID + ":profession_progress";
                    if (attachments.contains(id)) {
                        CompoundTag professionProgress = attachments.getCompound(id);
                        return ProfessionProgress.CODEC.parse(NbtOps.INSTANCE, professionProgress).result();
                    }
                }
            }
        } else {
            return Optional.of(ProfessionProgress.progressOf(onlinePlayer));
        }

        return Optional.empty();
    }

    public static boolean hasCompleted(MinecraftServer server, UUID uuid, Merit.Path merit) {
        var progress = progressOf(server, uuid);
        return progress.map(p -> p.doneList.contains(merit)).orElse(false);
    }

    public static boolean hasCompleted(ServerPlayer player, Merit.Path merit) {
        var progress = ProfessionProgress.progressOf(player);
        return progress.doneList.contains(merit);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasCompleted(Merit.Path merit) {
        return ProfessionProgress.ClientProfessionProgress.doneList.contains(merit);
    }

    public boolean isInProgress(Merit.Path path) {
        return inProgress.containsKey(path);
    }

    public boolean isDone(Merit.Path path) {
        var progress = inProgress.get(path);
        if(progress == null) return false;
        return progress.getXp() >= progress.getTagetXp();
    }

    public boolean canAdd(Merit.Path path) {
        return !inProgress.containsKey(path);
    }

    public MeritProgress getInProgress(Merit.Path path) {
        return this.inProgress.get(path);
    }

    public void clearMeritProgress(Merit.Path path) {
        this.inProgress.remove(path);
        this.doneList.remove(path);
    }

    public void clearAllMeritProgress() {
        this.inProgress.clear();
        this.doneList.clear();
    }

    public Map<Merit.Path, MeritProgress> progress() {
        return inProgress;
    }

    public Map<Merit.Path, MeritProgress> progressFromSpecialty(String speciality) {
        return this.progress().entrySet().stream()
            .filter(entry -> entry.getKey().speciality().equals(speciality))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Set<Merit.Path> done() {
        return this.doneList;
    }

    public List<Merit.Path> doneList() {
        return new ArrayList<>(this.done());
    }

    public List<Merit.Path> doneFromSpecialty(String speciality) {
        return this.done().stream()
            .filter(merit -> merit.speciality().equals(speciality))
            .collect(Collectors.toList());
    }

    public List<Merit.Path> all() {
        List<Merit.Path> paths = new ArrayList<>();
        paths.addAll(inProgress.keySet());
        return paths;
    }

    public void awardMerit(Merit.Path meritPath, ServerPlayer player) {
        this.awardMerit(meritPath, player, false);
    }

    public void awardMerit(Merit.Path meritPath, ServerPlayer player, boolean isFake) {
        if (!isFake) {
            if (doneList.contains(meritPath)) return;
            doneList.add(meritPath);
            ProfessionProgress.refreshPlayerMerits(player);
            PlayerOverlayPatch.enqueueMeritCompleteNotification(meritPath);
        } else {
            PlayerOverlayPatch.enqueueMeritCompleteNotification(meritPath);
        }
    }

    public void awardMerit(Merit.Path meritPath, ServerPlayer player, Merit merit) {
        this.awardMerit(meritPath, player, false);
        merit.rewards().forEach(reward -> reward.rewardPlayer(player));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ProfessionProgress) obj;
        return Objects.equals(this.inProgress, that.inProgress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inProgress);
    }

    @Override
    public String toString() {
        return "ProfessionProgress[" +
            "progress=" + inProgress + ']';
    }

}

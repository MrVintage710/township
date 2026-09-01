package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.networking.UpdatePlayerProfessionProgress;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.registry.DataAttachments;
import com.mrvintage.township.ui.PlayerOverlayPatch;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.network.PacketDistributor;

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
        merit.rewards().forEach(reward -> reward.rewardPlayer(player));
        this.awardMerit(meritPath, player, false);
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

package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.registry.DataAttachments;
import com.mrvintage.township.ui.PlayerOverlayPatch;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

import java.util.*;

public final class ProfessionProgress {
    public static final Codec<ProfessionProgress> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.unboundedMap(Merit.Path.CODEC, MeritProgress.CODEC).fieldOf("in_progress").forGetter(ProfessionProgress::progress),
            Merit.Path.CODEC.listOf().fieldOf("done").forGetter(ProfessionProgress::doneList)
        ).apply(instance, ProfessionProgress::new)
    );
    private final Map<Merit.Path, MeritProgress> inProgress;
    private final Set<Merit.Path> done;

    public ProfessionProgress(Map<Merit.Path, MeritProgress> inProgress) {
        this.inProgress = new HashMap<>(inProgress);
        this.done = new HashSet<>();
    }

    public ProfessionProgress(Map<Merit.Path, MeritProgress> inProgress, List<Merit.Path> done) {
        this.inProgress = new HashMap<>(inProgress);
        this.done = new HashSet<>(done);
    }

    public ProfessionProgress() {
        this(new HashMap<>());

    }

    public static void incrementProgress(ServerPlayer player, Event event) {
        ProfessionProgress progress = player.getData(DataAttachments.PROFESSION_PROGRESS);
        var merits = progress.inProgress.keySet();

        boolean isDirty = false;

        for (Merit.Path path : merits) {
            Merit merit = Profession.findMerit(player.serverLevel(), path);
            for (Goal goal : merit.goals()) {
                if (!goal.accepts(event)) continue;
                var meritProgress = progress.inProgress.get(path);
                int xp = goal.calcXp(event, merit);
                PlayerOverlayPatch.enqueueXpNotification(path, xp);
                meritProgress.add(xp);
                isDirty = true;
                if (merit.xp() <= meritProgress.getXp()) {
                    progress.awardMerit(path, player, merit);
                }
            }
        }

        if(isDirty) player.setData(DataAttachments.PROFESSION_PROGRESS, progress);
    }

    public static void refreshPlayerMerits(ServerPlayer player) {
        var merits = Profession.allMerits(player.serverLevel());
        var progress = player.getData(DataAttachments.PROFESSION_PROGRESS);
        for(var meritEntry : merits.entrySet()) {
            if(progress.canAdd(meritEntry.getKey()) && meritEntry.getValue().prereqs().stream().allMatch(progress::isDone)) {
                progress.inProgress.put(meritEntry.getKey(), new MeritProgress());
            }
        }
        player.setData(DataAttachments.PROFESSION_PROGRESS, progress);
    }

    public static ProfessionProgress progressOf(ServerPlayer player) {
        return player.getData(DataAttachments.PROFESSION_PROGRESS);
    }

    public boolean isInProgress(Merit.Path path) {
        return inProgress.containsKey(path);
    }

    public boolean isDone(Merit.Path path) {
        return done.contains(path);
    }

    public boolean canAdd(Merit.Path path) {
        return !inProgress.containsKey(path) && !done.contains(path);
    }

    public MeritProgress getInProgress(Merit.Path path) {
        return this.inProgress.get(path);
    }

    public void clearMeritProgress(Merit.Path path) {
        this.inProgress.remove(path);
        this.done.remove(path);
    }

    public void clearAllMeritProgress() {
        this.inProgress.clear();
        this.done.clear();
    }

    public Map<Merit.Path, MeritProgress> progress() {
        return inProgress;
    }

    public Set<Merit.Path> done() {
        return done;
    }

    public List<Merit.Path> doneList() {
        return new ArrayList<>(done);
    }

    public List<Merit.Path> all() {
        List<Merit.Path> paths = new ArrayList<>();
        paths.addAll(inProgress.keySet());
        paths.addAll(done);
        return paths;
    }

    public void awardMerit(Merit.Path meritPath, ServerPlayer player) {
        this.awardMerit(meritPath, player, false);
    }

    public void awardMerit(Merit.Path meritPath, ServerPlayer player, boolean isFake) {
        if (!isFake) {
            this.done.add(meritPath);
            this.inProgress.remove(meritPath);
            ProfessionProgress.refreshPlayerMerits(player);
        }

        PlayerOverlayPatch.enqueueMeritCompleteNotification(meritPath);
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

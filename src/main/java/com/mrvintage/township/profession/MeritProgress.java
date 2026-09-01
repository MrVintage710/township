package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MeritProgress {

    public static final Codec<MeritProgress> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("xp").forGetter(MeritProgress::getXp),
            Codec.INT.fieldOf("targetXp").forGetter(MeritProgress::getTagetXp),
            Codec.INT.fieldOf("successes").forGetter(MeritProgress::getSuccesses)
        ).apply(instance, MeritProgress::new)
    );

    private int xp = 0;
    private final int tagetXp;

    private int successes = 0;

    public MeritProgress(int xp, int targetXp, int successes) {
        this.xp = xp;
        this.tagetXp = targetXp;
        this.successes = successes;
    }

    public MeritProgress(int targetXp) {
        this.xp = 0;
        this.tagetXp = targetXp;
        this.successes = 0;
    }

    public int getXp() {
        return xp;
    }

    public int getTagetXp() {
        return tagetXp;
    }

    public boolean isDone() {
        return this.xp >= this.tagetXp;
    }

    public MeritProgress markDone() {
        this.xp = this.tagetXp;
        return this;
    }

    public int getSuccesses() {
        return successes;
    }

    public MeritProgress setXp(int xp) {
        this.xp = xp;
        return this;
    }

    public MeritProgress setSuccesses(int successes) {
        this.successes = successes;
        return this;
    }

    public MeritProgress add(int xp) {
        this.xp += xp;
        return this;
    }

    @Override
    public String toString() {
        return "{ xp: " + xp + "}";
    }
}

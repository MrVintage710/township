package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MeritProgress {

    public static final Codec<MeritProgress> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("xp").forGetter(MeritProgress::getXp),
            Codec.INT.fieldOf("successes").forGetter(MeritProgress::getSuccesses)
        ).apply(instance, MeritProgress::new)
    );

    private int xp = 0;

    private int successes = 0;

    public MeritProgress(int xp, int successes) {
        this.xp = xp;
        this.successes = successes;
    }

    public MeritProgress() {
        this.xp = 0;
        this.successes = 0;
    }

    public int getXp() {
        return xp;
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

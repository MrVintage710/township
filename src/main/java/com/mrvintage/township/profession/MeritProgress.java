package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MeritProgress {

    public static final Codec<MeritProgress> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Merit.Path.CODEC.fieldOf("path").forGetter(MeritProgress::getMerit),
            Codec.INT.fieldOf("xp").forGetter(MeritProgress::getXp),
            Codec.INT.fieldOf("successes").forGetter(MeritProgress::getSuccesses)
        ).apply(instance, MeritProgress::new)
    );

    private final Merit.Path merit;

    private int xp = 0;

    private int successes = 0;


    public MeritProgress(Merit.Path merit) {
        this.merit = merit;
    }

    public MeritProgress(Merit.Path merit, int xp, int successes) {
        this.merit = merit;
        this.xp = xp;
        this.successes = successes;
    }

    public Merit.Path getMerit() {
        return merit;
    }

    public int getXp() {
        return xp;
    }

    public int getSuccesses() {
        return successes;
    }
}

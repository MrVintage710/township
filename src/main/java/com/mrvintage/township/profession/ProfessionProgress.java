package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public record ProfessionProgress(Map<Merit.Path, MeritProgress> progress) {
    public static final Codec<ProfessionProgress> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.unboundedMap(Merit.Path.CODEC, MeritProgress.CODEC).fieldOf("progress").forGetter(ProfessionProgress::progress)
        ).apply(instance, ProfessionProgress::new)
    );

    public ProfessionProgress(Map<Merit.Path, MeritProgress> progress) {
        this.progress = progress;
    }

    public ProfessionProgress() {
        this(new HashMap<>());
    }
}

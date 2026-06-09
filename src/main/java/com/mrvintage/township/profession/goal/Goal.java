package com.mrvintage.township.profession.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mrvintage.township.profession.Merit;
import net.neoforged.bus.api.Event;

import java.util.function.Function;

public interface Goal<T extends Event> {

    Codec<Goal<?>> CODEC = Goals.DISPATCH.byNameCodec().dispatch(Goal::type, Function.identity());

    MapCodec<? extends Goal<?>> type();

    int calcXp(T event, Merit merit);

    boolean accepts(Event event);


}

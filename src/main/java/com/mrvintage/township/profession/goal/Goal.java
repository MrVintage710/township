package com.mrvintage.township.profession.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;

import java.util.function.Function;

public interface Goal {

    Codec<Goal> CODEC = Goals.DISPATCH.byNameCodec().dispatch(Goal::type, Function.identity());

    MapCodec<? extends Goal> type();

    int calcXp(Event event);

    boolean accepts(Event event);


}

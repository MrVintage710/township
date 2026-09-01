package com.mrvintage.township.profession.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.function.Function;

public interface Reward {

    Codec<Reward> CODEC = Rewards.DISPATCH.byNameCodec().dispatch(Reward::type, Function.identity());

    void rewardPlayer(ServerPlayer player);

    default Optional<Component> getDescription(){
        return Optional.empty();
    }

    MapCodec<? extends Reward> type();
}

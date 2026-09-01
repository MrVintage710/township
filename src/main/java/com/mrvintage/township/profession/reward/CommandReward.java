package com.mrvintage.township.profession.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public record CommandReward(String command, Optional<Component> rewardMessage) implements Reward {

    public static final MapCodec<CommandReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.STRING.fieldOf("command").forGetter(CommandReward::command),
            ComponentSerialization.CODEC.optionalFieldOf("rewardMessage").forGetter(CommandReward::rewardMessage)
        ).apply(instance, CommandReward::new)
    );

    @Override
    public void rewardPlayer(ServerPlayer player) {
        player.server.getCommands().performPrefixedCommand(
            player.server.createCommandSourceStack(),
            command
        );
    }

    @Override
    public Optional<Component> getDescription() {
        return this.rewardMessage;
    }

    @Override
    public MapCodec<? extends Reward> type() {
        return CODEC;
    }
}

package com.mrvintage.township.profession.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

public record CommandReward(String command) implements Reward {

    public static final MapCodec<CommandReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.STRING.fieldOf("command").forGetter(CommandReward::command)
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
    public MapCodec<? extends Reward> type() {
        return CODEC;
    }
}

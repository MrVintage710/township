package com.mrvintage.township.profession.reward;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;

public class ProgressionReward implements Reward {



    @Override
    public void rewardPlayer(ServerPlayer player) {

    }

    @Override
    public MapCodec<? extends Reward> type() {
        return null;
    }
}

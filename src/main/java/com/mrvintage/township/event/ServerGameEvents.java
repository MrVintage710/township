package com.mrvintage.township.event;

import com.mrvintage.township.Township;
import com.mrvintage.township.profession.*;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.profession.goal.Goals;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class ServerGameEvents {

    public void onBlockBreak(BlockEvent.BreakEvent event) {

    }

    @SubscribeEvent
    public void onDealDamage(LivingDamageEvent.Post event) {
        if(event.getSource().getEntity() instanceof ServerPlayer player) {
            ProfessionProgress.incrementProgress(player, event);
        }
    }

    @SubscribeEvent
    public void playerEntersEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            ProfessionProgress.refreshPlayerMerits(player);
        }
    }

}

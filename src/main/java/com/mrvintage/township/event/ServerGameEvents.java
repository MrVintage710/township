package com.mrvintage.township.event;

import com.mojang.brigadier.tree.CommandNode;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class ServerGameEvents {

    public void onBlockBreak(BlockEvent.BreakEvent event) {

    }

    @SubscribeEvent
    public void onDealDamage(LivingDamageEvent.Post event) {
        var location = ResourceLocation.fromNamespaceAndPath(Township.MODID, "block");
    }

    @SubscribeEvent
    public void playerEntersEvent(PlayerEvent.PlayerLoggedInEvent event) {

    }

}

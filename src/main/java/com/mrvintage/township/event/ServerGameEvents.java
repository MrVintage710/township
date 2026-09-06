package com.mrvintage.township.event;

import com.mrvintage.township.Township;
import com.mrvintage.township.lock.LockRegistry;
import com.mrvintage.township.profession.*;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.profession.goal.Goals;
import com.mrvintage.township.profession.reward.ItemCraftReward;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class ServerGameEvents {

    public void onBlockBreak(BlockEvent.BreakEvent event) {

    }

    @SubscribeEvent
    public void onDealDamage(LivingDamageEvent.Post event) {
        if(event.getSource().getEntity() instanceof ServerPlayer player) {
            player.server.execute(() -> ProfessionProgress.incrementProgress(player, event));
        }
    }

    @SubscribeEvent
    public void playerEntersEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            ProfessionProgress.refreshPlayerMerits(player);
            player.getRecipeBook().removeRecipes(List.of(player.server.getRecipeManager().byKey(ResourceLocation.withDefaultNamespace("stick")).get()), player);
        }
    }

    @SubscribeEvent
    public void onGameAboutToStartEvent(ServerAboutToStartEvent event) {

        HashMap<Merit.Path, ExtraCodecs.TagOrElementLocation> itemCraftRewards = new HashMap<>();

        for(var profession : event.getServer().registryAccess().registry(Profession.REGISTRY_KEY).get().entrySet()) {
            for(var speciality : profession.getValue().specialties().entrySet()) {
                for(var merit : speciality.getValue().merits().entrySet()) {
                    for(var reward : merit.getValue().rewards()) {
                        if (reward instanceof ItemCraftReward(List<ExtraCodecs.TagOrElementLocation> items)) {
                            for(var item : items) {
                                Merit.Path path = new Merit.Path(profession.getKey().location(), speciality.getKey(), merit.getKey());
                                itemCraftRewards.put(path, item);
                            }
                        }
                    }
                }
            }
        }

        for(var entry : itemCraftRewards.entrySet()) {
            if(entry.getValue().tag()) {
                TagKey<Item> itemTag = TagKey.create(Registries.ITEM, entry.getValue().id());
                BuiltInRegistries.ITEM.getTag(itemTag).ifPresent(tag -> {
                    for(var item : tag) {
                        LockRegistry.getInstance().addCraftingLock(item.value(), entry.getKey());
                    }
                });
            } else {
                Item item = BuiltInRegistries.ITEM.get(entry.getValue().id());
                LockRegistry.getInstance().addCraftingLock(item, entry.getKey());
            }
        }
    }
}

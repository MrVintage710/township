package com.mrvintage.township.profession.reward;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemCraftReward(List<ExtraCodecs.TagOrElementLocation> items) implements Reward {

    public static final MapCodec<ItemCraftReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.either(
                ExtraCodecs.TAG_OR_ELEMENT_ID,
                ExtraCodecs.TAG_OR_ELEMENT_ID.listOf()
            ).xmap(
                either -> either.map(List::of, lists -> lists),
                list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list)
            ).orElse(new ArrayList<>()).fieldOf("items").forGetter(ItemCraftReward::items)
        ).apply(instance, ItemCraftReward::new)
    );

    @Override
    public Optional<Component> getDescription() {
        return Optional.of(Component.literal("Ability to craft: "));
    }

    @Override
    public void rewardPlayer(ServerPlayer player) {
        var items = Util.getItemsFromList(this.items);
        var recipes = player.server.getRecipeManager().getRecipes().stream().filter(recipeHolder -> {
            var result = recipeHolder.value().getResultItem(player.server.registryAccess());
            return items.contains(result.getItem());
        }).toList();
        if(!recipes.isEmpty()) player.awardRecipes(recipes);
    }

    @Override
    public Optional<List<ResourceLocation>> renderItems() {
        return Optional.of(Util.getItemIdsFromList(this.items));
    }

    @Override
    public MapCodec<? extends Reward> type() {
        return null;
    }
}

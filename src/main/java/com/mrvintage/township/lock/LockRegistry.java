package com.mrvintage.township.lock;

import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.ProfessionProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class LockRegistry {
    private static final LockRegistry INSTANCE = new LockRegistry();

    //This is a map of item -> merit that shows what merit needs to be acquired to do specific crafting. If the craft's
    //result is the item in the table, and they don't have the merit specified, then refuse to craft.
    private final HashMap<ResourceLocation, Merit.Path> restrictedCrafts = new HashMap<>();

    public static LockRegistry getInstance() {
        return INSTANCE;
    }

    public void addCraftingLock(Item item, Merit.Path merit) {
        var id = BuiltInRegistries.ITEM.getKey(item);
        this.restrictedCrafts.put(id, merit);
    }

    public void addCraftingLock(ResourceLocation id, Merit.Path merit) {
        this.restrictedCrafts.put(id, merit);
    }

    public boolean canPlayerCraft(ServerPlayer player, Item item) {
        if(item == null) return true;
        var id = BuiltInRegistries.ITEM.getKey(item);
        var meritPath = Optional.ofNullable(this.restrictedCrafts.get(id));
        return meritPath.map(path -> ProfessionProgress.hasCompleted(player, path)).orElse(true);
    }

    public boolean canPlayerCraft(MinecraftServer server, UUID playerID, Item item) {
        if(item == null) return true;
        var id = BuiltInRegistries.ITEM.getKey(item);
        var meritPath = Optional.ofNullable(this.restrictedCrafts.get(id));
        return meritPath.map(path -> ProfessionProgress.hasCompleted(server, playerID, path)).orElse(true);
    }

    public boolean canPlayerCraft(ServerPlayer player, RecipeHolder<?> recipeHolder) {
        if(recipeHolder == null) return true;
        ItemStack result = recipeHolder.value().getResultItem(player.server.registryAccess());
        return this.canPlayerCraft(player, result.getItem());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canPlayerCraft(RecipeHolder<?> recipe) {
        if(recipe == null) return true;

        var meritPath = Optional.ofNullable(this.restrictedCrafts.get(recipe.id()));
        return meritPath.map(ProfessionProgress::hasCompleted).orElse(true);
    }
}

package com.mrvintage.township.mixins;

import com.mrvintage.township.Township;
import com.mrvintage.township.lock.LockRegistry;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

    @Shadow @Final private Player player;
    @Shadow @Final private ResultContainer resultSlots;
    @Shadow @Final private CraftingContainer craftSlots;

    @Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
    private static void township$onSlotChangedCraftingGrid(
        net.minecraft.world.inventory.AbstractContainerMenu menu,
        Level level,
        Player player,
        CraftingContainer craftSlots,
        ResultContainer resultSlots,
        RecipeHolder<CraftingRecipe> recipe,
        CallbackInfo ci
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if(player.isCreative()) return;

        RecipeHolder<?> matchedRecipeId = null;
        if (recipe != null) {
            matchedRecipeId = recipe;
        } else {
            // Vanilla stores the matched recipe in ResultContainer.recipeUsed during
            // slotChangedCraftingGrid — by TAIL it's always set if a recipe matched.
            RecipeHolder<?> storedRecipe = resultSlots.getRecipeUsed();
            if (storedRecipe != null) {
                matchedRecipeId = storedRecipe;
            }
        }

        if(!LockRegistry.getInstance().canPlayerCraft(serverPlayer, matchedRecipeId)) {
            clearResultAndSync(menu, resultSlots, serverPlayer);
        }
    }

    /**
     * Clears the result slot on the server AND sends a packet to the client so
     * the empty slot is reflected immediately (vanilla already sent the non-empty
     * packet before our TAIL injection runs).
     */
    private static void clearResultAndSync(
        net.minecraft.world.inventory.AbstractContainerMenu menu,
        ResultContainer resultSlots,
        ServerPlayer serverPlayer
    ) {
        resultSlots.setItem(0, ItemStack.EMPTY);
//        menu.setRemoteSlot(0, ItemStack.EMPTY);
        serverPlayer.connection.send(
            new ClientboundContainerSetSlotPacket(
                menu.containerId, menu.incrementStateId(), 0, ItemStack.EMPTY
            )
        );
    }
}

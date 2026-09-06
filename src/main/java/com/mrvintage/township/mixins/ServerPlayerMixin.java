package com.mrvintage.township.mixins;

import com.mrvintage.township.lock.LockRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Shadow
    private ServerRecipeBook recipeBook;

    @Inject(method = "awardRecipes", at = @At("HEAD"), cancellable = true)
    public void township$awardRecipes(Collection<RecipeHolder<?>> recipes, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer serverPlayer = (ServerPlayer)(Object) this;

        var filteredRecipes = recipes.stream()
            .filter(recipeHolder -> LockRegistry.getInstance().canPlayerCraft(serverPlayer, recipeHolder))
            .toList();

        cir.setReturnValue(this.recipeBook.addRecipes(filteredRecipes, serverPlayer));
    }
}

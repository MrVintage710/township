package com.mrvintage.township.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class MeritServerPlayerMixin {
    @Shadow
    public abstract void sendSystemMessage(Component component);

    @Inject(method = "awardRecipes", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipes(Collection<RecipeHolder<?>> holders, @NotNull CallbackInfoReturnable<Integer> cir) {
        this.sendSystemMessage(Component.literal("NOPE"));
        cir.setReturnValue(0);
    }

    @Inject(method = "awardRecipesByKey", at = @At("HEAD"), cancellable = true)
    public void onAwardRecipesByKey(List<ResourceLocation> recipes, CallbackInfo ci) {
        ci.cancel();
    }
}

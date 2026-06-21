package com.mrvintage.township.mixin;

import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBook.class)
public class MeritRecipeBookMixin {

    @Inject(method = "copyOverData", at = @At("HEAD"), cancellable = true)
    public void onCopy(RecipeBook other, CallbackInfo ci) {
        ci.cancel();
    }
}

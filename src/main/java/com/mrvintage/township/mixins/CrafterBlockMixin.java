package com.mrvintage.township.mixins;

import com.mrvintage.township.lock.LockRegistry;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.util.TracksPlacer;
import com.mrvintage.township.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

    @Inject(method = "setPlacedBy", at = @At("TAIL"))
    public void township$newBlockEntity(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (!level.isClientSide) {
            if (placer instanceof ServerPlayer player) {
                BlockEntity blockentity = level.getBlockEntity(pos);
                if (blockentity instanceof CrafterBlockEntity crafterBlockEntity) {
                    ((TracksPlacer) crafterBlockEntity).setPlacer(player);
                }
            }
        }
    }

    @Inject(method = "dispenseItem", at = @At("HEAD"), cancellable = true)
    public void township$dispenseItem(ServerLevel level, BlockPos pos, CrafterBlockEntity crafter, ItemStack stack, BlockState state, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        if(!level.isClientSide) {
            if(!LockRegistry.getInstance().canPlayerCraft(level.getServer(), ((TracksPlacer) crafter).getPlacer(), stack.getItem())) {
                ci.cancel();
            }
        }
    }

}

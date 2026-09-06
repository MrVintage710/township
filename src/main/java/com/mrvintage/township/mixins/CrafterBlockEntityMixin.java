package com.mrvintage.township.mixins;

import com.mrvintage.township.util.TracksPlacer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CrafterBlockEntity.class)
public abstract class CrafterBlockEntityMixin implements TracksPlacer {

    @Unique
    public UUID township$placedBy;


    @Override
    public void setPlacer(ServerPlayer player) {
        this.township$placedBy = player.getUUID();
    }

    @Override
    public UUID getPlacer() {
        return this.township$placedBy;
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void township$saveAdditional(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if(this.township$placedBy != null) {
            tag.putUUID("placedBy", this.township$placedBy);
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    public void township$loadAdditional(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if(tag.contains("placedBy")) {
            this.township$placedBy = tag.getUUID("placedBy");
        }
    }
}

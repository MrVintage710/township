package com.mrvintage.township.networking;

import com.mrvintage.township.Township;
import com.mrvintage.township.lock.LockRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateLocks(LockRegistry registry) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateLocks> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Township.MODID, "update_locks"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return UpdateLocks.TYPE;
    }
}

package com.mrvintage.township.util;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface TracksPlacer {

    void setPlacer(ServerPlayer player);

    UUID getPlacer();
}

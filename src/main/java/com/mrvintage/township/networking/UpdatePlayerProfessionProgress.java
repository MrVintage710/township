package com.mrvintage.township.networking;

import com.mrvintage.township.Township;
import com.mrvintage.township.profession.ProfessionProgress;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdatePlayerProfessionProgress(ProfessionProgress progress) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdatePlayerProfessionProgress> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Township.MODID, "update_player_profession_progress"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, UpdatePlayerProfessionProgress> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.fromCodec(ProfessionProgress.CODEC),
        UpdatePlayerProfessionProgress::progress,
        UpdatePlayerProfessionProgress::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return UpdatePlayerProfessionProgress.TYPE;
    }

    public static void handle(UpdatePlayerProfessionProgress packet, final IPayloadContext context) {
        ProfessionProgress.ClientProfessionProgress = packet.progress;
    }
}

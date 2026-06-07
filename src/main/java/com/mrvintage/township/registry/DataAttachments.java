package com.mrvintage.township.registry;

import com.mrvintage.township.Township;
import com.mrvintage.township.profession.ProfessionProgress;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            Township.MODID
    );

    public static final Supplier<AttachmentType<ProfessionProgress>> PROFESSION_PROGRESS = DataAttachments.ATTACHMENT_TYPES.register(
        "profession_progress",
        () -> AttachmentType.builder(() -> new ProfessionProgress()).serialize(ProfessionProgress.CODEC).copyOnDeath().build()
    );

    public static ProfessionProgress getProfessionProgress(ServerPlayer player) {
        ProfessionProgress progress = player.getData(PROFESSION_PROGRESS);

    }
}

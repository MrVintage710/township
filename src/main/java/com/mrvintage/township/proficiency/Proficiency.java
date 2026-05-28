package com.mrvintage.township.proficiency;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class Proficiency implements INBTSerializable {



    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {

    }
}

package com.mrvintage.township.util;

import com.mrvintage.township.Township;
import com.mrvintage.township.profession.ProfessionProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Util {

    public static List<ResourceLocation> getItemIdsFromList(List<ExtraCodecs.TagOrElementLocation> tags) {
        List<ResourceLocation> items = new ArrayList<>();
        for(var tag : tags) {
            if(tag.tag()) {
                TagKey<Item> itemTag = TagKey.create(Registries.ITEM, tag.id());
                BuiltInRegistries.ITEM.getTag(itemTag).ifPresent(itemList -> {
                    for(var item : itemList) {
                        items.add(item.getKey().location());
                    }
                });
            } else {
                items.add(tag.id());
            }
        }
        return items;
    }

    public static List<Item> getItemsFromList(List<ExtraCodecs.TagOrElementLocation> tags) {
        List<Item> items = new ArrayList<>();
        for(var tag : tags) {
            if(tag.tag()) {
                TagKey<Item> itemTag = TagKey.create(Registries.ITEM, tag.id());
                BuiltInRegistries.ITEM.getTag(itemTag).ifPresent(itemList -> {
                    for(var item : itemList) {
                        items.add(item.value());
                    }
                });
            } else {
                items.add(BuiltInRegistries.ITEM.get(tag.id()));
            }
        }
        return items;
    }
}

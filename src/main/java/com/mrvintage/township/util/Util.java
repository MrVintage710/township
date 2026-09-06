package com.mrvintage.township.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

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

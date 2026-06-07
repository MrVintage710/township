package com.mrvintage.township;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TownshipTags {

    public static class Blocks {
        public static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Township.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ARCHERY_WEAPONS = createTag("archery_weapons");

        public static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Township.MODID, name));
        }
    }
}

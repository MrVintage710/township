package com.mrvintage.township.ui.nodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemArrayNode extends Node {

    private List<Item> items = new ArrayList<>();
    private int minGap = 2;

    public ItemArrayNode() {
        this.withHeight(Unit.auto());
    }

    public ItemArrayNode withItems(List<ResourceLocation> items) {
        this.items = items.stream().map(BuiltInRegistries.ITEM::get).toList();
        return this;
    }

    private int numberOfItemsPerRow() {
        return (int) Math.ceil((float) (16 + this.minGap) / (float) this.width());
    }

    @Override
    public int contentHeight(int basis) {
        var numberOfItemsPerRow =  this.numberOfItemsPerRow();
        var numberOfLines = (int) Math.ceil((float) numberOfItemsPerRow / (float) this.items.size());
        return Math.max(super.contentHeight(basis), numberOfLines * 18);
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        var width = this.width();
        var numberOfItemsPerRow =  this.numberOfItemsPerRow();
        var currentItemIndex = 0;
        for(var item : this.items) {
            int x_index = currentItemIndex /  numberOfItemsPerRow;
            int y_index = currentItemIndex % numberOfItemsPerRow;

            int x = (x_index * (16 + this.minGap));
            int y = (y_index * 18);

            guiGraphics.renderItem(new ItemStack(item), this.x() + x, this.y() + y);

            currentItemIndex++;
        }

    }
}

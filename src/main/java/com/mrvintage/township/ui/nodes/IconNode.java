package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.ui.BlitSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IconNode extends Node {

    private ResourceLocation icon;
    private int w = 16, h = 16;

    public IconNode(ResourceLocation icon) {
        this.icon = icon;
    }

    public IconNode() {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if(icon == null) return;
        if(BuiltInRegistries.ITEM.containsKey(icon)) {
            Item item = BuiltInRegistries.ITEM.get(this.icon);
            guiGraphics.renderItem(new ItemStack(item), this.x(), this.y());
        } else {
            guiGraphics.blit(icon, this.x(), this.y(), this.width(), this.height(), 0, 0, w, h, w, h);
        }
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }
}

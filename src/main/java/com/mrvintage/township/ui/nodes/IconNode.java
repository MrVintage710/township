package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.ui.BlitSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IconNode extends Node {

    private ResourceLocation icon;
    private int x = 0, y = 0, w = 16, h = 16;

    public IconNode(ResourceLocation icon) {
        this.icon = icon;
    }

    public IconNode() {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if(icon == null) return;
        if(BuiltInRegistries.ITEM.containsKey(icon)) {
            Item item = BuiltInRegistries.ITEM.get(this.icon);
            guiGraphics.renderItem(new ItemStack(item), this.x(guiGraphics.guiWidth()), this.y(guiGraphics.guiHeight()));
        } else {
            guiGraphics.blit(icon, x, y, w, h, 0, 0, w, h);
        }
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}

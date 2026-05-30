package com.mrvintage.township.ui.widgets;

import com.mrvintage.township.Township;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record BlitSprite(ResourceLocation sprite, int x, int y, int width, int height, int sprite_width, int sprite_height) {
    public void blit(GuiGraphics graphics, int x, int y) {
        graphics.blit(this.sprite, x, y, this.width, this.height, this.x, this.y, this.width, this.height, this.sprite_width, this.sprite_height);
    }

    public void blit(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blit(this.sprite, x, y, width, height, this.x, this.y, this.width, this.height, this.sprite_width, this.sprite_height);
    }
}

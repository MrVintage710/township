package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.ui.Sprites;
import net.minecraft.client.gui.GuiGraphics;

public class ProgressNode extends Node {

    private float progress;

    public ProgressNode setProgress(float progress) {
        this.progress = progress;
        return this;
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Sprites.PROGRESS_BAR_EMPTY.blit(guiGraphics, this.x(), this.y(), this.width(), 5);
        Sprites.PROGRESS_BAR_FULL.blit(guiGraphics, this.x(), this.y(), Math.round(this.width() * this.progress), 5);
    }
}

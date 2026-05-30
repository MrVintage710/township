package com.mrvintage.township.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrvintage.township.ui.BlitSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class TownshipButton extends AbstractWidget {

    protected final BlitSprite sprite;
    protected final BlitSprite hoverSprite;

    public abstract void onPress();

    public TownshipButton(int x, int y, int width, int height, Component message, BlitSprite sprite, BlitSprite hoverSprite) {
        super(x, y, width, height, message);
        this.sprite = sprite;
        this.hoverSprite = hoverSprite;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int x, int y, float v) {
        Minecraft minecraft = Minecraft.getInstance();
        BlitSprite sprite = this.isHovered() ? this.hoverSprite : this.sprite;
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        sprite.blit(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.getFGColor();
        this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.active && this.visible) {
            if (CommonInputs.selected(keyCode)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        this.onPress();
        super.onClick(mouseX, mouseY, button);
    }

    private void renderString(GuiGraphics guiGraphics, Font font, int color) {
        this.renderScrollingString(guiGraphics, font, 2, color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
        narrationElementOutput.add(NarratedElementType.USAGE, "Button");
    }
}

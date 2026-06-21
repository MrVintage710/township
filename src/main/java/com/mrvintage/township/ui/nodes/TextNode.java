package com.mrvintage.township.ui.nodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextNode extends Node {

    private Component textComponent;
    private Font font;
    private boolean dropShadow = false;

    public TextNode(String text) {
        this(Component.literal(text));
    }

    public TextNode(Component textComponent) {
        this.textComponent = textComponent;
        this.font = Minecraft.getInstance().font;
    }

    public Component getTextComponent() {
        return textComponent;
    }

    public void setTextComponent(Component textComponent) {
        this.textComponent = textComponent;
        this.layout();
    }

    public TextNode shadowed() {
        this.dropShadow = true;
        return this;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.drawString(this.font, this.textComponent, this.x(), this.y(), 0x0, this.dropShadow);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}

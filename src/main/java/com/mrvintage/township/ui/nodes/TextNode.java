package com.mrvintage.township.ui.nodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class TextNode extends Node{

    public enum Behavior {
        Wrap,
        Pan
    }

    private Component text;
    private float scale = 1.0f;

    private NodeUi.VerticalAlign verticalAlign = NodeUi.VerticalAlign.TOP;
    private NodeUi.HorizontalAlign horizontalAlign = NodeUi.HorizontalAlign.LEFT;

    private int color = 0x000000;

    private Behavior behavior = Behavior.Wrap;

    public TextNode(String text) {
        this.text = Component.literal(text);
    }

    public TextNode(Component text) {
        this.text = text;
    }

    public TextNode() {
        this.text = Component.empty();
    }

    public TextNode withScale(float scale) {
        this.scale = scale;
        return this;
    }

    public TextNode withVerticalAlign(NodeUi.VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
        return this;
    }

    public TextNode withHorizontalAlign(NodeUi.HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
        return this;
    }

    public TextNode withColor(int color) {
        this.color = color;
        return this;
    }

    public TextNode withText(String text) {
        this.text = Component.literal(text);
        return this;
    }

    public TextNode withText(Component text) {
        this.text = text;
        return this;
    }

    @Override
    protected int contentHeight(int basis) {
        Font font = Minecraft.getInstance().font;
        int textWidth = Math.min( this.width(), font.width( this.text));
        int textHeight = font.wordWrapHeight( this.text.getString(), textWidth );

        return Math.max(super.contentHeight(basis), textHeight) ;
    }

    public int fitHeight() {
        return Minecraft.getInstance().font.wordWrapHeight(this.text, this.width());
    }

    public TextNode withBehavior(Behavior behavior) {
        this.behavior = behavior;
        return this;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.text.getString().isEmpty()) { super.render(guiGraphics, mouseX, mouseY, delta); return; }
        Font font = Minecraft.getInstance().font;

        int textWidth = Math.min( this.width(), font.width( this.text.getString()) + 6);
        int textHeight = Math.min( this.height() , font.wordWrapHeight( this.text.getString(), textWidth ));

        int originX = 0, originY = 0;

        switch (this.horizontalAlign) {
            case CENTER -> originX = (this.width() / 2 - textWidth / 2);
            case RIGHT -> originX = (this.width() - textWidth);
        }

        switch (this.verticalAlign) {
            case CENTER -> originY = (this.height() / 2 - textHeight / 2);
            case BOTTOM -> originY = (this.height() - textHeight);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(this.scale, this.scale, 1.0f);
        switch (this.behavior) {
            case Pan -> guiGraphics.drawString(Minecraft.getInstance().font,
                this.text,
                (int) ((float) (this.x() + originX + 1) / this.scale),
                (int) ((float) (this.y() + originY + 1) / this.scale),
                this.color,
                false
            );
            case Wrap -> guiGraphics.drawWordWrap(Minecraft.getInstance().font,
                this.text,
                (int) ((float) (this.x() + originX + 1) / this.scale),
                (int) ((float) (this.y() + originY + 1) / this.scale),
                (int) ((float) textWidth / this.scale),
                this.color
            );
        }
        guiGraphics.pose().popPose();
    }
}

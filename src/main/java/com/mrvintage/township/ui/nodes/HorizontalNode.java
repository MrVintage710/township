package com.mrvintage.township.ui.nodes;

import net.minecraft.client.gui.GuiGraphics;

public class HorizontalNode extends Node {

    private int gap = 1;

    private NodeUi.VerticalAlign align = NodeUi.VerticalAlign.TOP;

    public HorizontalNode() {
        this.withClip();
    }

    public HorizontalNode withGap(int gap) {
        this.gap = gap;
        return this;
    }

    public HorizontalNode withAlign(NodeUi.VerticalAlign align) {
        this.align = align;
        return this;
    }

    @Override
    public void layout() {
        int total = 0;
        for (Node child : this.children) {
            child.setX(total);
            total += child.width() + this.gap;
        }
        this.withWidth(total - this.gap);
        super.layout();
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {}
}

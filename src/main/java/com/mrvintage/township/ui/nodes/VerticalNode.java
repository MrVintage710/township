package com.mrvintage.township.ui.nodes;

import net.minecraft.client.gui.GuiGraphics;

public class VerticalNode extends Node {

    private int gap = 1;
    private NodeUi.HorizontalAlign align = NodeUi.HorizontalAlign.LEFT;

    public VerticalNode() {
        this.withClip();
    }

    public VerticalNode withGap(int gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public void layout() {
        int total = 0;
        for (Node child : this.children) {
            child.setY(total);
            total += child.height() + this.gap;

            switch (align) {
                case RIGHT -> child.setX(this.right() - child.width());
                case CENTER -> {
                }
            }
        }
        this.withHeight(total - this.gap);
        super.layout();
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {}
}

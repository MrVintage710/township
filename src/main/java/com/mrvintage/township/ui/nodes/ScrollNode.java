package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.Sprites;
import net.minecraft.client.gui.GuiGraphics;

public class ScrollNode extends Node {


    private int contentBottom;
    private boolean alwaysShowVerticalScrollBar = false;

    private BlitSprite handleSprite = Sprites.PARCHMENT_SCROLL_HANDLE;
    private BlitSprite barSprite = Sprites.PARCHMENT_SCROLL;

    public ScrollNode() {
        this.withClip();
    }

    public boolean shouldScrollVertically() {
        return (this.height() < contentBottom) || alwaysShowVerticalScrollBar;
    }

    public ScrollNode withScrollY(int scrollY) {
        this.scrollY = Math.min(scrollY, this.maxScrollY());
        return this;
    }

    public ScrollNode withVerticalScrollbarShown() {
        this.alwaysShowVerticalScrollBar = true;
        return this;
    }

    public ScrollNode withHandleSprite(BlitSprite handleSprite) {
        this.handleSprite = handleSprite;
        return this;
    }

    public ScrollNode withBarSprite(BlitSprite barSprite) {
        this.barSprite = barSprite;
        return this;
    }

    public void scrollY(int scroll) {
        this.scrollY = Math.min(this.maxScrollY(), this.scrollY + scroll);
    }

    private int maxScrollY() {
        return Math.max(0, contentBottom - this.height());
    }

    private float scrollPercentY() {
        return (float) this.scrollY / (float) this.maxScrollY();
    }

    @Override
    public void layout() {
        super.layout();
        contentBottom = this.contentHeight(this.height());
    }

    @Override
    public int getHorizontalBasis(int defaultBasis) {
        int modifier = this.shouldScrollVertically() ? handleSprite.width() + 3 : 0;
        return super.getHorizontalBasis(defaultBasis) - modifier;
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.shouldScrollVertically()) {
            this.barSprite.blit(guiGraphics, this.right() - this.barSprite.width(), this.y(), this.barSprite.width(), this.getVerticalBasis());
            this.handleSprite.blit(guiGraphics, this.right() - this.handleSprite.width(), Math.round(this.y() * this.scrollPercentY()));
        }
    }
}

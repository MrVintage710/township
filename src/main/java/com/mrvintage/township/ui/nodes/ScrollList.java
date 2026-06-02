package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.Sprites;
import net.minecraft.client.gui.GuiGraphics;

public class ScrollList extends Node {

    private int gap = 1;
    private int scroll = 0;

    private boolean isHorizontal = false;

    private final BlitSprite scrollSprite = Sprites.PARCHMENT_SCROLL;

    private final BlitSprite scrollHandleSprite = Sprites.PARCHMENT_SCROLL_HANDLE;

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.enableScissor(this.x(), this.y(),  this.x() + this.width(), this.y() + this.height());
        scrollSprite.blit(guiGraphics, this.right() - scrollSprite.width(), this.y(), scrollSprite.width(), this.height());
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.disableScissor();
    }

    @Override
    public void layout() {
        int basis = 0;
        for (Node child : children) {
            if (this.isHorizontal) {
                child.setX(Unit.px(scroll + basis));
                basis += child.width() + this.gap;
            } else {
                child.setY(Unit.px(scroll + basis));
                basis += child.height() + this.gap;
            }
            child.layout();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll += scrollY;
        this.layout();
        return true;
    }

    @Override
    public int getHorizontalBasis() {
        int scrollBarSize = this.isHorizontal ? 0 : Integer.max(scrollSprite.width(), scrollHandleSprite.width());
        return super.getHorizontalBasis() - scrollBarSize;
    }
}

package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.BlitSpriteScaling;
import net.minecraft.client.gui.GuiGraphics;

public class BlitSpriteNode extends Node {

    private final BlitSprite blitSprite;

    private boolean centered = false;

    public BlitSpriteNode(BlitSprite blitSprite) {
        this.blitSprite = blitSprite;
        if (blitSprite.scaling().type() == BlitSpriteScaling.Type.STRETCH) {
            this.withWidth(Unit.px(blitSprite.width()));
            this.withHeight(Unit.px(blitSprite.height()));
        }
    }

    public BlitSpriteNode centered() {
        this.centered = true;
        return this;
    }

    @Override
    public void layout() {
        if (this.centered) {
            this.setX(Unit.px((this.parentWidth() - blitSprite.width()) / 2));
            this.setY(Unit.px((this.parentHeight() - blitSprite.height()) / 2));
        }
        super.layout();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Township.LOGGER.info("{}", this.children);
        blitSprite.blit(guiGraphics, this.x(), this.y(), this.width(), this.height());
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}

package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.BlitSpriteScaling;
import net.minecraft.client.gui.GuiGraphics;

public class BlitSpriteNode extends Node {

    private final BlitSprite blitSprite;

    public BlitSpriteNode(BlitSprite blitSprite) {
        this.blitSprite = blitSprite;
        if (blitSprite.scaling().type() == BlitSpriteScaling.Type.STRETCH) {
            this.setWidth(Unit.px(blitSprite.width()));
            this.setHeight(Unit.px(blitSprite.height()));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
//        Township.LOGGER.info("{}", this.y());
        blitSprite.blit(guiGraphics, this.x(), this.y(), this.width(), this.height());
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}

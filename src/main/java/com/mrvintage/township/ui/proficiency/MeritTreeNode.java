package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.IconNode;
import com.mrvintage.township.ui.nodes.Node;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Optional;

public class MeritTreeNode extends Node {

    public final Merit.Path path;
    public final MeritSheet meritSheet;
    public final boolean complete;

    public MeritTreeNode(Merit.Path path, Node.Rect popupRect, boolean complete) {
        this.path = path;
        this.meritSheet = (MeritSheet) new MeritSheet(path).withRect(popupRect);
        this.meritSheet.layout();
        this.complete = complete;

        Merit merit = Profession.findMerit(this.path);
        Optional.ofNullable(merit).ifPresent(m -> {
            this.withChildren(
                new IconNode(merit.icon()).withPos(3, 3)
            );
        });

    }

    @Override
    public void layout() {
        this.withWidth(22).withHeight(22).withOrigin(11, 0);
        super.layout();
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        boolean hovered = this.isMouseOver(mouseX, mouseY);

        if (hovered) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0f, 0.0f, 1000.0f);
            this.meritSheet.render(guiGraphics, mouseX, mouseY, delta);
            guiGraphics.pose().popPose();
        }

        BlitSprite incompleteBg = hovered ? Sprites.SEWN_RED_BORDER_HOVER : Sprites.SEWN_RED_BORDER;
        BlitSprite completeBg = hovered ? Sprites.SEWN_GREEN_BORDER_HOVER : Sprites.SEWN_GREEN_BORDER;
        BlitSprite bg = this.complete ? completeBg : incompleteBg;
        bg.blit(guiGraphics, this.x(), this.y(), this.width(), this.height());
    }
}

package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GoalDisplay extends Node {

    private static final int LEFT = 20;

    public final Node root = new BlitSpriteNode(Sprites.PARCHMENT_BG).withPadding(3, 3).withHeight(Unit.auto()).withChildren(
        new TextNode()
            .withVerticalAlign(NodeUi.VerticalAlign.CENTER)
            .withHorizontalAlign(NodeUi.HorizontalAlign.CENTER)
            .withRect(Unit.px(0), Unit.px(0), Unit.px(LEFT), Unit.percent(1.0f))
            .withId("xp"),
        new TextNode()
            .withVerticalAlign(NodeUi.VerticalAlign.CENTER)
            .withHorizontalAlign(NodeUi.HorizontalAlign.CENTER)
            .withRect(Unit.px(LEFT + 3), Unit.px(0), basis -> basis - (LEFT + 3),  Unit.auto())
            .withId("desc")
    );

    public GoalDisplay(Component desc, int xp) {
        TextNode xpNode = (TextNode) this.root.getNodeWithId("xp").get();
        TextNode descNode = (TextNode) this.root.getNodeWithId("desc").get();
        descNode.withText(desc);
        xpNode.withText(String.valueOf(xp));

        this.withChildren(root).withHeight(Unit.auto());
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {}
}

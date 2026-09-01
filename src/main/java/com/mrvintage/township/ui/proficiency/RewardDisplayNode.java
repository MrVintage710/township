package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.reward.Reward;
import com.mrvintage.township.profession.reward.Rewards;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.BlitSpriteNode;
import com.mrvintage.township.ui.nodes.Node;
import com.mrvintage.township.ui.nodes.TextNode;
import com.mrvintage.township.ui.nodes.Unit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RewardDisplayNode extends Node {

    private final Node root = new BlitSpriteNode(Sprites.PARCHMENT_BG).withPadding(3, 3).withHeight(Unit.auto()).withChildren(
        new TextNode().withHeight(Unit.auto()).withId("desc")
    );

    public RewardDisplayNode(Component text) {
        this.root.getNodeWithId("desc").ifPresent(node -> {
            TextNode descNode = (TextNode) node;
            descNode.withText(text);
        });
        this.withChildren(root).withHeight(Unit.auto());
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }
}

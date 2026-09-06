package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class RewardDisplayNode extends Node {

    private final Node root = new BlitSpriteNode(Sprites.PARCHMENT_BG).withPadding(3, 3).withHeight(Unit.auto()).withChildren(
        new VerticalNode().withGap(2).withId("container").withChildren(
            new TextNode().withHeight(Unit.auto()).withId("desc")
        )
    );

    public RewardDisplayNode(Component text, Optional<List<ResourceLocation>> items) {
        this.withChildren(root).withHeight(Unit.auto());
        this.root.getNodeWithId("desc").ifPresent(node -> {
            TextNode descNode = (TextNode) node;
            descNode.withText(text);
        });

        items
            .ifPresent(resourceLocations -> this.root.getNodeWithId("container")
                .ifPresent(node -> node.withChildren(new ItemArrayNode().withItems(resourceLocations)))
            );
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }
}

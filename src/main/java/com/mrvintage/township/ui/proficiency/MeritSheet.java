package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Optional;

public class MeritSheet extends Node {

    private final Merit.Path path;

    private final Node node = new BlitSpriteNode(Sprites.TORN_PAPER_BG)
        .withRect(Unit.px(0), Unit.px(0), Unit.percent(1.0f), Unit.percent(1.0f))
        .withPadding(10, 10)
        .withChildren(
            new BlitSpriteNode(Sprites.SEWN_RED_BORDER)
                .withRect(0, 0, 22, 22)
                .withPadding(3, 3)
                .withChildren(
                    new IconNode().withId("icon")
                ),
            new TextNode()
                .withVerticalAlign(NodeUi.VerticalAlign.CENTER)
                .withHorizontalAlign(NodeUi.HorizontalAlign.CENTER)
                .withId("title")
                .withRect(Unit.px(24), Unit.px(0), Unit.px(115), Unit.px(22)),

            new EmptyNode().withPos(0, 26).withHeight(Unit.auto()).withId("goals")
        );

    public MeritSheet(Merit.Path path) {
        this.path = path;
        this.withChildren(this.node);
    }

    @Override
    public void layout() {
        Optional.ofNullable(Profession.findMerit(path)).ifPresent(merit -> {
            this.node.getNodeWithId("icon").ifPresent(node -> ((IconNode) node).setIcon(merit.icon()));
            this.node.getNodeWithId("title").ifPresent(node -> ((TextNode) node).withText(merit.name()));

            this.node.getNodeWithId("goals").ifPresent(node -> {
                for(var goal : merit.goals()) {
                    node.withChildren(new GoalDisplay(goal.toDescription(), 10));
                }
            });

        });
        super.layout();
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.node.render(guiGraphics, mouseX, mouseY, delta);
    }
}

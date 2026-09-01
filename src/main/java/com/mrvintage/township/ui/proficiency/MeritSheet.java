package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.profession.reward.Reward;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.Optional;
import java.util.stream.Collectors;

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

            new ScrollNode().withPos(0, 23).withHeight(basis -> basis - 23).withChildren(
                new VerticalNode().withGap(6).withHeight(Unit.auto()).withId("content")
            )
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

            this.node.getNodeWithId("content").ifPresent(node -> {
                if (!merit.goals().isEmpty()) {
                    Node goals = new VerticalNode().withGap(2).withWidth(1.0f).withHeight(Unit.auto()).withChildren(
                        new HorizontalNode().withGap(2).withWidth(157).withHeight(Unit.auto()).withChildren(
                            new TextNode(Component.literal("Goals").withStyle(style -> style.withUnderlined(true)))
                                .withHeight(Unit.auto()).withWidth(30),

                            new ProgressNode().setY(3).withId("goal_progress").withWidth(basis -> basis - 33)
                        )
                    );
                    for(var goal : merit.goals()) {
                        goals.withChildren(new GoalDisplay(goal.toDescription(), 10));
                    }

                    node.withChildren(goals);
                }

                var rewardTexts = merit.rewards().stream().map(Reward::getDescription).filter(Optional::isPresent).map(Optional::get).toList();
                if(!rewardTexts.isEmpty()) {
                    Node rewards = new VerticalNode().withGap(2).withWidth(1.0f).withHeight(Unit.auto()).withChildren(
                        new TextNode(Component.literal("Rewards").withStyle(style -> style.withUnderlined(true)))
                            .withHeight(Unit.auto()).withWidth(1.0f)
                    );
                    for (Component rewardText : rewardTexts) {
                        rewards.withChildren(new RewardDisplayNode(rewardText));
                    }

                    node.withChildren(rewards);
                }

                merit.desc().ifPresent(desc -> {
                    node.withChildren(new TextNode(desc).withHeight(Unit.auto()));
                });
            });

        });
        super.layout();
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.node.getNodeWithId("goal_progress").ifPresent(node -> {
            ProgressNode progressNode = (ProgressNode) node;
            var meritProgress = ProfessionProgress.ClientProfessionProgress.getInProgress(this.path);
            var merit = Profession.findMerit(this.path);
            if(merit == null || meritProgress == null) {
                progressNode.setProgress(1.0f);
            } else {
                progressNode.setProgress((float) meritProgress.getXp() / (float)merit.xp());
            }
        });
    }
}

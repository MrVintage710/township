package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.profession.reward.Reward;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class MeritSheet extends Node {

    private final Merit.Path path;

    private final Node node = new BlitSpriteNode(Sprites.TORN_PAPER_BG)
        .withPadding(10, 10)
        .withRect(0, 0, 174,190)
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


            new EmptyNode().withId("container").withPos(0, 23).withHeight(basis -> basis - 23).withClip().withChildren(
                new VerticalNode().withGap(6).withClip().withId("content")
            )
        );

    public MeritSheet(Merit.Path path) {
        this.path = path;
        this.withChildren(this.node);

        Optional.ofNullable(Profession.findMerit(path)).ifPresent(merit -> {
            this.node.getNodeWithId("icon").ifPresent(node -> ((IconNode) node).setIcon(merit.icon()));
            this.node.getNodeWithId("title").ifPresent(node -> ((TextNode) node).withText(merit.name()));

            this.node.getNodeWithId("content").ifPresent(node -> {
                if (!merit.goals().isEmpty()) {
                    Node goals = new VerticalNode().withGap(2).withParent(node).withHeight(Unit.auto()).withChildren(
                        new HorizontalNode().withGap(2).withHeight(Unit.auto()).withChildren(
                            new TextNode(Component.literal("Goals").withStyle(style -> style.withUnderlined(true)))
                                .withHeight(Unit.auto()).withWidth(30),

                            new ProgressNode().setY(3).withId("goal_progress").withWidth(basis -> basis - 32)
                        )
                    );
                    for(var goal : merit.goals()) {
                        goals.withChildren(new GoalDisplay(goal.toDescription(), 10));
                    }
                }

                var rewardTexts = merit.rewards().stream().filter(reward -> reward.getDescription().isPresent()).map(reward -> new RewardDisplayNode(reward.getDescription().get(), reward.renderItems())).toList();
                if(!rewardTexts.isEmpty()) {
                    Node rewards = new VerticalNode().withGap(2).withWidth(1.0f).withHeight(Unit.auto()).withChildren(
                        new TextNode(Component.literal("Rewards").withStyle(style -> style.withUnderlined(true)))
                            .withHeight(Unit.auto()).withWidth(1.0f)
                    );
                    for (RewardDisplayNode reward : rewardTexts) {
                        rewards.withChildren(reward);
                    }

                    node.withChildren(rewards);
                }

                merit.desc().ifPresent(desc -> {
                    node.withChildren(new TextNode(desc).withHeight(Unit.auto()));
                });
            });

            this.layout();
        });
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

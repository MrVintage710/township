package com.mrvintage.township.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.sound.Sounds;
import com.mrvintage.township.ui.nodes.*;
import com.mrvintage.township.util.Easing;
import com.mrvintage.township.util.timeline.Timeline;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Township.MODID, value = Dist.CLIENT)
public class PlayerOverlayPatch {
    private static final float XP_NOTIFICATION_LIFETIME = 60f;
    private static final float MERIT_COMPLETE_NOTIFICATION_LIFETIME = 6f * 20f;

    private static final List<XpNotification> XP_NOTIFICATIONS = new ArrayList<>();
    private static final List<MeritCompleteNotification> MERIT_COMPLETE_NOTIFICATIONS = new ArrayList<>();

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Township.MODID, "hud"), PlayerOverlayPatch::render);
    }

    public static void enqueueXpNotification(Merit.Path path, int xp) {
        if(xp <= 0) return;
        XP_NOTIFICATIONS.add(new XpNotification(xp, path));
    }

    public static void enqueueMeritCompleteNotification(Merit.Path path) {
        MERIT_COMPLETE_NOTIFICATIONS.add(new MeritCompleteNotification(path));
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        ArrayList<XpNotification> removeIndices = new ArrayList<>();
        for(XpNotification notification : XP_NOTIFICATIONS) {
            if (notification.render(graphics, deltaTracker)) {
                removeIndices.add(notification);
            }
        }
        XP_NOTIFICATIONS.removeAll(removeIndices);
        if (!MERIT_COMPLETE_NOTIFICATIONS.isEmpty()) {
            if (MERIT_COMPLETE_NOTIFICATIONS.getFirst().render(graphics, deltaTracker)) {
                MERIT_COMPLETE_NOTIFICATIONS.removeFirst();
            }
        }
    }

    private static class XpNotification {
        final Integer xp;
        final Merit.Path path;
        float time;

        final Timeline timeline = new Timeline(XP_NOTIFICATION_LIFETIME);

        public XpNotification(Integer number, Merit.Path path) {
            this.xp = number;
            this.path = path;
        }

        public boolean render(GuiGraphics graphics, DeltaTracker deltaTracker) {
            var meritProgress = ProfessionProgress.ClientProfessionProgress.getInProgress(path);
            var merit = Profession.findMerit(path);
            if(merit == null || meritProgress == null || this.time >= XP_NOTIFICATION_LIFETIME) return true;

            float t = 1.0f - (time / XP_NOTIFICATION_LIFETIME);

            int originX = graphics.guiWidth() / 2 - 8;
            int originY = graphics.guiHeight() - 80;
            int x = (int) ((float) originX + (3 * Math.sin(time / 4.0f)));
            int y = (int) ((float) originY + (t * 32));
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, t);

            merit.renderIcon(graphics, x, y);
            graphics.drawString(Minecraft.getInstance().font, "+" + this.xp, x + 16, y + 6, 0xFFFF99);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            float percent = (float) meritProgress.getXp() / (float) merit.xp();
            Sprites.PROGRESS_BAR_EMPTY.blit(graphics, graphics.guiWidth() / 2 - 91, graphics.guiHeight() - 29, 182, 5);
            Sprites.PROGRESS_BAR_FULL.blit(graphics, graphics.guiWidth() / 2 - 91, graphics.guiHeight() - 29, Math.round(182 * percent), 5);

            this.time += deltaTracker.getRealtimeDeltaTicks();
            return false;
        }
    }

    private static class MeritCompleteNotification {

        private final Node overlay =
            new BlitSpriteNode(Sprites.TORN_PAPER_BG).withRect(basis -> basis / 2 - 170 / 2, Unit.px(5), Unit.px(170), Unit.px(44)).withPadding(10, 7).withChildren(
                new BlitSpriteNode(Sprites.SEWN_RED_BORDER).withRect(Unit.px(0), basis -> basis / 2 - 11, Unit.px(22), Unit.px(22)).withChildren(
                    new IconNode().withRect(3, 3, 16, 16).withId("icon")
                ),

                new TextNode("Congrats! You completed")
                    .withBehavior(TextNode.Behavior.Pan)
                    .withVerticalAlign(NodeUi.VerticalAlign.CENTER)
                    .withRect(Unit.px(23), basis -> basis / 2 - 11, basis -> basis - 22, Unit.px(11)),

                new TextNode()
                    .withColor(0XFFAA00)
                    .withBehavior(TextNode.Behavior.Pan)
                    .withVerticalAlign(NodeUi.VerticalAlign.CENTER)
                    .withClip()
                    .withId("text")
                    .withRect(Unit.px(23), basis -> basis / 2, basis -> basis - 22, Unit.px(11))
            );

        private final Timeline timeline = new Timeline(MERIT_COMPLETE_NOTIFICATION_LIFETIME);

        private final Merit.Path path;
        private float opacity = 0.0f;

        public MeritCompleteNotification(Merit.Path path) {
            var merit = Profession.findMerit(path);
            this.path = path;

            overlay.getNodeWithId("icon").ifPresent(icon -> {
                ((IconNode) icon).setIcon(merit.icon());
            });

            overlay.getNodeWithId("text").ifPresent(text -> {
                ((TextNode) text).withText(merit.name() + "!");
            });

            this.timeline.addToTimeline(0f, 5f, progress -> this.opacity = progress);
            this.timeline.addToTimeline(-5f, MERIT_COMPLETE_NOTIFICATION_LIFETIME, progress -> this.opacity = (1.0f - progress));
            this.timeline.addToTimeline(0f, 15f, progress -> {
                int height = this.overlay.height();
                this.overlay.setY((int) ( -height + (height + 5) * progress));
            }, Easing.easeOutCubic);

            this.timeline.addEvent(0f, () -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(Sounds.LEVEL_UP.get(), 1.0F)));

            var textLength = Minecraft.getInstance().font.width(merit.name());

            // If the text is falling off of the ui, change the size and position to fix.
            if (textLength >= this.overlay.getHorizontalBasis() - 22) {
                var newWidth = textLength + 25 + this.overlay.getPaddingLeft() + this.overlay.getPaddingRight();
                this.overlay.withWidth(newWidth);
                this.overlay.withRect(basis -> basis / 2 - newWidth / 2, Unit.px(5), Unit.px(newWidth), Unit.px(44));
            }

        }

        public boolean render(GuiGraphics graphics, DeltaTracker deltaTracker) {
            var merit = Profession.findMerit(path);
            if(merit == null || this.timeline.isDone()) return true;
            this.timeline.tick(deltaTracker.getGameTimeDeltaTicks());

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.opacity);

            overlay.render(graphics, 0, 0, deltaTracker.getRealtimeDeltaTicks());

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            return false;
        }
    }
}

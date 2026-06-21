package com.mrvintage.township.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.sound.Sounds;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Township.MODID, value = Dist.CLIENT)
public class PlayerOverlayPatch {
    private static final float XP_NOTIFICATION_LIFETIME = 60f;
    private static final float MERIT_COMPLETE_NOTIFICATION_LIFETIME = 180f;

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
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(Sounds.LEVEL_UP.get(), 1.0F));
        MERIT_COMPLETE_NOTIFICATIONS.add(new MeritCompleteNotification(path));
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        XP_NOTIFICATIONS.removeIf(xpNotification -> xpNotification.render(graphics, deltaTracker));
        MERIT_COMPLETE_NOTIFICATIONS.removeIf(meritCompleteNotification -> meritCompleteNotification.render(graphics, deltaTracker));
    }

    private static class XpNotification {
        final Integer xp;
        final Merit.Path path;

        float time;

        public XpNotification(Integer number, Merit.Path path) {
            this.xp = number;
            this.path = path;
        }

        public boolean render(GuiGraphics graphics, DeltaTracker deltaTracker) {
            var merit = Profession.findMerit(path);
            if(merit == null || this.time >= XP_NOTIFICATION_LIFETIME) return true;

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

            this.time += deltaTracker.getRealtimeDeltaTicks();
            return false;
        }
    }

    private static class MeritCompleteNotification {

        private IconNode icon = (IconNode) new IconNode().withRect(3, 3, 16, 16);

        private TextNode text = (TextNode) new TextNode("Merit Completed!").withRect(Unit.px(0), Unit.px(0),  Unit.percent(1.0f), Unit.px(9));

        private final Node overlay =
            new BlitSpriteNode(Sprites.TORN_PAPER_BG).withRect(0.25f, 5, 0.5f, 42).withChildren(
                new Node().withRect(Unit.px(10), basis -> (basis / 2) - 11, Unit.percent(1.0f), Unit.px(22)).withChildren(
                    new BlitSpriteNode(Sprites.SEWN_BORDER).withRect(0, 0, 22, 22).withChildren(
                        icon
                    ),
                    new SeriesNode().withGap(1).withPos(24, 0).withChildren(
                        text,
                        new TextNode("complete!")
                    )
                )
            );

        private float time = 0f;
        private final Merit.Path path;

        public MeritCompleteNotification(Merit.Path path) {
            this.path = path;
        }

        public boolean render(GuiGraphics graphics, DeltaTracker deltaTracker) {
            var merit = Profession.findMerit(path);
            if(merit == null || this.time >= MERIT_COMPLETE_NOTIFICATION_LIFETIME) return true;

            Node overlay = new BlitSpriteNode(Sprites.TORN_PAPER_BG).withRect(0.25f, 5, 0.5f, 42).withChildren(
                new Node().withRect(Unit.px(10), basis -> (basis / 2) - 11, Unit.percent(1.0f), Unit.percent(1.0f)).withChildren(
                    new BlitSpriteNode(Sprites.SEWN_BORDER).withRect(0, 0, 22, 22).withChildren(
                        icon
                    ),
                    new SeriesNode().withGap(4).withPos(24, 0).withChildren(
                        text,
                        new TextNode(Component.literal("complete!"))
                    ),
                    new TextNode("Open your journal for your rewards!").withRect(Unit.px(24), Unit.px(12), basis -> basis - 24, Unit.px(16))
                )
            );

            overlay.setDefaultSize(graphics);

            icon.setIcon(merit.icon());
            text.setTextComponent(
                Component.literal(merit.name()).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)
            );

            overlay.render(graphics, 0, 0, deltaTracker.getRealtimeDeltaTicks());

            time += deltaTracker.getRealtimeDeltaTicks();
            return false;
        }
    }
}

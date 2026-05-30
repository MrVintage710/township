package com.mrvintage.township.ui;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.widgets.ProficiencyButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = Township.MODID, value = Dist.CLIENT)
public class PlayerInventoryPatch {

    private static final ProficiencyButton proficiencyButton = new ProficiencyButton(100, 100);

    @SubscribeEvent
    public static void onRenderInventoryEvent(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen) {
            event.addListener(proficiencyButton);
        }
    }

    @SubscribeEvent
    public static void onRenderInventoryEvent(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            proficiencyButton.setX(screen.getGuiLeft() + 130);
            proficiencyButton.setY(screen.getGuiTop() + 61);
            proficiencyButton.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
        }
    }
}

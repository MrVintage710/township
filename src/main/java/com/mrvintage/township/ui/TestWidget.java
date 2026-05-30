package com.mrvintage.township.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrvintage.township.event.ServerModEvents;
import com.mrvintage.township.proficiency.Proficiency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.NotNull;

public class TestWidget implements Renderable {
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float v) {

        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            var registry = connection.registryAccess().registry(ServerModEvents.PROFICIENCY_REGISTRY_KEY);

            if (registry.isPresent()) {
                int index = 0;
                for (Proficiency proficiency : registry.get()) {
                    guiGraphics.blit(proficiency.getIcon(), 16 * index, 0, 0, 0, 16, 16, 16, 16);
                    index++;
                }
            }
        }
    }
}

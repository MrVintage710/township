package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.Township;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ProficiencyScreen extends Screen {

    public static final ResourceLocation PROFICIENCY_SCREEN_TEXTURE = ResourceLocation.fromNamespaceAndPath(Township.MODID, "textures/gui/proficiency_screen.png");
    public static final int PROFICIENCY_SCREEN_TEXTURE_WIDTH = 512;
    public static final int PROFICIENCY_SCREEN_TEXTURE_HEIGHT = 512;

    protected ProficiencyScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {

    }
}

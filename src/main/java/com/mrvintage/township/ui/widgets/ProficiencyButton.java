package com.mrvintage.township.ui.widgets;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.proficiency.ProficiencyScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

public class ProficiencyButton extends TownshipButton {

    public static final BlitSprite PROFICIENCY_BUTTON_TEXTURE = new BlitSprite(
            ProficiencyScreen.PROFICIENCY_SCREEN_TEXTURE,
            350, 34, 20, 18,
            ProficiencyScreen.PROFICIENCY_SCREEN_TEXTURE_WIDTH,
            ProficiencyScreen.PROFICIENCY_SCREEN_TEXTURE_HEIGHT
    );

    public static final BlitSprite PROFICIENCY_BUTTON_HOVER_TEXTURE = new BlitSprite(
            ProficiencyScreen.PROFICIENCY_SCREEN_TEXTURE,
            350, 53, 20, 18,
            ProficiencyScreen.PROFICIENCY_SCREEN_TEXTURE_WIDTH,
            ProficiencyScreen.PROFICIENCY_SCREEN_TEXTURE_HEIGHT
    );

    public ProficiencyButton(int x, int y) {
        super(x, y, 20, 18, Component.empty(), PROFICIENCY_BUTTON_TEXTURE, PROFICIENCY_BUTTON_HOVER_TEXTURE);
    }

    @Override
    public void onPress() {
        Township.LOGGER.info("Test");
    }
}

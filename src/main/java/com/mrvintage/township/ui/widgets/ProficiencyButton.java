package com.mrvintage.township.ui.widgets;

import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.proficiency.ProficiencyScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ProficiencyButton extends TownshipButton {

    public ProficiencyButton(int x, int y) {
        super(x, y, 20, 18, Component.empty(), Sprites.PROFIENCIES_BUTTON, Sprites.PROFIENCIES_BUTTON_HOVER);
    }

    @Override
    public void onPress() {
        Minecraft.getInstance().setScreen(new ProficiencyScreen(Component.empty()));
    }


}

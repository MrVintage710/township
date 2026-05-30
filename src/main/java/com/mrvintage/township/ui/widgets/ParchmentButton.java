package com.mrvintage.township.ui.widgets;

import com.mrvintage.township.ui.Sprites;
import net.minecraft.network.chat.Component;

public class ParchmentButton extends TownshipButton {

    public ParchmentButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message, Sprites.PARCHEMENT_BG, Sprites.PARCHEMENT_BG_HOVER);
    }

    @Override
    public void onPress() {

    }
}

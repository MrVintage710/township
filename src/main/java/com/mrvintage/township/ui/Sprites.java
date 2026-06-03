package com.mrvintage.township.ui;

import com.mrvintage.township.Township;
import net.minecraft.resources.ResourceLocation;

public class Sprites {

    public static final BlitSprite PROFICIENCIES_FULL = new BlitSprite(
            ResourceLocation.fromNamespaceAndPath(Township.MODID, "textures/gui/proficiencies.png"),
            512, 512
    );

    public static final BlitSprite PROFICIENCIES_BG = PROFICIENCIES_FULL.slice(36, 4, 296, 201);

    public static final BlitSprite PROFIENCIES_BUTTON = PROFICIENCIES_FULL.slice(350, 34, 20, 18);
    public static final BlitSprite PROFIENCIES_BUTTON_HOVER = PROFICIENCIES_FULL.slice(350, 53, 20, 18);

    private static final BlitSpriteScaling PARCHMENT_BG_SCALING = new BlitSpriteScaling.NineSlice(3, 3,3,3);

    public static final BlitSprite PARCHMENT_BG = PROFICIENCIES_FULL.slice(384, 16, 32, 16, PARCHMENT_BG_SCALING);
    public static final BlitSprite PARCHMENT_BG_HOVER = PROFICIENCIES_FULL.slice(384, 32, 32, 16, PARCHMENT_BG_SCALING);

    private static final BlitSpriteScaling PARCHMENT_SCROLL_SCALING = new BlitSpriteScaling.NineSlice(0, 0,3,3);
    private static final BlitSpriteScaling PARCHMENT_SCROLL_HANDLE_SCALING = new BlitSpriteScaling.NineSlice(0, 0,2,2);

    public static final BlitSprite PARCHMENT_SCROLL = PROFICIENCIES_FULL.slice(437, 48, 5, 16, PARCHMENT_SCROLL_SCALING);

    public static final BlitSprite PARCHMENT_SCROLL_HANDLE = PROFICIENCIES_FULL.slice(453, 52, 5, 8, PARCHMENT_SCROLL_HANDLE_SCALING);
}

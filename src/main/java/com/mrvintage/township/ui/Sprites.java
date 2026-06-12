package com.mrvintage.township.ui;

import com.mrvintage.township.Township;
import net.minecraft.resources.ResourceLocation;

public class Sprites {

    public static final BlitSprite PROFICIENCIES_FULL = new BlitSprite(
            ResourceLocation.fromNamespaceAndPath(Township.MODID, "textures/gui/profession_menu.png"),
            512, 512
    );

    public static final BlitSprite PROFICIENCIES_BG = PROFICIENCIES_FULL.slice(36, 4, 296, 201);
    public static final BlitSprite TORN_PAPER_BG = PROFICIENCIES_FULL.slice(347, 38, 58, 36,
        new BlitSpriteScaling.NineSlice(10, 10, 10, 10)
    );
    public static final BlitSprite BOOKMARK_RIGHT = PROFICIENCIES_FULL.slice(419, 0, 34, 18,
        new BlitSpriteScaling.NineSlice(1, 15, 8, 8)
    );

    public static final BlitSprite PROFIENCIES_BUTTON = PROFICIENCIES_FULL.slice(398, 0, 20, 18);
    public static final BlitSprite PROFIENCIES_BUTTON_HOVER = PROFICIENCIES_FULL.slice(398, 19, 20, 18);

    private static final BlitSpriteScaling PARCHMENT_BG_SCALING = new BlitSpriteScaling.NineSlice(3, 3,3,3);

    public static final BlitSprite PARCHMENT_BG = PROFICIENCIES_FULL.slice(352, 0, 32, 16, PARCHMENT_BG_SCALING);
    public static final BlitSprite PARCHMENT_BG_HOVER = PROFICIENCIES_FULL.slice(352, 16, 32, 16, PARCHMENT_BG_SCALING);

    private static final BlitSpriteScaling PARCHMENT_SCROLL_SCALING = new BlitSpriteScaling.NineSlice(0, 0,3,3);
    private static final BlitSpriteScaling PARCHMENT_SCROLL_HANDLE_SCALING = new BlitSpriteScaling.NineSlice(0, 0,2,2);

    public static final BlitSprite PARCHMENT_SCROLL = PROFICIENCIES_FULL.slice(387, 0, 7, 16, PARCHMENT_SCROLL_SCALING);

    public static final BlitSprite PARCHMENT_SCROLL_HANDLE = PROFICIENCIES_FULL.slice(387, 20, 5, 8, PARCHMENT_SCROLL_HANDLE_SCALING);

    public static final BlitSprite SEWN_BORDER = PROFICIENCIES_FULL.slice(332, 0, 18, 18, new BlitSpriteScaling.NineSlice(3, 3, 3, 3));
    public static final BlitSprite SEWN_BORDER_HOVER = PROFICIENCIES_FULL.slice(332, 18, 18, 18, new BlitSpriteScaling.NineSlice(3, 3, 3, 3));

    public static final BlitSprite BOOKMARK_LEFT = PROFICIENCIES_FULL.slice(419, 19, 34, 18, new BlitSpriteScaling.NineSlice(15, 1, 8, 8));

}

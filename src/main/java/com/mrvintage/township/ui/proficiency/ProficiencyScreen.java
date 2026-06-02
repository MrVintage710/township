package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.BlitSpriteNode;
import com.mrvintage.township.ui.nodes.ScrollList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ProficiencyScreen extends Screen {

    private static final int bgPaddingX = 18;
    private static final int bgPaddingY = 18;

    protected int leftPos;
    protected int topPos;

    public ProficiencyScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - Sprites.PROFICIENCIES_BG.width()) / 2;
        this.topPos = (this.height - Sprites.PROFICIENCIES_BG.height()) / 2;

        var root =
            new BlitSpriteNode(Sprites.PROFICIENCIES_BG)
                .withPadding(15, 15, 16, 15)
                .withChildren(
                    new ScrollList().withRect(0, 0, 128, 168).withPadding(1, 1).withChildren(
                        new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16).debugMode(),
                        new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16).debugMode(),
                        new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16).debugMode()
                    )
                );

        this.renderables.add(root);
        this.addWidget(root);

//        this.addRenderableWidget(new ParchmentButton(this.leftPos, this.topPos, 128, 16, Component.literal("Test")));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.leftPos = (this.width - Sprites.PROFICIENCIES_BG.width()) / 2;
        this.topPos = (this.height - Sprites.PROFICIENCIES_BG.height()) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

//        guiGraphics.drawString(Minecraft.getInstance().font, "Skils", leftPos + bgPaddingX, topPos + bgPaddingY, 0);

//        Sprites.PARCHEMENT_BG.blit(guiGraphics, 0, 0);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, partialTick);
        }

        this.renderTransparentBackground(guiGraphics);
//        Sprites.PROFICIENCIES_BG.blit(guiGraphics, this.leftPos, this.topPos, 296, 201);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

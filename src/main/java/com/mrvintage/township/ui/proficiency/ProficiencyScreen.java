package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.widgets.ParchmentButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ProficiencyScreen extends Screen {

    protected int leftPos;
    protected int topPos;

    public ProficiencyScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - Sprites.PROFICIENCIES_BG.width()) / 2;
        this.topPos = (this.height - Sprites.PROFICIENCIES_BG.height()) / 2;

        this.addRenderableWidget(new ParchmentButton(this.leftPos, this.topPos, 128, 16, Component.literal("Test")));
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
        Sprites.PARCHEMENT_BG.blit(guiGraphics, 0, 0, 32, 64);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, partialTick);
        }

        this.renderTransparentBackground(guiGraphics);
        Sprites.PROFICIENCIES_BG.blit(guiGraphics, this.leftPos, this.topPos, 296, 201);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

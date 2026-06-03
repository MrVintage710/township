package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.BlitSpriteNode;
import com.mrvintage.township.ui.nodes.Node;
import com.mrvintage.township.ui.nodes.NodeScreen;
import com.mrvintage.township.ui.nodes.ScrollList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ProficiencyScreen extends NodeScreen {

    private static final int bgPaddingX = 18;
    private static final int bgPaddingY = 18;

    protected int leftPos;
    protected int topPos;

    public ProficiencyScreen(Component title) {
        super(title);
    }

    @Override
    protected Node root() {
        return new BlitSpriteNode(Sprites.PROFICIENCIES_BG)
            .withPadding(15, 15, 16, 15)
            .withChildren(
                new ScrollList().withRect(0, 0, 128, 169).withChildren(
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16),
                    new BlitSpriteNode(Sprites.PARCHMENT_BG).withHeight(16).debugMode()
                )
            );
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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}

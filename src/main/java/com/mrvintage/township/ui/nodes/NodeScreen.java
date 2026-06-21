package com.mrvintage.township.ui.nodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class NodeScreen extends Screen {

    protected Node root;

    protected NodeScreen(Component title) {
        super(title);
        this.root = root();
    }

    protected abstract Node root();

    @Override
    protected void init() {
        this.renderables.add(this.root);
        this.addWidget(this.root);
        this.root.layout();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.root.setDefaultSize(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void layout() {
        this.root.layout();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.layout();
        super.resize(minecraft, width, height);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.root.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.root.mouseReleased(mouseX, mouseY, button);
    }
}

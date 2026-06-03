package com.mrvintage.township.ui.nodes;

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

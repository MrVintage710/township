package com.mrvintage.township.ui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class Rect<T extends RectRenderable> implements Renderable, GuiEventListener {

    private Unit x = Unit.px(0);
    private Unit y = Unit.px(0);
    private Unit w = Unit.percent(1.0f);
    private Unit h =  Unit.percent(1.0f);

    private List<Rect<?>> children = new ArrayList<>();

    private Optional<Rect<?>> parent = Optional.empty();

    private final T widget;

    public Rect(T widget) {
        this.widget = widget;
    }

    public Rect<T> with_children(Rect<?>... children) {
        this.children.addAll(List.of(children));
        return this;
    }

    public Rect<T> with_pos(Unit x, Unit y, Unit w, Unit h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.widget.render(guiGraphics, this, mouseX, mouseY, delta);
        for(Rect<?> child : this.children) {
            child.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for(Rect<?> child : this.children) {
            child.mouseMoved(mouseX, mouseY);
        }
        this.widget.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(Rect<?> child : this.children) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return this.widget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(Rect<?> child : this.children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return this.widget.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for(Rect<?> child : this.children) {
            if (child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return this.widget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for(Rect<?> child : this.children) {
            if (child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return this.widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Rect<?> child : this.children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return this.widget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Rect<?> child : this.children) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return this.widget.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Rect<?> child : this.children) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return this.widget.charTyped(codePoint, modifiers);
    }

    @Override
    public void setFocused(boolean b) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public int x() {
        int basis = this.parent.map(Rect::width).orElse(Minecraft.getInstance().screen.width);
        int origin = this.parent.map(Rect::x).orElse(0);
        return this.x.calc(basis) + origin;
    }

    public int y() {
        int basis = this.parent.map(Rect::height).orElse(Minecraft.getInstance().screen.height);
        int origin = this.parent.map(Rect::y).orElse(0);
        return this.x.calc(basis) + origin;
    }

    public int width() {
        int basis = this.parent.map(Rect::width).orElse(Minecraft.getInstance().screen.width);
        return this.w.calc(basis);
    }

    public int height() {
        int basis = this.parent.map(Rect::height).orElse(Minecraft.getInstance().screen.height);
        return this.h.calc(basis);
    }
}

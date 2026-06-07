package com.mrvintage.township.ui.nodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class Node implements Renderable, GuiEventListener, NarratableEntry {

    public record Rect(int x, int y, int w, int h) {
        public Rect inner(int x, int y, int w, int h) {
            return new Rect(this.x + x, this.y + y, w, h);
        }

        public Rect inner(int x, int y, float w, float h) {
            return new Rect(this.x + x, this.y + y, (int) (this.w * w), (int) (this.h * h));
        }

        public Rect inner(int x, int y, float w, int h) {
            return new Rect(this.x + x, this.y + y, (int) (this.w * w), h);
        }

        public Rect shrinkTop(int amount) {
            return new Rect(this.x, this.y + amount, this.w, this.h - amount);
        }

        public Rect shrinkBottom(int amount) {
            return new Rect(this.x, this.y, this.w, this.h - amount);
        }

        public int right() {
            return this.x() + this.w;
        }

        public int left() {
            return this.x();
        }

        public int top() {
            return this.y();
        }

        public int bottom() {
            return this.y + this.h;
        }

        public boolean contains(double x, double y) {
            return x >= this.left() &&  x <= this.right() && y >= this.top() && y <= this.bottom();
        }

        public void debug(GuiGraphics graphics) {
            graphics.renderOutline(this.x(), this.y(), this.w(), this.w(), FastColor.ARGB32.color(50, 0, 255, 255));
//            graphics.drawString(Minecraft.getInstance().font, this.x() + ", " + this.y(), this.x(), this.y(), FastColor.ARGB32.color(50, 255, 0, 0));
        }
    }

    private Unit x = Unit.px(0);
    private Unit y = Unit.px(0);
    private Unit w = Unit.percent(1.0f);
    private Unit h =  Unit.percent(1.0f);

    private boolean shouldDebug = false;

    private int pl, pr, pt, pb = 0;

    private boolean isFocused = false;

    protected List<Node> children = new ArrayList<>();

    protected Optional<Node> parent = Optional.empty();

    public Node withChildren(Node... children) {
        for (Node node : children) { node.parent = Optional.of(this); }
        this.children.addAll(List.of(children));
        this.layout();
        return this;
    }

    public Node withChildren(List<Node> children) {
        for (Node node : children) { node.parent = Optional.of(this); }
        this.children.addAll(children);
        this.layout();
        return this;
    }

    public Node withRect(Unit x, Unit y, Unit w, Unit h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }

    public Node withRect(int x, int y, int w, int h) {
        this.x = Unit.px(x);
        this.y = Unit.px(y);
        this.w = Unit.px(w);
        this.h = Unit.px(h);
        return this;
    }

    public Node withPos(int x, int y) {
        this.x = Unit.px(x);
        this.y = Unit.px(y);
        return this;
    }

    public Node withRect(float x, float y, float w, float h) {
        this.x = Unit.percent(x);
        this.y = Unit.percent(y);
        this.w = Unit.percent(w);
        this.h = Unit.percent(h);
        return this;
    }

    public Node withPos(float x, float y) {
        this.x = Unit.percent(x);
        this.y = Unit.percent(y);
        return this;
    }

    public Node withPos(float x, int y) {
        this.x = Unit.percent(x);
        this.y = Unit.px(y);
        return this;
    }

    public Node withPos(int x, float y) {
        this.x = Unit.px(x);
        this.y = Unit.percent(y);
        return this;
    }

    public Node withPos(Unit x, Unit y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Node withPadding(int top, int right, int bottom, int left) {
        this.pt =  top;
        this.pr = right;
        this.pb = bottom;
        this.pl = left;
        return this;
    }

    public Node withPadding(int horizontal, int vertical) {
        this.pt =  vertical;
        this.pr = horizontal;
        this.pb = vertical;
        this.pl = horizontal;
        return this;
    }

    public Node withHeight(int height) {
        this.h = Unit.px(height);
        return this;
    }

    public Node withHeight(float height) {
        this.h = Unit.percent(height);
        return this;
    }

    public void layout() {
        for (Node child : children) {
            child.layout();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        for(Node child : this.children) {
            child.render(guiGraphics, mouseX, mouseY, delta);
        }
        if(this.shouldDebug) this.debug(guiGraphics);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for(Node child : this.children) {
            child.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(Node child : this.children) {
            if (child.isMouseOver(mouseX, mouseY) && child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(Node child : this.children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for(Node child : this.children) {
            if (child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for(Node child : this.children) {
            if (child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Node child : this.children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Node child : this.children) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Node child : this.children) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setFocused(boolean b) {
        this.isFocused = b;
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }

    public final int x() {
        int basis = this.parent.map(Node::getHorizontalBasis).orElse(Minecraft.getInstance().screen.width);
        int origin = this.parent.map(Node::x).orElse(0) + this.parent.map(Node::getPaddingLeft).orElse(0);
        return this.x.calc(basis) + origin;
    }

    public final Node setX(Unit x) {
        this.x = x;
        return this;
    }

    public final int y() {
        int basis = this.parent.map(Node::getVerticalBasis).orElse(Minecraft.getInstance().screen.height);
        int origin = this.parent.map(Node::y).orElse(0) + this.parent.map(Node::getPaddingTop).orElse(0);
        return this.y.calc(basis) + origin;
    }

    public final Node setY(Unit y) {
        this.y = y;
        return this;
    }

    public final int width() {
        int basis = this.parent.map(Node::getHorizontalBasis).orElse(Minecraft.getInstance().screen.width);
        return this.w.calc(basis);
    }

    public final int parentWidth() {
        return this.parent.map(Node::getHorizontalBasis).orElse(Minecraft.getInstance().screen.width);
    }

    public final Node setWidth(Unit w) {
        this.w = w;
        return this;
    }

    public final int height() {
        int basis = this.parent.map(Node::getVerticalBasis).orElse(Minecraft.getInstance().screen.height);
        return this.h.calc(basis);
    }

    public final int parentHeight() {
        return this.parent.map(Node::getVerticalBasis).orElse(Minecraft.getInstance().screen.height);
    }

    public final Node setHeight(Unit h) {
        this.h = h;
        return this;
    }

    public final int right() {
        return this.x() + (this.width() - this.getPaddingRight());
    }

    public final int left() {
        return this.x();
    }

    public final int top() {
        return this.y();
    }

    public final int bottom() {
        return this.y() + (this.height() - this.getPaddingBottom());
    }

    public int getPaddingLeft() {
        return pl;
    }

    public int getPaddingRight() {
        return pr;
    }

    public int getPaddingTop() {
        return pt;
    }

    public int getPaddingBottom() {
        return pb;
    }

    public int getHorizontalBasis() {
        return this.width() - pl - pr;
    }

    public int getVerticalBasis() {
        return this.height() - pt - pb;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    private void debug(GuiGraphics graphics) {
        graphics.renderOutline(this.x(), this.y(), this.width(), this.height(), FastColor.ARGB32.color(50, 255, 0, 0));
        graphics.drawString(Minecraft.getInstance().font, this.x() + ", " + this.y(), this.x(), this.y(), FastColor.ARGB32.color(50, 255, 0, 0));
    }

    public Node debugMode() {
        this.shouldDebug = true;
        return this;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left() &&  mouseX <= this.right() && mouseY >= this.top() && mouseY <= this.bottom();
    }

    public Rect rect() {
        return new Rect(this.x(), this.y(), this.width(), this.height());
    }
}

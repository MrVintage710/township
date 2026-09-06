package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.Township;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

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
            graphics.drawString(Minecraft.getInstance().font, this.x() + ", " + this.y(), this.x(), this.y(), FastColor.ARGB32.color(50, 255, 0, 0));
        }
    }

    private Unit originX = Unit.px(0);
    private Unit originY = Unit.px(0);

    private Unit x = Unit.px(0);
    private Unit y = Unit.px(0);
    private Unit w = Unit.percent(1.0f);
    private Unit h =  Unit.percent(1.0f);
    private int pl = 0, pr = 0, pt = 0, pb = 0;
    protected int scrollX = 0;
    protected int scrollY = 0;
    private boolean shouldDebug = false;
    private boolean isFocused = false;
    private boolean shouldClip = false;
    private boolean mouseOverLastFrame = false;


    private Optional<String> id = Optional.empty();

    protected List<Node> children = new ArrayList<>();

    protected Optional<Node> parent = Optional.empty();

    public Node withChildren(Node... children) {
        for (Node node : children) {
            this.children.add(node);
            node.parent = Optional.of(this);
            node.layout();
        }
        return this;
    }

    public Node withChildren(List<Node> children) {
        for (Node node : children) {
            this.children.add(node);
            node.parent = Optional.of(this);
            node.layout();
        }
        return this;
    }

    public Node withParent(Node parent) {
        if (this.parent.isPresent()) {
            this.removeParent();
        }
        parent.withChildren(this);
        return this;
    }

    public Node removeChildren(Node... children) {
        for (Node node : children) {
            if (!this.children.contains(node)) continue;
            this.children.remove(node);
            node.removeParent();
        }
        return this;
    }

    public Node removeParent() {
        if (this.parent.isPresent()) {
            this.parent.get().removeChildren(this);
            this.parent = Optional.empty();
        }
        return this;
    }

    public Node resetScroll() {
        this.scrollX = 0;
        this.scrollY = 0;
        return this;
    }

    public Node withRect(@NotNull Node.Rect rect) {
        this.x = Unit.px(rect.x);
        this.y = Unit.px(rect.y);
        this.w = Unit.px(rect.w);
        this.h = Unit.px(rect.h);
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

    public Node withRect(float x, float y, float w, float h) {
        this.x = Unit.percent(x);
        this.y = Unit.percent(y);
        this.w = Unit.percent(w);
        this.h = Unit.percent(h);
        return this;
    }

    public Node withPos(int x, int y) {
        this.x = Unit.px(x);
        this.y = Unit.px(y);
        return this;
    }

    public Node withPos(float x, float y) {
        this.x = Unit.percent(x);
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

    public Node withOrigin(Unit x, Unit y) {
        this.originX = x;
        this.originY = y;
        return this;
    }

    public Node withOrigin(int x, int y) {
        this.originX = Unit.px(x);
        this.originY = Unit.px(y);
        return this;
    }

    public Node withOrigin(float x, float y) {
        this.originX = Unit.percent(x);
        this.originY = Unit.percent(y);
        return this;
    }

    public Node withId(String id) {
        this.id = Optional.of(id);
        return this;
    }

    public Node withClip() {
        this.shouldClip = true;
        return this;
    }

    public void layout() {
        for (Node child : children) {
            child.layout();
        }
    }

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if(!mouseOverLastFrame) this.mouseEntered();

            this.mouseOverLastFrame = true;
        } else {
            this.mouseOverLastFrame = false;
        }

        if (shouldClip) {
            guiGraphics.enableScissor(this.x(), this.y(),  this.x() + this.width(), this.y() + this.height());
        }
        this.draw(guiGraphics, mouseX, mouseY, delta);
        for(Node child : this.children) {
            child.render(guiGraphics, mouseX, mouseY, delta);
        }
        if(shouldClip) { guiGraphics.disableScissor(); }
        if(this.shouldDebug) this.debug(guiGraphics);
    }

    protected abstract void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta);

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
        if(this.isMouseOver(mouseX, mouseY)) {
            for(Node child : this.children) {
                child.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
            return true;
        }
        return false;
    }

    protected void mouseEntered() {}

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

    //==================================================================================================================
    //      X related Methods
    //==================================================================================================================

    public final int x() {
        return this.x(Node.getDefaultContextWidth());
    }

    /// Gets the X coord of the Node. This takes into account parent position and padding, as well as this node origin offset.
    public final int x(int defaultBasis) {
        int basis = this.parent.map(parent -> parent.getHorizontalBasis(defaultBasis)).orElse(defaultBasis);
        int origin =
            this.parent.map(p -> p.x(defaultBasis)).orElse(0) +
            this.parent.map(Node::getPaddingLeft).orElse(0) -
            this.parent.map(Node::scrollX).orElse(0) -
            this.originX.calc(this.width());

        return this.x.calc(basis) + origin;
    }

    public final int localX() {
        return this.localX(this.parentWidth());
    }

    public final int localX(int basis) {
        int origin = -this.originX.calc(basis);
        return this.x.calc(basis) + origin;
    }

    public final int scrollX() {
        return this.scrollX;
    }


    public final Node setX(Unit x) {
        this.x = x;
        return this;
    }
    public final Node setX(int x) {
        this.x = Unit.px(x);
        return this;
    }

    public final Node setX(float x) {
        this.x = Unit.percent(x);
        return this;
    }

    //==================================================================================================================
    //      Y Related Methods
    //==================================================================================================================

    public final int y() {
        return this.y(Node.getDefaultContextHeight());
    }

    public final int y(int defaultBasis) {
        int basis = this.parent.map(parent -> parent.getVerticalBasis(defaultBasis)).orElse(defaultBasis);
        int origin =
            this.parent.map(parent -> parent.y(defaultBasis)).orElse(0) +
            this.parent.map(Node::getPaddingTop).orElse(0) -
            this.parent.map(Node::scrollY).orElse(0) -
            this.originY.calc(this.height());
        return this.y.calc(basis) + origin;
    }

    public final int localY() {
        return this.localY(this.parentHeight());
    }

    public final int localY(int basis) {
        int origin = -this.originY.calc(basis);
        return this.y.calc(basis) + origin;
    }

    public int scrollY() {
        return scrollY;
    }

    public Node setScrollY(int scroll) {
        this.scrollY = scroll;
        return this;
    }

    public Node addScrollY(int delta) {
        this.scrollY += delta;
        return this;
    }

    public final Node setY(int y) {
        this.y = Unit.px(y);
        return this;
    }

    public final Node setY(float y) {
        this.y = Unit.percent(y);
        return this;
    }

    public final Node setY(Unit y) {
        this.y = y;
        return this;
    }

    //==================================================================================================================
    //      Width related Methods
    //==================================================================================================================

    public final int width() {
        return this.width(this.parentHorizontalBasis());
    }

    public final int width(int basis) {
        if (this.w.isAuto()) {return this.contentWidth(basis);}
        return this.w.calc(basis);
    }

    public final int parentWidth() {
        return this.parent.map(Node::width).orElse(Node.getDefaultContextWidth());
    }

    public final int parentHorizontalBasis() {
        return this.parent.map(Node::getHorizontalBasis).orElse(Node.getDefaultContextWidth());
    }

    public final Node withWidth(Unit w) {
        this.w = w;
        return this;
    }

    public final Node withWidth(int w) {
        this.w = Unit.px(w);
        return this;
    }

    public final Node withWidth(float w) {
        this.w = Unit.percent(w);
        return this;
    }

    public int getHorizontalBasis() {
        return this.getHorizontalBasis(this.parentHorizontalBasis());
    }

    public int getHorizontalBasis(int defaultBasis) {
        return this.width(defaultBasis) - pl - pr;
    }

    public int contentWidth(int basis) {
        return this.children.stream()
            .filter(child -> !child.w.isDependentOnParent())
            .mapToInt(child -> child.localX() + child.contentWidth(basis))
            .max()
            .orElse(0);
    }

    //==================================================================================================================
    //      Height related methods
    //==================================================================================================================

    public final int height() {
        return this.height(parentVerticalBasis());
    }

    public final int height(int basis) {
        if (this.h.isAuto()) { return this.contentHeight(basis); }
        return this.h.calc(basis);
    }

    public final int parentHeight() {
        return this.parent.map(Node::height).orElse(Node.getDefaultContextHeight());
    }

    public final int parentVerticalBasis() {
        return this.parent.map(Node::getVerticalBasis).orElse(Node.getDefaultContextHeight());
    }

    public final Node withHeight(Unit h) {
        this.h = h;
        return this;
    }

    public int getVerticalBasis() {
        return getVerticalBasis(this.parentVerticalBasis());
    }

    public int getVerticalBasis(int defaultBasis) {
        return this.height(defaultBasis) - pt - pb;
    }

    public int contentHeight(int basis) {
        return this.children.stream()
            .filter(child -> !child.h.isDependentOnParent())
            .mapToInt(child -> child.localY(basis) + child.contentHeight(basis))
            .max()
            .orElse(0) + this.pt + this.pb;
    }

    public int contentHeight() {
        return this.contentHeight(this.parentVerticalBasis());
    }

    //==================================================================================================================
    //      Sides of the Node
    //==================================================================================================================

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

    //==================================================================================================================
    //      Padding
    //==================================================================================================================

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

    public Optional<Node> getNodeWithId(String id) {
        if (this.id.isPresent() && this.id.get().equals(id)) {
            return Optional.of( this );
        }

        for (Node child : this.children) {
            var result = child.getNodeWithId(id);
            if (result.isPresent()) { return result; }
        }

        return Optional.empty();
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    private void debug(GuiGraphics graphics) {
        graphics.renderOutline(this.x(), this.y(), this.width(), this.height(), FastColor.ARGB32.color(50, 255, 0, 0));
        graphics.drawString(Minecraft.getInstance().font, this.width() + ", " + this.height() + " " + this.id.orElse(""), this.x(), this.y(), FastColor.ARGB32.color(50, 255, 0, 0));
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

    public Rect rectWithPadding() {
        return new Rect(this.x() + this.getPaddingLeft(), this.y() + this.getPaddingTop(), this.width() - this.getPaddingRight(), this.height() - this.getPaddingBottom());
    }

    public static int getDefaultContextWidth() {
        return Optional.ofNullable(Minecraft.getInstance().screen).map(screen -> screen.width).orElse(Minecraft.getInstance().getWindow().getGuiScaledWidth());
    }

    public static int getDefaultContextHeight() {
        return Optional.ofNullable(Minecraft.getInstance().screen).map(screen -> screen.height).orElse(Minecraft.getInstance().getWindow().getGuiScaledHeight());
    }
}

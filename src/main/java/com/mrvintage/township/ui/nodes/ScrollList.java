package com.mrvintage.township.ui.nodes;

import com.mrvintage.township.Township;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.BlitSpriteScaling;
import com.mrvintage.township.ui.Sprites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class ScrollList extends Node {

    private int gap = 1;
    private int scroll = 0;

    private int requiredSpace = 0;

    private boolean isHorizontal = false;

    private boolean isDragging = false;
    private double currentScrollOffset = 0.0;

    private final BlitSprite scrollSprite = Sprites.PARCHMENT_SCROLL;

    private final BlitSprite scrollHandleSprite = Sprites.PARCHMENT_SCROLL_HANDLE;

    public ScrollList() {
        this.withClip();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.enableScissor(this.x(), this.y(),  this.x() + this.width(), this.y() + this.height());
        if(this.needsScrollbar()) {
            var scrollHandleArea = this.scrollHandleArea();
            scrollSprite.blit(guiGraphics, this.right() - scrollSprite.width(), this.y(), scrollSprite.width(), this.height());
            scrollHandleSprite.blit(guiGraphics, scrollHandleArea.x(), scrollHandleArea.y(), scrollHandleArea.w(), scrollHandleArea.h());
        }
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.disableScissor();

    }

    @Override
    public void layout() {
        int basis = 0;
        for (Node child : children) {
            if (this.isHorizontal) {
                child.setX(Unit.px(basis - this.getScroll()));
                basis += child.width() + this.gap;
            } else {
                child.setY(Unit.px(basis - this.getScroll()));
                basis += child.height() + this.gap;
            }
            child.layout();
        }
        this.requiredSpace = basis;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.rect().contains(mouseX, mouseY)) {
            double value = isHorizontal ? scrollX : scrollY;
            this.scroll(value * 3.0);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        this.scroll = this.getScroll();
        this.currentScrollOffset = 0.0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(this.isDragging) {
            this.scroll(-dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.scrollUpButtonArea().contains(mouseX, mouseY) && this.needsScrollbar()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.scroll(3);
        } else if (scrollDownButtonArea().contains(mouseX, mouseY) && this.needsScrollbar()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.scroll(-3);
        } else if(this.scrollHandleArea().contains(mouseX, mouseY) && this.needsScrollbar()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.isDragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Rect scrollbarArea() {
        return this.rect().inner(this.width() - scrollSprite.width(), 0, scrollSprite.width(), this.height());
    }

    private Rect scrollUpButtonArea() {
        int height = scrollSprite.scaling() instanceof BlitSpriteScaling.NineSlice nineslice ? nineslice.top() : 3;
        return this.scrollbarArea().inner(0, 0, scrollSprite.width(), height);
    }

    private Rect scrollDownButtonArea() {
        int height = scrollSprite.scaling() instanceof BlitSpriteScaling.NineSlice nineslice ? nineslice.bottom() : 3;
        Rect scrollBarArea = this.scrollbarArea();
        return scrollBarArea.inner(0, scrollBarArea.h() - height, scrollSprite.width(), height);
    }

    private Rect scrollingArea() {
        int amountTop = scrollSprite.scaling() instanceof BlitSpriteScaling.NineSlice nineslice ? nineslice.top() : 3;
        int amountBottom = scrollSprite.scaling() instanceof BlitSpriteScaling.NineSlice nineslice ? nineslice.bottom() : 3;
        return this.scrollbarArea().shrinkTop(amountTop).shrinkBottom(amountBottom);
    }

    private Rect scrollHandleArea() {
        return this.scrollingArea().inner(0, (int) ((double)this.getScroll() * this.ratio()), 1.0f, this.calcScrollHandleSize());
    }

    @Override
    public int getHorizontalBasis() {
        if (!this.isHorizontal && this.needsScrollbar()) {
            int scrollBarSize = Integer.max(scrollSprite.width(), scrollHandleSprite.width());
            return super.getHorizontalBasis() - scrollBarSize - 1;
        } else {
            return super.getHorizontalBasis();
        }
    }

    @Override
    public int getVerticalBasis() {
        if (this.isHorizontal && this.needsScrollbar()) {
            int scrollBarSize = Integer.max(scrollSprite.width(), scrollHandleSprite.width());
            return super.getHorizontalBasis() - scrollBarSize - 1;
        } else {
            return super.getVerticalBasis();
        }
    }

    private int getMaxScroll() {
        int space = this.requiredSpace;
        int basis = this.isHorizontal ? this.width() : this.height();
        return Integer.max(space - basis, 0);
    }

    private int calcScrollHandleSize() {
        int offset = scrollSprite.scaling() instanceof BlitSpriteScaling.NineSlice nineSlice
                ? nineSlice.top() + nineSlice.bottom()
                : 0;
        int maxSize = isHorizontal ? this.width() - offset : this.height() - offset;
        double ratio = this.ratio();
        return (int) (maxSize * ratio);
    }

    private double ratio() {
        int side = isHorizontal ? this.width() : this.height();
        return (double) side / (double) this.requiredSpace;
    }

    private boolean needsScrollbar() {
        int basis = this.requiredSpace;
        if(this.isHorizontal) {
            return basis <= this.width();
        } else  {
            return basis >= this.height();
        }
    }

    private void scroll(double value) {
        this.currentScrollOffset += -value * ratio();
        this.layout();
    }

    private int getScroll() {
        return Integer.min(Integer.max(this.scroll + (int) this.currentScrollOffset, 0), this.getMaxScroll());
    }
}

package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Specialty;
import com.mrvintage.township.ui.BlitSprite;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.Node;
import com.mrvintage.township.ui.nodes.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ProficiencyTab extends Node {

    private Specialty specialty;
    private boolean onRight = false;

    private OnClickListener onClick = tab -> false;

    public ProficiencyTab(Specialty specialty) {
        this.specialty = specialty;
    }

    public ProficiencyTab setOnClick(OnClickListener onClick) {
        this.onClick = onClick;
        return this;
    }

    public ProficiencyTab onLeft() {
        this.onRight = false;
        return this;
    }

    @Override
    public void layout() {
        this.withHeight(Unit.px(18));
        this.withWidth(Unit.px(16));
        super.layout();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        BlitSprite bookmark = !onRight ? Sprites.BOOKMARK_RIGHT : Sprites.BOOKMARK_LEFT;
        BlitSprite bg = this.isMouseOver(mouseX, mouseY) ? Sprites.PARCHMENT_BG_HOVER : Sprites.PARCHMENT_BG;
        bookmark.blit(guiGraphics, this.x() + 4, this.y() - 2, this.width() + 12, 18);
        bg.blit(guiGraphics, this.x()-1, this.y()-1, 16, 16);

        Item item = BuiltInRegistries.ITEM.get(this.specialty.icon());
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
        guiGraphics.renderItem(
                new ItemStack(item, 1),
                (int) ((float) (this.x() + 1) / 0.75f),
                (int) ((float) (this.y() + 1) / 0.75f));
        guiGraphics.pose().popPose();
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }

    public interface OnClickListener {
        boolean onClick(ProficiencyTab tab);
    }
}

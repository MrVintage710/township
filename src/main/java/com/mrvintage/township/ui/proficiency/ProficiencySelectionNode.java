package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Profession;
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

import java.util.Objects;

public class ProficiencySelectionNode extends Node {

    private final Profession profession;
    private final ProficiencyScreen screen;

    public ProficiencySelectionNode(Profession profession, ProficiencyScreen screen) {
        this.profession = profession;
        this.screen = screen;
    }

    @Override
    public void layout() {
        this.setHeight(Unit.px(18));
        super.layout();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        boolean isHovered = this.isMouseOver(mouseX, mouseY) || Objects.equals(screen.getSelectedProficiency(), profession);
        BlitSprite bg = isHovered ? Sprites.PARCHMENT_BG_HOVER : Sprites.PARCHMENT_BG;
        BlitSprite iconBorder = isHovered ? Sprites.SEWN_BORDER_HOVER : Sprites.SEWN_BORDER;
        bg.blit(guiGraphics, this.x(), this.y() + 1,  this.width(), 16);
        iconBorder.blit(guiGraphics, this.x(), this.y(), 18, 18);
        Item item = BuiltInRegistries.ITEM.get(this.profession.icon());
        guiGraphics.renderItem(new ItemStack(item, 1), this.x() + 1, this.y() + 1);
        guiGraphics.drawString(Minecraft.getInstance().font, this.profession.name(), this.x() + 20, this.y() + 4, 0, false);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        screen.setSelectedProficiency(profession);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
}

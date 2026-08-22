package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.ui.nodes.Node;
import net.minecraft.client.gui.GuiGraphics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SpecialtyTabs extends Node {
    private Profession profession;
    private ProficiencyScreen proficiencyScreen;

    public SpecialtyTabs(ProficiencyScreen screen, Profession profession) {
        this.proficiencyScreen = screen;
        this.setProficiency(profession);
    }

    public SpecialtyTabs setProficiency(Profession profession) {
        this.profession = profession;
        this.children.clear();
        AtomicInteger index = new AtomicInteger(0);
        this.withChildren(this.profession
                .specialties()
                .entrySet()
                .stream()
                .map(entry -> new SpecialtyTab(this.proficiencyScreen, entry.getValue(), entry.getKey()).withPos(0, index.getAndIncrement() * 21))
                .collect(Collectors.toList())
        );
        return this;
    }

    @Override
    protected void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {}
}

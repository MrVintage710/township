package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.*;
import net.minecraft.network.chat.Component;


import java.util.List;
import java.util.stream.Collectors;

public class ProficiencyScreen extends NodeScreen {

    private Profession selectedProfession;
    private SpecialtyTabs specialtyTabs;

    private MeritTree tree;

    private String selectedSpeciality;

    public ProficiencyScreen(Component title) {
        super(title);
    }

    @Override
    protected Node root() {
        List<Profession> proficiencies = Profession.all();

        this.selectedProfession = proficiencies.getFirst();
        this.selectedSpeciality = this.selectedProfession.specialties().firstKey();
        this.tree = (MeritTree) new MeritTree(this).withRect(Unit.px(138), Unit.px(0), Unit.px(128), Unit.px(170)).debugMode();
        this.specialtyTabs = (SpecialtyTabs) new SpecialtyTabs(this, this.selectedProfession).withPos(Unit.percent(1.0f), Unit.px(8));

        var ui = new BlitSpriteNode(Sprites.PROFICIENCIES_BG).centered()
            .withPadding(15, 15, 16, 15)
            .withChildren(
                new ScrollList().withRect(0, 0, 128, 169).withChildren(
                    proficiencies.stream().map(proficiency -> new ProficiencySelectionNode(proficiency, this)).collect(Collectors.toUnmodifiableList())
                ),
                tree,
                specialtyTabs
            );

        this.tree.setPopupRect(ui.rectWithPadding().inner(-25, -10, 158, 189));

        return ui;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public Profession getSelectedProficiency() {
        return selectedProfession;
    }

    public ProficiencyScreen setSelectedProficiency(Profession selectedProfession) {
        this.selectedProfession = selectedProfession;
        specialtyTabs.setProficiency(selectedProfession);
        this.setSelectedSpeciality(selectedProfession.specialties().firstKey());
        return this;
    }

    public ProficiencyScreen setSelectedSpeciality(String selectedSpeciality) {
        this.selectedSpeciality = selectedSpeciality;
        this.tree.update();
        return this;
    }

    public String getSelectedSpeciality() {
        return selectedSpeciality;
    }
}

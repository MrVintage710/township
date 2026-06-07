package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.ui.Sprites;
import com.mrvintage.township.ui.nodes.BlitSpriteNode;
import com.mrvintage.township.ui.nodes.Node;
import com.mrvintage.township.ui.nodes.NodeScreen;
import com.mrvintage.township.ui.nodes.ScrollList;
import net.minecraft.network.chat.Component;


import java.util.List;
import java.util.stream.Collectors;

public class ProficiencyScreen extends NodeScreen {

    private Profession selectedProfession;

    private ProficiencyTabs selectedProficiencyTabs;

    public ProficiencyScreen(Component title) {
        super(title);
    }

    @Override
    protected Node root() {
        List<Profession> proficiencies = Profession.all();

        this.selectedProfession = proficiencies.getFirst();
        selectedProficiencyTabs = (ProficiencyTabs) new ProficiencyTabs(this.selectedProfession).withPos(1.0f, 8);

        return new BlitSpriteNode(Sprites.PROFICIENCIES_BG).centered()
            .withPadding(15, 15, 16, 15)
            .withChildren(
                new ScrollList().withRect(0, 0, 128, 169).withChildren(
                    proficiencies.stream().map(proficiency -> new ProficiencySelectionNode(proficiency, this)).collect(Collectors.toUnmodifiableList())
                ),
                selectedProficiencyTabs
            );
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
        selectedProficiencyTabs.setProficiency(selectedProfession);
        return this;
    }
}

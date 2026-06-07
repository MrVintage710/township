package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.ui.nodes.Node;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProficiencyTabs extends Node {
    private Profession profession;

    public ProficiencyTabs(Profession profession) {
        this.setProficiency(profession);
    }

    public ProficiencyTabs setProficiency(Profession profession) {
        this.profession = profession;
        this.children.clear();
        AtomicInteger index = new AtomicInteger(0);
        this.withChildren(this.profession
                .specialties()
                .values()
                .stream()
                .map(p -> new ProficiencyTab(p).withPos(0, index.getAndIncrement() * 21))
                .collect(Collectors.toList())
        );
        return this;
    }
}

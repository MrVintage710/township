package com.mrvintage.township.ui.proficiency;

import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.ui.nodes.Node;
import com.mrvintage.township.ui.nodes.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MeritTree extends Node {

    private final ProficiencyScreen screen;

    private class TreeNode {
        public final boolean isComplete;
        public Set<Merit.Path> parents = new LinkedHashSet<>();
        public Set<Merit.Path> children = new LinkedHashSet<>();

        public Optional<Integer> level;

        public float order = 0.0f;

        public TreeNode(boolean isComplete) {
            this.isComplete = isComplete;
        }

        public boolean addParents(List<Merit.Path> paths) {
            return parents.addAll(paths);
        }

        public boolean addChildren(List<Merit.Path> paths) {
            return children.addAll(paths);
        }
    }

    private HashMap<Merit.Path, TreeNode> nodes = new HashMap<>();

    private List<HashSet<Merit.Path>> levels = new ArrayList<>();

    public MeritTree(ProficiencyScreen screen) {
        this.screen = screen;
    }

    @Override
    public void layout() {
        update();
        super.layout();
    }

    public void update() {
        this.levels.clear();
        this.nodes.clear();
        this.levels.add(new LinkedHashSet<>());
        AtomicReference<Float> order = new AtomicReference<>(0f);
        for (Merit.Path path : ProfessionProgress.ClientProfessionProgress.progressFromSpecialty(this.screen.getSelectedSpeciality()).keySet()) {
            addMeritToTree(path, false, order);
        }

        for (Merit.Path path : ProfessionProgress.ClientProfessionProgress.doneFromSpecialty(this.screen.getSelectedSpeciality())) {
            addMeritToTree(path, true, order);
        }

        this.fillLevels(1);

        this.children.clear();
        var popupRect = this.rect().inner(-159, -10, 159,190);
        for (int i = 0; i < this.levels.size(); i++) {
            var level = this.levels.get(i);
            int finalI = i;
            level.stream()
                .map(path -> new AbstractMap.SimpleEntry<>(path, this.nodes.get(path)))
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .sorted((a, b) -> Float.compare(a.getValue().order, b.getValue().order))
                .forEach(entry -> {
                    int y = 4 + (24 * finalI);
                    int x = this.width() / 2;

                    this.withChildren(new MeritTreeNode(entry.getKey(), popupRect, entry.getValue().isComplete).withPos(x, y));
                });
        }
    }

    private void addMeritToTree(Merit.Path path, boolean isComplete, AtomicReference<Float> order) {
        Merit merit = Profession.findMerit(path);
        if (merit == null) {
            return;
        }

        TreeNode node = new TreeNode(isComplete);
        if (merit.prereqs().isEmpty()) {
            this.levels.getFirst().add(path);
            node.level = Optional.of(0);
            node.order = order.get();
            order.set(order.get() + 1);
        } else {
            node.addParents(merit.prereqs());
        }
        node.addChildren(
            Profession.allMerits().entrySet()
                .stream()
                .filter(entry -> entry.getValue().prereqs().contains(path))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
        );
        this.nodes.put(path, node);
    }

    private void fillLevels(int startLevel) {
        this.levels.add(startLevel, new HashSet<>());
        HashSet<Merit.Path> level = this.levels.get(startLevel);
        HashSet<Merit.Path> lastLevel = this.levels.get(startLevel - 1);
        if (lastLevel == null) { return; }

        var candidates = lastLevel.stream().map(node -> this.nodes.get(node).children).flatMap(Set::stream).collect(Collectors.toSet());
        if (candidates.isEmpty()) { return; }
        for (Merit.Path path : candidates) {

            TreeNode node = this.nodes.get(path);
            if (node == null) { continue; }

            // Check to make sure all parents are placed in previous levels
            boolean allParentsArePlaced = node.parents.stream().map(this.nodes::get).allMatch(n -> n.level.isPresent());
            if (allParentsArePlaced) {

                float order = node.parents.stream().reduce(0.0f, (acc, p) -> {
                    TreeNode n = this.nodes.get(p);
                    return n.order + acc;
                }, Float::sum) / (float) node.parents.size();

                level.add(path);
                node.level = Optional.of(startLevel);
                node.order = order;
            }

        }

        fillLevels(startLevel + 1);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

    }
}

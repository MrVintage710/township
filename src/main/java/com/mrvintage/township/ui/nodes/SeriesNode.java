package com.mrvintage.township.ui.nodes;

public class SeriesNode extends Node {

    private int gap = 1;
    private boolean isHorizontal = true;

    @Override
    public void layout() {
        int currentBasis = 0;
        for(Node child : children) {
            if(isHorizontal) {
                child.setX(Unit.px(currentBasis));
                child.setY(Unit.px(0));
                currentBasis += child.width() + gap;
            } else {
                child.setY(Unit.px(currentBasis));
                child.setX(Unit.px(0));
                currentBasis += child.height() + gap;
            }
        }
        super.layout();
    }

    public SeriesNode withGap(int gap) {
        this.gap = gap;
        return this;
    }

    public SeriesNode vertical() {
        this.isHorizontal = false;
        return this;
    }
}

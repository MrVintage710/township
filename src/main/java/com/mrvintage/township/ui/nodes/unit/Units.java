package com.mrvintage.township.ui.nodes.unit;

import com.mrvintage.township.ui.nodes.Node;

public class Units {

    public record Px(int px) implements Unit {
        @Override
        public int calc(Node node) {
            return this.px;
        }
    }

//    public record Percent(float percent) implements Unit {
//
//        @Override
//        public int calc(Node node) {
//            return node.;
//        }
//    }
}

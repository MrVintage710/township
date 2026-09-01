package com.mrvintage.township.ui;

public interface BlitSpriteScaling {

    Type type();

    BlitSpriteScaling DEFAULT = new Stretch();

    record Stretch() implements BlitSpriteScaling {

        @Override
        public Type type() {
            return Type.STRETCH;
        }
    }

    record NineSlice(int left, int right, int top, int bottom) implements BlitSpriteScaling {

        @Override
        public Type type() {
            return Type.NINE_SLICE;
        }
    }

    enum Type {
        STRETCH,
        TILE,
        NINE_SLICE
    }

}

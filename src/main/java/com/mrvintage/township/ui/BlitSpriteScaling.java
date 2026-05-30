package com.mrvintage.township.ui;

public interface BlitSpriteScaling {

    Type type();

    public static final BlitSpriteScaling DEFAULT = new Stretch();

    public static record Stretch() implements BlitSpriteScaling {

        @Override
        public Type type() {
            return Type.STRETCH;
        }
    }

    public static record NineSlice(int left, int right, int top, int bottom) implements BlitSpriteScaling {

        @Override
        public Type type() {
            return Type.NINE_SLICE;
        }
    }

    public static enum Type {
        STRETCH,
        TILE,
        NINE_SLICE
    }

}

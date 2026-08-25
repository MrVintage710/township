package com.mrvintage.township.ui.nodes;

public interface Unit {

    class Percent implements Unit {

        private float percent;
        private boolean isDirty = false;
        private int cached = 0;

        public Percent(float percent) {
            this.percent = percent;
        }

        @Override
        public int calc(int basis) {
            if (!this.isDirty) {
                this.cached = (int) ((float) basis * this.percent);
            }

            return this.cached;
        }
    }

    record Px(int px) implements Unit {
        @Override
        public int calc(int basis) {
            return px;
        }
    }

    record Auto() implements Unit {
        @Override
        public int calc(int basis) { return 0; }

        @Override
        public boolean isAuto() {
            return true;
        }
    }

    int calc(int basis);

    default boolean isAuto() { return false; }

    static Unit.Px px(int px) {
        return new Unit.Px(px);
    }

    static Unit.Percent percent(float percent) {
        return new Unit.Percent(percent);
    }

    static Unit.Auto auto() { return new Unit.Auto(); }
}

package com.mrvintage.township.ui.widgets;

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
                this.cached = (int) ((float) basis * this.percent)
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

    int calc(int basis);

    static Unit.Px px(int px) {
        return new Unit.Px(px);
    }

    static Unit.Percent percent(float percent) {
        return new Unit.Percent(percent);
    }
}

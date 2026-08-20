package com.mrvintage.township.util.timeline;

public interface TimelineRange {

    /// Called when this range should be updated. "progress" will be a number between 0.0 and 1.0
    void tick(float progress);
}

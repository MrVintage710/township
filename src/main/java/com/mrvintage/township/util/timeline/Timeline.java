package com.mrvintage.township.util.timeline;

import com.mrvintage.township.Township;
import com.mrvintage.township.util.Easing;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Timeline {

    private record TimelineRangeEntry(float start, float end, TimelineRange range, Easing easing) {
        public boolean overlaps(@NotNull Timeline.TimelineRangeEntry other) {
            return this.start <= other.end && this.end >= other.start;
        }

        public boolean contains(float instant) {
            return this.start <= instant && this.end >= instant;
        }
    }

    private record TimelineEvent(float instant, Runnable callback) {}

    private final float runtime;
    private final List<TimelineRangeEntry> timeline = new ArrayList<>();
    private final List<TimelineEvent> events = new ArrayList<>();
    private float elapsed = 0.0f;
    private boolean isDone = false;
    private boolean hasStarted = false;

    public Timeline(float runtime) {
        this.runtime = runtime;
    }

    public void tick(float delta) {
        this.hasStarted = true;
        for (TimelineEvent event : events) {
            if (this.elapsed <= event.instant && this.elapsed + delta > event.instant) {
                event.callback.run();
            }
        }
        if (this.isDone) { return; }
        for (TimelineRangeEntry entry : timeline) {
            float start = entry.start < 0 ? this.runtime + entry.start : entry.start;
            float end = entry.end < 0 ? this.runtime + entry.end : entry.end;
            if (entry.contains(this.elapsed)) {
                float progress = Math.min(this.linearMap(start, end, this.elapsed), 1.0f);
                entry.range.tick(entry.easing.ease(progress));
            } else if (this.elapsed <= entry.end && this.elapsed + delta > entry.end) {
                entry.range.tick(entry.easing.ease(1.0f));
            }
        }
        elapsed += delta;
        if (elapsed > this.runtime) { this.isDone = true; }
    }

    public Timeline addToTimeline(float start, float end, TimelineRange event) {
        this.timeline.add(new TimelineRangeEntry(start, end, event, Easing.Linear));
        return this;
    }

    public Timeline addToTimeline(float start, float end, TimelineRange event, Easing easing) {
        this.timeline.add(new TimelineRangeEntry(start, end, event, easing));
        return this;
    }

    public Timeline addEvent(float instant, Runnable callback) {
        this.events.add(new TimelineEvent(instant, callback));
        return this;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void reset() {
        this.elapsed = 0.0f;
        this.isDone = false;
        this.hasStarted = false;
    }

    private float linearMap(float start, float end, float value) {
        return (value - start) / (end - start);
    }
}

package com.mrvintage.township.ui.nodes;

public class NodeUi {

    public enum VerticalAlign {
        TOP,
        BOTTOM,
        CENTER
    }

    public enum HorizontalAlign {
        LEFT,
        RIGHT,
        CENTER
    }

    public record Color(float r, float g, float b, float a) {}
}

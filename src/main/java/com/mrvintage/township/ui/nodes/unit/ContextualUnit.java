package com.mrvintage.township.ui.nodes.unit;

import com.mrvintage.township.ui.nodes.Node;

public interface ContextualUnit<T> extends Unit {
    T getContext(Node node, boolean isHorizontal);
}

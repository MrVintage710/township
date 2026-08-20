package com.mrvintage.township.util;

public interface Easing {

    Easing Linear = progress -> progress;
    /// [Ease In and Out Quadratic](https://easings.net/#easeInOutQuad)
    Easing easeInOutQuad = progress -> progress < 0.5f ? 2f * progress * progress : 1f - (float) Math.pow(-2f * progress + 2f, 2f) / 2f;
    ///  [Ease Out Cubic](https://easings.net/#easeOutCubic)
    Easing easeOutCubic = progress -> 1f - (float) Math.pow(1f - progress, 3f);

    float ease(float progress);
}

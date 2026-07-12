package com.textaura.social;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

final class SafeTypefaceSpan extends MetricAffectingSpan {
    private final String fontId;
    private final String family;
    private final int requestedStyle;

    SafeTypefaceSpan(String fontId, String family, int requestedStyle) {
        this.fontId = fontId == null ? "system" : fontId;
        this.family = family == null || family.trim().isEmpty() ? "sans-serif" : family;
        this.requestedStyle = requestedStyle;
    }

    String getFontId() {
        return fontId;
    }

    String getFamily() {
        return family;
    }

    int getRequestedStyle() {
        return requestedStyle;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        apply(paint);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        apply(paint);
    }

    private void apply(Paint paint) {
        Typeface old = paint.getTypeface();
        int inheritedStyle = old == null ? Typeface.NORMAL : old.getStyle();
        int combinedStyle = inheritedStyle | requestedStyle;
        Typeface resolved = Typeface.create(family, combinedStyle);
        if (resolved == null) resolved = Typeface.create(Typeface.DEFAULT, combinedStyle);

        int missing = combinedStyle & ~resolved.getStyle();
        paint.setFakeBoldText((missing & Typeface.BOLD) != 0);
        paint.setTextSkewX((missing & Typeface.ITALIC) != 0 ? -0.25f : 0f);
        paint.setTypeface(resolved);
    }
}

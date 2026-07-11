package com.textaura.social;

import android.os.Build;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

final class ColorUnderlineSpan extends CharacterStyle implements UpdateAppearance {
    private final int color;

    ColorUnderlineSpan(int color) {
        this.color = color;
    }

    int getColor() {
        return color;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            float thickness = Math.max(2f, paint.getTextSize() / 14f);
            paint.setUnderlineText(color, thickness);
        } else {
            paint.setUnderlineText(true);
        }
    }
}

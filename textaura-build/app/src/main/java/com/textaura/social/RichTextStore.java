package com.textaura.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;

import org.json.JSONArray;
import org.json.JSONObject;

final class RichTextStore {
    private static final String PREFS = "textaura";
    private static final String KEY_RICH_DRAFT = "rich_draft_v3";
    private static final String KEY_LEGACY_DRAFT = "draft";

    static final class SavedState {
        final SpannableStringBuilder text;
        final int selectionStart;
        final int selectionEnd;

        SavedState(SpannableStringBuilder text, int selectionStart, int selectionEnd) {
            this.text = text;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }
    }

    private RichTextStore() {}

    static void save(Context context, Spanned text, int selectionStart, int selectionEnd) {
        if (context == null || text == null) return;
        try {
            JSONObject root = new JSONObject();
            root.put("text", text.toString());
            root.put("selectionStart", clamp(selectionStart, 0, text.length()));
            root.put("selectionEnd", clamp(selectionEnd, 0, text.length()));
            JSONArray spans = new JSONArray();

            for (ForegroundColorSpan span : text.getSpans(0, text.length(), ForegroundColorSpan.class)) {
                add(spans, "foreground", text, span, span.getForegroundColor(), null, 0);
            }
            for (BackgroundColorSpan span : text.getSpans(0, text.length(), BackgroundColorSpan.class)) {
                add(spans, "background", text, span, span.getBackgroundColor(), null, 0);
            }
            for (ColorUnderlineSpan span : text.getSpans(0, text.length(), ColorUnderlineSpan.class)) {
                add(spans, "underlineColor", text, span, span.getColor(), null, 0);
            }
            for (UnderlineSpan span : text.getSpans(0, text.length(), UnderlineSpan.class)) {
                add(spans, "underline", text, span, 0, null, 0);
            }
            for (StrikethroughSpan span : text.getSpans(0, text.length(), StrikethroughSpan.class)) {
                add(spans, "strike", text, span, 0, null, 0);
            }
            for (SafeTypefaceSpan span : text.getSpans(0, text.length(), SafeTypefaceSpan.class)) {
                JSONObject object = base("font", text, span);
                object.put("id", span.getFontId());
                object.put("family", span.getFamily());
                object.put("style", span.getRequestedStyle());
                spans.put(object);
            }
            for (AlignmentSpan.Standard span : text.getSpans(0, text.length(), AlignmentSpan.Standard.class)) {
                JSONObject object = base("alignment", text, span);
                object.put("alignment", span.getAlignment().name());
                spans.put(object);
            }
            for (JustifySpan span : text.getSpans(0, text.length(), JustifySpan.class)) {
                spans.put(base("justify", text, span));
            }

            root.put("spans", spans);
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_RICH_DRAFT, root.toString())
                .putString(KEY_LEGACY_DRAFT, text.toString())
                .apply();
        } catch (Exception ignored) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LEGACY_DRAFT, text.toString())
                .apply();
        }
    }

    static SavedState load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = preferences.getString(KEY_RICH_DRAFT, null);
        if (raw == null || raw.trim().isEmpty()) {
            String legacy = preferences.getString(KEY_LEGACY_DRAFT, "");
            return new SavedState(new SpannableStringBuilder(legacy), legacy.length(), legacy.length());
        }
        try {
            JSONObject root = new JSONObject(raw);
            String value = root.optString("text", "");
            SpannableStringBuilder builder = new SpannableStringBuilder(value);
            JSONArray spans = root.optJSONArray("spans");
            if (spans != null) {
                for (int i = 0; i < spans.length(); i++) {
                    JSONObject object = spans.optJSONObject(i);
                    if (object == null) continue;
                    int start = clamp(object.optInt("start", 0), 0, builder.length());
                    int end = clamp(object.optInt("end", start), start, builder.length());
                    if (end <= start) continue;
                    String type = object.optString("type", "");
                    Object span = createSpan(type, object);
                    if (span == null) continue;
                    int flags = (span instanceof AlignmentSpan || span instanceof JustifySpan)
                        ? Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        : Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
                    builder.setSpan(span, start, end, flags);
                }
            }
            int selectionStart = clamp(root.optInt("selectionStart", builder.length()), 0, builder.length());
            int selectionEnd = clamp(root.optInt("selectionEnd", selectionStart), 0, builder.length());
            return new SavedState(builder, Math.min(selectionStart, selectionEnd), Math.max(selectionStart, selectionEnd));
        } catch (Exception ignored) {
            String legacy = preferences.getString(KEY_LEGACY_DRAFT, "");
            return new SavedState(new SpannableStringBuilder(legacy), legacy.length(), legacy.length());
        }
    }

    static void clear(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_RICH_DRAFT)
            .remove(KEY_LEGACY_DRAFT)
            .apply();
    }

    private static Object createSpan(String type, JSONObject object) {
        switch (type) {
            case "foreground": return new ForegroundColorSpan(object.optInt("value"));
            case "background": return new BackgroundColorSpan(object.optInt("value"));
            case "underlineColor": return new ColorUnderlineSpan(object.optInt("value"));
            case "underline": return new UnderlineSpan();
            case "strike": return new StrikethroughSpan();
            case "font": return new SafeTypefaceSpan(
                object.optString("id", "system"),
                object.optString("family", "sans-serif"),
                object.optInt("style", 0)
            );
            case "alignment":
                try {
                    return new AlignmentSpan.Standard(Layout.Alignment.valueOf(object.optString("alignment", Layout.Alignment.ALIGN_NORMAL.name())));
                } catch (Exception ignored) {
                    return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
                }
            case "justify": return new JustifySpan();
            default: return null;
        }
    }

    private static void add(JSONArray spans, String type, Spanned text, Object span, int value, String stringValue, int style) throws Exception {
        JSONObject object = base(type, text, span);
        if (value != 0 || "foreground".equals(type) || "background".equals(type) || "underlineColor".equals(type)) object.put("value", value);
        if (stringValue != null) object.put("string", stringValue);
        if (style != 0) object.put("style", style);
        spans.put(object);
    }

    private static JSONObject base(String type, Spanned text, Object span) throws Exception {
        JSONObject object = new JSONObject();
        object.put("type", type);
        object.put("start", text.getSpanStart(span));
        object.put("end", text.getSpanEnd(span));
        return object;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}

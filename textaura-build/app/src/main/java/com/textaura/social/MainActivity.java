package com.textaura.social;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "textaura";
    private static final String KEY_LANG = "lang";
    private static final String KEY_DRAFT = "draft";
    private static final int MODE_TEXT = 1;
    private static final int MODE_HIGHLIGHT = 2;
    private static final int MODE_UNDERLINE = 3;

    private static final int[] PALETTE = {
        0xFFF5F3FF,0xFFFDF2F8,0xFFEFF6FF,0xFFF0FDFA,0xFFFFFBEB,0xFFFFF1F2
    };
    private static final int[] COLORS = {
        0xFF111827,0xFF6B7280,0xFFDC2626,0xFFF97316,0xFFEAB308,0xFF16A34A,
        0xFF14B8A6,0xFF0284C7,0xFF2563EB,0xFF7C3AED,0xFFC026D3,0xFFDB2777,
        0xFF7C2D12,0xFF0F766E,0xFF4338CA,0xFFBE123C,0xFF000000,0xFFFFFFFF
    };

    private EditText editor;
    private TextView preview;
    private TextView count;
    private final Deque<TextState> undoStack = new ArrayDeque<>();
    private final Deque<TextState> redoStack = new ArrayDeque<>();
    private TextState typingBefore;
    private boolean internal;
    private int langIndex;

    @Override
    protected void attachBaseContext(Context base) {
        String code = base.getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_LANG, null);
        if (code == null) {
            String detected = Locale.getDefault().getLanguage();
            code = isSupported(detected) ? detected : "fr";
        }
        Locale locale = Locale.forLanguageTag(code);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(base.getResources().getConfiguration());
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        super.attachBaseContext(base.createConfigurationContext(configuration));
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        langIndex = indexOf(currentCode());
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(0xFFF4F1FF);
        setContentView(build());
        restore(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        restore(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (editor != null) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString(KEY_DRAFT, editor.getText().toString()).apply();
        }
    }

    private View build() {
        LinearLayout root = col();
        root.setPadding(dp(12), dp(10), dp(12), 0);
        root.setBackground(gradient(0xFFF9F7FF, 0xFFECFAFF, 0));

        root.addView(header(), new LinearLayout.LayoutParams(-1, -2));
        gap(root, 8);
        root.addView(editorCard(), new LinearLayout.LayoutParams(-1, -2));
        gap(root, 8);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout body = col();
        body.setPadding(0, 0, 0, dp(30));
        body.addView(stylesCard());
        gap(body, 12);
        body.addView(formatsCard());
        gap(body, 12);
        body.addView(colorsCard());
        gap(body, 12);
        body.addView(previewCard());
        gap(body, 12);
        body.addView(shareCard());
        gap(body, 10);
        TextView privacy = text(t("privacy"), 12, 0xFF667085);
        privacy.setGravity(Gravity.CENTER);
        body.addView(privacy);
        scroll.addView(body);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        return root;
    }

    private View header() {
        LinearLayout box = row();
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(15), dp(12), dp(15), dp(12));
        box.setBackground(gradient(0xFF7C3AED, 0xFFEC4899, 24));
        box.setElevation(dp(5));

        TextView logo = text("Aa✦", 21, 0xFF7C3AED);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(round(Color.WHITE, 17, 0, 0));
        box.addView(logo, new LinearLayout.LayoutParams(dp(58), dp(50)));

        LinearLayout titles = col();
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, -2, 1);
        titleParams.setMargins(dp(11), 0, dp(8), 0);
        box.addView(titles, titleParams);
        TextView name = text("TextAura", 24, Color.WHITE);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        titles.addView(name);
        titles.addView(text(t("tag"), 13, 0xEEFFFFFF));

        Button language = button(currentCode().toUpperCase(Locale.ROOT), 0x30FFFFFF, Color.WHITE, 13);
        language.setOnClickListener(v -> languages());
        box.addView(language, new LinearLayout.LayoutParams(dp(58), dp(42)));
        return box;
    }

    private View editorCard() {
        LinearLayout card = card();
        LinearLayout line = row();
        line.setGravity(Gravity.CENTER_VERTICAL);
        line.addView(title(t("your_text")), new LinearLayout.LayoutParams(0, -2, 1));
        count = text("0 " + t("chars"), 12, 0xFF667085);
        line.addView(count);
        card.addView(line);

        TextView tip = text(t("tip"), 11, 0xFF667085);
        tip.setPadding(0, dp(4), 0, dp(8));
        card.addView(tip);

        editor = new EditText(this);
        editor.setHint(t("hint"));
        editor.setTextSize(18);
        editor.setTextColor(0xFF1B1F3B);
        editor.setHintTextColor(0xFFA5ABC2);
        editor.setGravity(Gravity.TOP | Gravity.START);
        editor.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        editor.setMinHeight(dp(128));
        editor.setMaxHeight(dp(210));
        editor.setVerticalScrollBarEnabled(true);
        editor.setPadding(dp(13), dp(12), dp(13), dp(12));
        editor.setBackground(round(0xFFFAFAFF, 16, 0xFFE1E1F0, 1));
        card.addView(editor, new LinearLayout.LayoutParams(-1, -2));

        editor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!internal) typingBefore = capture();
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                if (!internal && typingBefore != null && !typingBefore.text.toString().equals(editable.toString())) {
                    pushUndo(typingBefore);
                    redoStack.clear();
                }
                typingBefore = null;
                refresh();
            }
        });

        LinearLayout actions = row();
        actions.setPadding(0, dp(10), 0, 0);
        addEqualAction(actions, "↶\n" + t("undo"), 0xFFF5F3FF, v -> undoAction(), 0);
        addEqualAction(actions, "↷\n" + t("redo"), 0xFFF0FDFA, v -> redoAction(), 7);
        addEqualAction(actions, "😊\n" + t("emoji"), 0xFFEFF6FF, v -> emojis(), 7);
        addEqualAction(actions, "⌫\n" + t("clear"), 0xFFFFF1F2, v -> clearText(), 7);
        card.addView(actions);
        return card;
    }

    private View stylesCard() {
        LinearLayout card = card();
        card.addView(title(t("styles")));
        gap(card, 8);
        card.addView(styleRow(new Item[] {
            new Item("Abc", "normal", TextStyle.NORMAL),
            new Item("𝗔𝗯𝗰", "bold", TextStyle.BOLD),
            new Item("𝘈𝘣𝘤", "italic", TextStyle.ITALIC),
            new Item("𝘼𝙗𝙘", "bolditalic", TextStyle.BOLDITALIC),
            new Item("𝐀𝐛𝐜", "serif", TextStyle.SERIF),
            new Item("𝙰𝚋𝚌", "mono", TextStyle.MONO),
            new Item("𝔸𝕓𝕔", "double", TextStyle.DOUBLE),
            new Item("𝒜𝒷𝒸", "script", TextStyle.SCRIPT),
            new Item("ᴀʙᴄ", "small", TextStyle.SMALL),
            new Item("Ａｂｃ", "full", TextStyle.FULL),
            new Item("Ⓐⓑⓒ", "circle", TextStyle.CIRCLE)
        }));

        TextView decoration = title(t("decor"));
        decoration.setPadding(0, dp(17), 0, dp(8));
        card.addView(decoration);
        card.addView(styleRow(new Item[] {
            new Item("A̲b̲c̲", "underline", TextStyle.UNDER),
            new Item("A̶b̶c̶", "strike", TextStyle.STRIKE),
            new Item("A̅b̅c̅", "over", TextStyle.OVER),
            new Item("▰ Abc", "highlight", TextStyle.HIGHLIGHT),
            new Item("✨ Abc", "spark", TextStyle.SPARK),
            new Item("💖 Abc", "heart", TextStyle.HEART),
            new Item("【Abc】", "frame", TextStyle.FRAME)
        }));
        return card;
    }

    private View formatsCard() {
        LinearLayout card = card();
        card.addView(title(t("formats")));

        addSection(card, t("casing"));
        card.addView(actionRow(new ActionItem[] {
            new ActionItem("Abc.", "sentence", "sentence"),
            new ActionItem("abc", "lower", "lower"),
            new ActionItem("ABC", "upper", "upper"),
            new ActionItem("Abc Def", "title", "title"),
            new ActionItem("aBC", "toggle", "toggle"),
            new ActionItem("AbCd", "sponge", "sponge")
        }));

        addSection(card, t("naming"));
        card.addView(actionRow(new ActionItem[] {
            new ActionItem("a_b_c", "snake", "snake"),
            new ActionItem("a-b-c", "kebab", "kebab"),
            new ActionItem("aBc", "camel", "camel"),
            new ActionItem("ABc", "pascal", "pascal")
        }));

        addSection(card, t("cleanup"));
        card.addView(actionRow(new ActionItem[] {
            new ActionItem("e → e", "no_accents", "no_accents"),
            new ActionItem("a b → ab", "no_spaces", "no_spaces")
        }));
        return card;
    }

    private View colorsCard() {
        LinearLayout card = card();
        card.addView(title(t("colors")));
        gap(card, 8);
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = row();
        scroll.addView(row);
        addColorTool(row, "A", t("text_color"), 0xFFF5F3FF, () -> showColorPicker(MODE_TEXT));
        addColorTool(row, "▰", t("highlight_color"), 0xFFFFFBEB, () -> showColorPicker(MODE_HIGHLIGHT));
        addColorTool(row, "A̲", t("underline_color"), 0xFFEFF6FF, () -> showColorPicker(MODE_UNDERLINE));
        addColorTool(row, "✕", t("clear_colors"), 0xFFFFF1F2, this::clearColors);
        card.addView(scroll);
        return card;
    }

    private View previewCard() {
        LinearLayout card = card();
        card.setBackground(round(0xFFFFFDF7, 22, 0xFFF1D98C, 1));
        card.addView(title(t("preview")));
        gap(card, 8);
        preview = text(t("preview_empty"), 20, 0xFF667085);
        preview.setTextIsSelectable(true);
        preview.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        preview.setGravity(Gravity.START);
        preview.setMinHeight(dp(105));
        preview.setPadding(dp(14), dp(14), dp(14), dp(14));
        preview.setBackground(round(Color.WHITE, 16, 0xFFEEE4C6, 1));
        card.addView(preview, new LinearLayout.LayoutParams(-1, -2));
        return card;
    }

    private View shareCard() {
        LinearLayout card = card();
        HorizontalScrollView apps = new HorizontalScrollView(this);
        apps.setHorizontalScrollBarEnabled(false);
        LinearLayout appRow = row();
        apps.addView(appRow);
        social(appRow, "WhatsApp", "com.whatsapp", 0xFFE8FFF4);
        social(appRow, "LinkedIn", "com.linkedin.android", 0xFFEAF4FF);
        social(appRow, "Facebook", "com.facebook.katana", 0xFFEDF2FF);
        social(appRow, "X", "com.twitter.android", 0xFFF1F3F5);
        social(appRow, "Instagram", "com.instagram.android", 0xFFFFEFF7);
        social(appRow, "Threads", "com.instagram.barcelona", 0xFFF5F5F5);
        card.addView(apps);

        TextView note = text(t("rich_note"), 11, 0xFF667085);
        note.setPadding(0, dp(11), 0, dp(10));
        card.addView(note);

        LinearLayout actions = row();
        Button copy = button("⧉  " + t("copy"), 0xFF14B8A6, Color.WHITE, 14);
        copy.setTypeface(Typeface.DEFAULT_BOLD);
        copy.setOnClickListener(v -> copy());
        actions.addView(copy, new LinearLayout.LayoutParams(0, dp(54), 1));
        Button share = button("↗  " + t("share"), 0xFF7C3AED, Color.WHITE, 14);
        share.setTypeface(Typeface.DEFAULT_BOLD);
        share.setOnClickListener(v -> shareText(null));
        LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(0, dp(54), 1);
        shareParams.setMargins(dp(9), 0, 0, 0);
        actions.addView(share, shareParams);
        card.addView(actions);

        Button image = button("▣  " + t("share_image"), 0xFFEC4899, Color.WHITE, 15);
        image.setTypeface(Typeface.DEFAULT_BOLD);
        image.setOnClickListener(v -> shareImage(null));
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(-1, dp(55));
        imageParams.setMargins(0, dp(9), 0, 0);
        card.addView(image, imageParams);
        return card;
    }

    private View styleRow(Item[] items) {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = row();
        scroll.addView(row);
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            Button button = button(item.sample + "\n" + t(item.key), PALETTE[i % PALETTE.length], 0xFF1B1F3B, 13);
            button.setMinWidth(dp(112));
            button.setOnClickListener(v -> apply(item.style));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(70));
            if (i > 0) params.setMargins(dp(8), 0, 0, 0);
            row.addView(button, params);
        }
        return scroll;
    }

    private View actionRow(ActionItem[] items) {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = row();
        scroll.addView(row);
        for (int i = 0; i < items.length; i++) {
            ActionItem item = items[i];
            Button button = button(item.sample + "\n" + t(item.key), PALETTE[i % PALETTE.length], 0xFF1B1F3B, 12);
            button.setMinWidth(dp(116));
            button.setOnClickListener(v -> transform(item.operation));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(68));
            if (i > 0) params.setMargins(dp(8), 0, 0, 0);
            row.addView(button, params);
        }
        return scroll;
    }

    private void addSection(LinearLayout card, String label) {
        TextView section = text(label, 14, 0xFF475467);
        section.setTypeface(Typeface.DEFAULT_BOLD);
        section.setPadding(0, dp(15), 0, dp(7));
        card.addView(section);
    }

    private void addColorTool(LinearLayout row, String icon, String label, int background, Runnable action) {
        Button button = button(icon + "\n" + label, background, 0xFF1B1F3B, 12);
        button.setMinWidth(dp(128));
        button.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(68));
        if (row.getChildCount() > 0) params.setMargins(dp(8), 0, 0, 0);
        row.addView(button, params);
    }

    private void addEqualAction(LinearLayout row, String label, int background, View.OnClickListener listener, int leftMargin) {
        Button button = button(label, background, 0xFF1B1F3B, 11);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(55), 1);
        params.setMargins(dp(leftMargin), 0, 0, 0);
        row.addView(button, params);
    }

    private void social(LinearLayout row, String label, String packageName, int background) {
        Button button = button(label, background, 0xFF1B1F3B, 13);
        button.setOnClickListener(v -> shareText(packageName));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(45));
        if (row.getChildCount() > 0) params.setMargins(dp(8), 0, 0, 0);
        row.addView(button, params);
    }

    private void apply(TextStyle style) {
        if (!hasText()) return;
        if (style == TextStyle.UNDER) {
            applyPlainUnderline();
            return;
        }
        if (style == TextStyle.HIGHLIGHT) {
            showColorPicker(MODE_HIGHLIGHT);
            return;
        }

        int[] range = selectionOrAll();
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        String selected = builder.subSequence(range[0], range[1]).toString();
        String styled = UnicodeStyler.apply(selected, style);
        builder.replace(range[0], range[1], styled);
        int end = range[0] + styled.length();
        if (style == TextStyle.NORMAL) removeAllRichSpans(builder, range[0], end);
        commit(previous, builder, range[0], end);
    }

    private void applyPlainUnderline() {
        int[] range = selectionOrAll();
        TextState previous = capture();
        Editable editable = editor.getText();
        internal = true;
        removeSpans(editable, range[0], range[1], UnderlineSpan.class);
        removeSpans(editable, range[0], range[1], ColorUnderlineSpan.class);
        editable.setSpan(new UnderlineSpan(), range[0], range[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        editor.setSelection(range[0], range[1]);
        refresh();
    }

    private void transform(String operation) {
        if (!hasText()) return;
        int[] range = selectionOrAll();
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        String selected = builder.subSequence(range[0], range[1]).toString();
        String result = TextTransformer.transform(selected, operation, Locale.forLanguageTag(currentCode()));
        builder.replace(range[0], range[1], result);
        commit(previous, builder, range[0], range[0] + result.length());
    }

    private void showColorPicker(int mode) {
        if (!hasText()) return;
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(6);
        grid.setPadding(dp(12), dp(12), dp(12), dp(12));
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(t("color_title"))
            .setView(grid)
            .setNegativeButton(t("close"), null)
            .create();

        for (int baseColor : COLORS) {
            int applied = mode == MODE_HIGHLIGHT ? translucent(baseColor) : baseColor;
            TextView swatch = new TextView(this);
            swatch.setGravity(Gravity.CENTER);
            swatch.setText(baseColor == Color.WHITE ? "✓" : "");
            swatch.setTextColor(0xFF667085);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(applied);
            circle.setStroke(dp(1), 0xFFD0D5DD);
            swatch.setBackground(circle);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dp(44);
            params.height = dp(44);
            params.setMargins(dp(6), dp(6), dp(6), dp(6));
            grid.addView(swatch, params);
            swatch.setOnClickListener(v -> {
                applyColor(mode, applied);
                dialog.dismiss();
            });
        }
        dialog.show();
    }

    private void applyColor(int mode, int color) {
        int[] range = selectionOrAll();
        TextState previous = capture();
        Editable editable = editor.getText();
        internal = true;
        if (mode == MODE_TEXT) {
            editable.setSpan(new ForegroundColorSpan(color), range[0], range[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (mode == MODE_HIGHLIGHT) {
            editable.setSpan(new BackgroundColorSpan(color), range[0], range[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            removeSpans(editable, range[0], range[1], UnderlineSpan.class);
            removeSpans(editable, range[0], range[1], ColorUnderlineSpan.class);
            editable.setSpan(new ColorUnderlineSpan(color), range[0], range[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        editor.setSelection(range[0], range[1]);
        refresh();
    }

    private void clearColors() {
        if (!hasText()) return;
        int[] range = selectionOrAll();
        TextState previous = capture();
        Editable editable = editor.getText();
        internal = true;
        removeSpans(editable, range[0], range[1], ForegroundColorSpan.class);
        removeSpans(editable, range[0], range[1], BackgroundColorSpan.class);
        removeSpans(editable, range[0], range[1], ColorUnderlineSpan.class);
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        editor.setSelection(range[0], range[1]);
        refresh();
    }

    private void clearText() {
        if (editor.length() == 0) return;
        TextState previous = capture();
        setRichText("", 0, 0);
        pushUndo(previous);
        redoStack.clear();
    }

    private void undoAction() {
        if (undoStack.isEmpty()) return;
        TextState current = capture();
        TextState previous = undoStack.pop();
        redoStack.push(current);
        restoreState(previous);
    }

    private void redoAction() {
        if (redoStack.isEmpty()) return;
        TextState current = capture();
        TextState next = redoStack.pop();
        pushUndo(current);
        restoreState(next);
    }

    private void pushUndo(TextState state) {
        if (state == null) return;
        if (undoStack.size() >= 100) undoStack.removeLast();
        undoStack.push(state);
    }

    private TextState capture() {
        if (editor == null) return new TextState(new SpannableString(""), 0, 0);
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        return new TextState(new SpannableString(editor.getText()), start, end);
    }

    private void commit(TextState previous, CharSequence text, int selectionStart, int selectionEnd) {
        setRichText(text, selectionStart, selectionEnd);
        pushUndo(previous);
        redoStack.clear();
    }

    private void restoreState(TextState state) {
        setRichText(state.text, state.selectionStart, state.selectionEnd);
    }

    private void setRichText(CharSequence text, int selectionStart, int selectionEnd) {
        internal = true;
        editor.setText(text, TextView.BufferType.SPANNABLE);
        int length = editor.length();
        int start = Math.max(0, Math.min(selectionStart, length));
        int end = Math.max(0, Math.min(selectionEnd, length));
        editor.setSelection(Math.min(start, end), Math.max(start, end));
        internal = false;
        refresh();
    }

    private void refresh() {
        if (editor == null || preview == null || count == null) return;
        String raw = editor.getText().toString();
        count.setText(raw.codePointCount(0, raw.length()) + " " + t("chars"));
        if (raw.isEmpty()) {
            preview.setText(t("preview_empty"));
            preview.setTextColor(0xFF667085);
        } else {
            preview.setText(new SpannableString(editor.getText()), TextView.BufferType.SPANNABLE);
            preview.setTextColor(0xFF1B1F3B);
        }
    }

    private boolean hasText() {
        if (editor.getText().toString().trim().isEmpty()) {
            toast(t("empty"));
            return false;
        }
        return true;
    }

    private int[] selectionOrAll() {
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        if (start > end) { int swap = start; start = end; end = swap; }
        if (start == end) { start = 0; end = editor.length(); }
        return new int[] {start, end};
    }

    private void copy() {
        if (!hasText()) return;
        String value = exportPlainText();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("TextAura", value));
        toast(t("copied"));
    }

    private void shareText(String packageName) {
        if (!hasText()) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, exportPlainText());
        if (packageName != null) intent.setPackage(packageName);
        if (packageName != null && intent.resolveActivity(getPackageManager()) == null) {
            toast(t("app_missing"));
            shareText(null);
            return;
        }
        startActivity(packageName == null ? Intent.createChooser(intent, t("share_with")) : intent);
    }

    private String exportPlainText() {
        Spanned text = editor.getText();
        StringBuilder out = new StringBuilder();
        for (int index = 0; index < text.length();) {
            int cp = Character.codePointAt(text, index);
            int charCount = Character.charCount(cp);
            int end = Math.min(text.length(), index + charCount);
            boolean underlined = text.getSpans(index, end, UnderlineSpan.class).length > 0
                || text.getSpans(index, end, ColorUnderlineSpan.class).length > 0;
            out.appendCodePoint(cp);
            int type = Character.getType(cp);
            boolean combining = type == Character.NON_SPACING_MARK
                || type == Character.COMBINING_SPACING_MARK
                || type == Character.ENCLOSING_MARK;
            if (underlined && cp != '\n' && cp != '\r' && !combining) out.appendCodePoint(0x332);
            index = end;
        }
        return out.toString();
    }

    private void shareImage(String packageName) {
        if (!hasText()) return;
        try {
            Uri uri = createShareImage();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, exportPlainText());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setClipData(ClipData.newUri(getContentResolver(), "TextAura", uri));
            if (packageName != null) intent.setPackage(packageName);
            if (packageName != null && intent.resolveActivity(getPackageManager()) == null) {
                toast(t("app_missing"));
                shareImage(null);
                return;
            }
            startActivity(packageName == null ? Intent.createChooser(intent, t("share_with")) : intent);
        } catch (Exception exception) {
            toast(exception.getMessage() == null ? "Image error" : exception.getMessage());
        }
    }

    private Uri createShareImage() throws Exception {
        LinearLayout imageCard = col();
        imageCard.setPadding(dp(24), dp(22), dp(24), dp(24));
        imageCard.setBackground(round(Color.WHITE, 0, 0, 0));

        TextView brand = text("TextAura  ✦", 18, 0xFF7C3AED);
        brand.setTypeface(Typeface.DEFAULT_BOLD);
        imageCard.addView(brand);
        TextView content = text("", 23, 0xFF1B1F3B);
        content.setText(new SpannableString(editor.getText()), TextView.BufferType.SPANNABLE);
        content.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        content.setGravity(Gravity.START);
        content.setPadding(0, dp(18), 0, 0);
        imageCard.addView(content, new LinearLayout.LayoutParams(-1, -2));

        int width = Math.max(dp(300), getResources().getDisplayMetrics().widthPixels - dp(32));
        imageCard.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int height = Math.max(dp(220), imageCard.getMeasuredHeight());
        imageCard.layout(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        imageCard.draw(canvas);

        File directory = new File(getCacheDir(), "shared");
        if (!directory.exists() && !directory.mkdirs()) throw new IllegalStateException("Cache error");
        File file = new File(directory, "TextAura-publication.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
        bitmap.recycle();
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
    }

    private void restore(Intent intent) {
        String value = null;
        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            CharSequence incoming = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
            if (incoming != null) value = incoming.toString();
        }
        if (value == null) value = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_DRAFT, "");
        if (!value.isEmpty()) setRichText(value, value.length(), value.length());
    }

    private void emojis() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout root = col();
        root.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = title(t("emoji_title"));
        header.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        Button close = button("✕", 0xFFF2F4F7, 0xFF1B1F3B, 18);
        close.setOnClickListener(v -> dialog.dismiss());
        header.addView(close, new LinearLayout.LayoutParams(dp(48), dp(42)));
        root.addView(header);

        HorizontalScrollView categories = new HorizontalScrollView(this);
        categories.setHorizontalScrollBarEnabled(false);
        LinearLayout categoryRow = row();
        categories.addView(categoryRow);
        LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(-1, dp(58));
        categoryParams.setMargins(0, dp(8), 0, dp(5));
        root.addView(categories, categoryParams);

        TextView categoryTitle = text(t(EmojiData.KEYS[0]), 14, 0xFF475467);
        categoryTitle.setTypeface(Typeface.DEFAULT_BOLD);
        categoryTitle.setPadding(dp(3), dp(4), 0, dp(6));
        root.addView(categoryTitle);

        ScrollView emojiScroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setPadding(dp(2), dp(2), dp(2), dp(18));
        emojiScroll.addView(grid);
        root.addView(emojiScroll, new LinearLayout.LayoutParams(-1, 0, 1));

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columns = screenWidth >= dp(700) ? 10 : 6;
        int cell = Math.max(dp(48), (screenWidth - dp(44)) / columns);

        for (int i = 0; i < EmojiData.KEYS.length; i++) {
            final int index = i;
            Button tab = button(EmojiData.ICONS[i] + "\n" + t(EmojiData.KEYS[i]), PALETTE[i % PALETTE.length], 0xFF1B1F3B, 11);
            tab.setMinWidth(dp(116));
            tab.setOnClickListener(v -> {
                categoryTitle.setText(t(EmojiData.KEYS[index]));
                fillEmojiGrid(grid, index, columns, cell);
                emojiScroll.scrollTo(0, 0);
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(54));
            if (i > 0) params.setMargins(dp(7), 0, 0, 0);
            categoryRow.addView(tab, params);
        }

        fillEmojiGrid(grid, 0, columns, cell);
        dialog.setContentView(root);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    private void fillEmojiGrid(GridLayout grid, int category, int columns, int cell) {
        grid.removeAllViews();
        grid.setColumnCount(columns);
        for (String emoji : EmojiData.EMOJIS[category]) {
            TextView item = text(emoji, 26, Color.BLACK);
            item.setGravity(Gravity.CENTER);
            item.setBackground(round(0xFFF8F8FD, 13, 0xFFE4E4EE, 1));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cell - dp(6);
            params.height = cell - dp(6);
            params.setMargins(dp(3), dp(3), dp(3), dp(3));
            grid.addView(item, params);
            item.setOnClickListener(v -> insert(((TextView) v).getText().toString()));
        }
    }

    private void insert(String value) {
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        if (start > end) { int swap = start; start = end; end = swap; }
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        builder.replace(start, end, value);
        commit(previous, builder, start + value.length(), start + value.length());
    }

    private void languages() {
        String[] names = {"Français", "English", "Español", "Deutsch", "العربية"};
        String[] codes = {"fr", "en", "es", "de", "ar"};
        new AlertDialog.Builder(this)
            .setTitle(t("language"))
            .setSingleChoiceItems(names, langIndex, (dialog, which) -> {
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_LANG, codes[which]).apply();
                dialog.dismiss();
                recreate();
            })
            .show();
    }

    private void removeAllRichSpans(Spannable text, int start, int end) {
        removeSpans(text, start, end, ForegroundColorSpan.class);
        removeSpans(text, start, end, BackgroundColorSpan.class);
        removeSpans(text, start, end, UnderlineSpan.class);
        removeSpans(text, start, end, ColorUnderlineSpan.class);
    }

    private <T> void removeSpans(Spannable text, int start, int end, Class<T> type) {
        T[] spans = text.getSpans(start, end, type);
        for (T span : spans) text.removeSpan(span);
    }

    private int translucent(int color) {
        if (color == Color.WHITE) return 0x66FFFFFF;
        return (0x66 << 24) | (color & 0x00FFFFFF);
    }

    private String currentCode() {
        String value = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_LANG, Locale.getDefault().getLanguage());
        return isSupported(value) ? value : "fr";
    }

    private boolean isSupported(String code) {
        return "fr".equals(code) || "en".equals(code) || "es".equals(code) || "de".equals(code) || "ar".equals(code);
    }

    private int indexOf(String code) {
        if ("en".equals(code)) return 1;
        if ("es".equals(code)) return 2;
        if ("de".equals(code)) return 3;
        if ("ar".equals(code)) return 4;
        return 0;
    }

    private String t(String key) { return Texts.get(key, langIndex); }
    private void toast(String value) { Toast.makeText(this, value, Toast.LENGTH_SHORT).show(); }

    private LinearLayout col() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        return view;
    }

    private LinearLayout row() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.HORIZONTAL);
        return view;
    }

    private LinearLayout card() {
        LinearLayout card = col();
        card.setPadding(dp(15), dp(15), dp(15), dp(15));
        card.setBackground(round(Color.WHITE, 22, 0xFFE9E8F2, 1));
        card.setElevation(dp(3));
        card.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        return card;
    }

    private TextView title(String value) {
        TextView view = text(value, 18, 0xFF1B1F3B);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    private TextView text(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private Button button(String value, int background, int foreground, float size) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(value);
        button.setTextSize(size);
        button.setTextColor(foreground);
        button.setPadding(dp(10), 0, dp(10), 0);
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setBackground(round(background, 15, 0xFFE2E2EC, 1));
        return button;
    }

    private void gap(LinearLayout parent, int size) {
        View spacer = new View(this);
        parent.addView(spacer, new LinearLayout.LayoutParams(1, dp(size)));
    }

    private GradientDrawable round(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        if (strokeWidth > 0) drawable.setStroke(dp(strokeWidth), strokeColor);
        return drawable;
    }

    private GradientDrawable gradient(int start, int end, int radius) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {start, end});
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static final class TextState {
        final SpannableString text;
        final int selectionStart;
        final int selectionEnd;
        TextState(SpannableString text, int selectionStart, int selectionEnd) {
            this.text = text;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }
    }

    private static final class Item {
        final String sample;
        final String key;
        final TextStyle style;
        Item(String sample, String key, TextStyle style) {
            this.sample = sample;
            this.key = key;
            this.style = style;
        }
    }

    private static final class ActionItem {
        final String sample;
        final String key;
        final String operation;
        ActionItem(String sample, String key, String operation) {
            this.sample = sample;
            this.key = key;
            this.operation = operation;
        }
    }
}

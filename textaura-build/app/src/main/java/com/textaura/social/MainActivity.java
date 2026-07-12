package com.textaura.social;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String PREFS = "textaura";
    private static final String KEY_LANG = "lang";
    private static final String KEY_RECENT_FONTS = "recent_fonts_v3";
    private static final String KEY_FAVORITE_FONTS = "favorite_fonts_v3";
    private static final String KEY_RECENT_EMOJIS = "recent_emojis_v3";
    private static final String KEY_FAVORITE_EMOJIS = "favorite_emojis_v3";
    private static final String KEY_RECENT_COLORS = "recent_colors_v3";

    private static final int MODE_TEXT = 1;
    private static final int MODE_HIGHLIGHT = 2;
    private static final int MODE_UNDERLINE = 3;

    private static final int[] PALETTE = {
        0xFFF5F3FF, 0xFFFDF2F8, 0xFFEFF6FF, 0xFFF0FDFA, 0xFFFFFBEB, 0xFFFFF1F2
    };
    private static final int[] TEXT_COLORS = {
        0xFF111827, 0xFF6B7280, 0xFFDC2626, 0xFFF97316, 0xFFEAB308, 0xFF16A34A,
        0xFF14B8A6, 0xFF0284C7, 0xFF2563EB, 0xFF7C3AED, 0xFFC026D3, 0xFFDB2777,
        0xFF7C2D12, 0xFF0F766E, 0xFF4338CA, 0xFFBE123C, 0xFF000000, 0xFFFFFFFF
    };
    private static final int[] HIGHLIGHT_COLORS = {
        0x00FFFFFF, 0x66FFF176, 0x66F9A8D4, 0x6693C5FD, 0x6686EFAC, 0x66FCA5A5,
        0x66D8B4FE, 0x6667E8F9, 0x66FDBA74, 0x66BEF264, 0x66FFFFFF, 0x66CBD5E1
    };

    private SelectionEditText editor;
    private LinearLayout previewContainer;
    private LinearLayout selectionToolbar;
    private TextView count;
    private TextView selectedCount;
    private ScrollView contentScroll;
    private LinearLayout contentBody;
    private final Map<String, View> sectionRefs = new LinkedHashMap<>();

    private final Deque<TextState> undoStack = new ArrayDeque<>();
    private final Deque<TextState> redoStack = new ArrayDeque<>();
    private TextState typingBefore;
    private boolean internal;
    private int langIndex;
    private int savedSelectionStart = -1;
    private int savedSelectionEnd = -1;
    private boolean savedSelectionActive;

    private interface RangeAction {
        void run(int[] range);
    }

    private interface SpanCopier<T> {
        Object copy(T span);
    }

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        saveRichDraft();
        super.onPause();
    }

    private View build() {
        LinearLayout root = col();
        root.setPadding(dp(12), dp(10), dp(12), 0);
        root.setBackground(gradient(0xFFF9F7FF, 0xFFECFAFF, 0));

        root.addView(header(), new LinearLayout.LayoutParams(-1, -2));
        gap(root, 8);
        root.addView(editorCard(), new LinearLayout.LayoutParams(-1, -2));
        gap(root, 7);

        contentScroll = new ScrollView(this);
        contentScroll.setFillViewport(true);
        contentScroll.setClipToPadding(false);
        contentBody = col();
        contentBody.setPadding(0, 0, 0, dp(30));

        addSection("styles", stylesCard());
        addSection("fonts", fontsCard());
        addSection("formats", formatsCard());
        addSection("colors", colorsCard());
        addSection("paragraphs", paragraphCard());
        addSection("preview", previewCard());
        addSection("share", shareCard());

        TextView privacy = text(t("privacy"), 12, 0xFF667085);
        privacy.setGravity(Gravity.CENTER);
        privacy.setPadding(dp(8), dp(5), dp(8), dp(12));
        contentBody.addView(privacy);
        contentScroll.addView(contentBody);

        root.addView(menuBar(), new LinearLayout.LayoutParams(-1, dp(58)));
        gap(root, 5);
        root.addView(contentScroll, new LinearLayout.LayoutParams(-1, 0, 1));
        return root;
    }

    private void addSection(String id, View view) {
        sectionRefs.put(id, view);
        contentBody.addView(view, new LinearLayout.LayoutParams(-1, -2));
        gap(contentBody, 12);
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

        Button language = button(languageLabel(currentCode()), 0x30FFFFFF, Color.WHITE, 12);
        language.setOnClickListener(v -> languages());
        box.addView(language, new LinearLayout.LayoutParams(dp(82), dp(44)));
        return box;
    }

    private View editorCard() {
        LinearLayout card = sectionCard("✍", t("your_text"), 0xFF8B5CF6);
        LinearLayout line = row();
        line.setGravity(Gravity.CENTER_VERTICAL);
        count = text("0 " + t("chars"), 12, 0xFF667085);
        line.addView(count, new LinearLayout.LayoutParams(0, -2, 1));
        Button hideKeyboard = button("⌨↓", 0xFFEFF6FF, 0xFF1B1F3B, 16);
        hideKeyboard.setContentDescription(t("hide_keyboard"));
        hideKeyboard.setOnClickListener(v -> hideKeyboardPreservingSelection());
        line.addView(hideKeyboard, new LinearLayout.LayoutParams(dp(50), dp(40)));
        card.addView(line);

        TextView tip = text(t("tip"), 11, 0xFF667085);
        tip.setPadding(0, dp(5), 0, dp(8));
        card.addView(tip);

        editor = new SelectionEditText(this);
        editor.setHint(t("hint"));
        editor.setTextSize(18);
        editor.setTextColor(0xFF1B1F3B);
        editor.setHintTextColor(0xFFA5ABC2);
        editor.setHighlightColor(0x6634D399);
        editor.setGravity(Gravity.TOP | Gravity.START);
        editor.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        editor.setMinHeight(dp(126));
        editor.setMaxHeight(dp(210));
        editor.setVerticalScrollBarEnabled(true);
        editor.setPadding(dp(13), dp(12), dp(13), dp(12));
        editor.setBackground(round(0xFFFAFAFF, 16, 0xFFE1E1F0, 1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editor.setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY);
            editor.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
        }
        editor.setSelectionListener((start, end, fromTouch) -> {
            if (internal) return;
            int a = Math.min(start, end);
            int b = Math.max(start, end);
            if (a != b) rememberSelection(a, b);
            else if (fromTouch) clearRememberedSelection();
            refreshSelectionToolbar();
        });
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
                    clearRememberedSelection();
                }
                typingBefore = null;
                refresh();
            }
        });

        selectionToolbar = col();
        selectionToolbar.setVisibility(View.GONE);
        selectionToolbar.setPadding(0, dp(8), 0, 0);
        selectedCount = text("", 11, 0xFF0F766E);
        selectedCount.setTypeface(Typeface.DEFAULT_BOLD);
        selectionToolbar.addView(selectedCount);
        HorizontalScrollView contextScroll = new HorizontalScrollView(this);
        contextScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout contextRow = row();
        contextScroll.addView(contextRow);
        addContextAction(contextRow, "𝗕", t("bold"), 0xFFF5F3FF, () -> apply(TextStyle.BOLD));
        addContextAction(contextRow, "𝘐", t("italic"), 0xFFEFF6FF, () -> apply(TextStyle.ITALIC));
        addContextAction(contextRow, "U̲", t("underline"), 0xFFF0FDFA, () -> apply(TextStyle.UNDER));
        addContextAction(contextRow, "🎨", t("text_color"), 0xFFFDF2F8, () -> showColorPicker(MODE_TEXT));
        addContextAction(contextRow, "𝑻", t("fonts"), 0xFFFFFBEB, this::showFontPicker);
        addContextAction(contextRow, "⧉", t("copy_selection"), 0xFFEFF6FF, this::copySelection);
        addContextAction(contextRow, "✕", t("clear_format"), 0xFFFFF1F2, this::clearFormatting);
        selectionToolbar.addView(contextScroll, new LinearLayout.LayoutParams(-1, dp(60)));
        card.addView(selectionToolbar);

        LinearLayout actions = row();
        actions.setPadding(0, dp(10), 0, 0);
        addEqualAction(actions, "↶\n" + t("undo"), 0xFFF5F3FF, v -> undoAction(), 0);
        addEqualAction(actions, "↷\n" + t("redo"), 0xFFF0FDFA, v -> redoAction(), 7);
        addEqualAction(actions, "😊\n" + t("emoji"), 0xFFEFF6FF, v -> emojis(), 7);
        addEqualAction(actions, "⌫\n" + t("clear"), 0xFFFFF1F2, v -> clearText(), 7);
        card.addView(actions);
        return card;
    }

    private View menuBar() {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setFillViewport(false);
        LinearLayout row = row();
        row.setPadding(dp(2), dp(4), dp(2), dp(4));
        scroll.addView(row);

        addMenuButton(row, "✨", t("styles"), "styles", 0xFF7C3AED);
        addMenuButton(row, "𝑻", t("fonts"), "fonts", 0xFF2563EB);
        addMenuButton(row, "⇅", t("formats"), "formats", 0xFF0F766E);
        addMenuButton(row, "🎨", t("colors"), "colors", 0xFFDB2777);
        addMenuButton(row, "☷", t("paragraph_tools"), "paragraphs", 0xFFF59E0B);
        addMenuButton(row, "😊", t("emoji"), "emoji", 0xFF0284C7);
        addMenuButton(row, "👁", t("preview"), "preview", 0xFFB45309);
        addMenuButton(row, "↗", t("share"), "share", 0xFF16A34A);
        return scroll;
    }

    private void addMenuButton(LinearLayout row, String icon, String label, String target, int border) {
        Button button = button(icon + "  " + label, 0xFFFCFCFF, 0xFF344054, 12);
        button.setBackground(round(0xFFFCFCFF, 14, border, 1));
        button.setMinWidth(dp(112));
        button.setOnClickListener(v -> {
            if ("emoji".equals(target)) {
                emojis();
                return;
            }
            View section = sectionRefs.get(target);
            if (section != null) contentScroll.post(() -> contentScroll.smoothScrollTo(0, Math.max(0, section.getTop() - dp(6))));
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(48));
        if (row.getChildCount() > 0) params.setMargins(dp(7), 0, 0, 0);
        row.addView(button, params);
    }

    private View stylesCard() {
        LinearLayout card = sectionCard("✨", t("styles"), 0xFF7C3AED);
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

        addSectionTitle(card, "✦ " + t("decor"));
        card.addView(styleRow(new Item[] {
            new Item("A̲bc", "underline", TextStyle.UNDER),
            new Item("A̶bc", "strike", TextStyle.STRIKE),
            new Item("A̅b̅c̅", "over", TextStyle.OVER),
            new Item("▰ Abc", "highlight", TextStyle.HIGHLIGHT),
            new Item("✨ Abc", "spark", TextStyle.SPARK),
            new Item("💖 Abc", "heart", TextStyle.HEART),
            new Item("【Abc】", "frame", TextStyle.FRAME)
        }));
        return card;
    }

    private View fontsCard() {
        LinearLayout card = sectionCard("𝑻", t("fonts"), 0xFF2563EB);
        TextView note = text(t("font_note"), 11, 0xFF667085);
        note.setPadding(0, 0, 0, dp(8));
        card.addView(note);

        HorizontalScrollView quickScroll = new HorizontalScrollView(this);
        quickScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout quick = row();
        quickScroll.addView(quick);
        String[] quickIds = {"system", "times", "garamond", "baskerville", "bodoni", "rockwell", "futura", "optima", "shelley"};
        for (String id : quickIds) {
            FontCatalog.Option option = FontCatalog.find(id);
            Button button = button(option.label, 0xFFF5F7FF, 0xFF1B1F3B, 13);
            button.setTypeface(option.typeface());
            button.setMinWidth(dp(132));
            button.setOnClickListener(v -> resolveInlineRange(range -> applyFont(range, option)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(58));
            if (quick.getChildCount() > 0) params.setMargins(dp(8), 0, 0, 0);
            quick.addView(button, params);
        }
        card.addView(quickScroll);

        Button allFonts = button("🔎  " + t("browse_fonts"), 0xFFEFF6FF, 0xFF1D4ED8, 14);
        allFonts.setTypeface(Typeface.DEFAULT_BOLD);
        allFonts.setOnClickListener(v -> showFontPicker());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(54));
        params.setMargins(0, dp(10), 0, 0);
        card.addView(allFonts, params);
        return card;
    }

    private View formatsCard() {
        LinearLayout card = sectionCard("⇅", t("formats"), 0xFF0F766E);
        addSectionTitle(card, t("casing"));
        card.addView(actionRow(new ActionItem[] {
            new ActionItem("Abc.", "sentence", "sentence"),
            new ActionItem("abc", "lower", "lower"),
            new ActionItem("ABC", "upper", "upper"),
            new ActionItem("Abc Def", "title", "title"),
            new ActionItem("aBC", "toggle", "toggle"),
            new ActionItem("AbCd", "sponge", "sponge")
        }));

        addSectionTitle(card, t("naming"));
        card.addView(actionRow(new ActionItem[] {
            new ActionItem("a_b_c", "snake", "snake"),
            new ActionItem("a-b-c", "kebab", "kebab"),
            new ActionItem("aBc", "camel", "camel"),
            new ActionItem("ABc", "pascal", "pascal")
        }));

        addSectionTitle(card, t("cleanup"));
        card.addView(actionRow(new ActionItem[] {
            new ActionItem("é → e", "no_accents", "no_accents"),
            new ActionItem("a b → ab", "no_spaces", "no_spaces"),
            new ActionItem("↔", "trim", "trim"),
            new ActionItem("a  b → a b", "collapse_spaces", "collapse_spaces"),
            new ActionItem(".,!?", "punctuation", "punctuation"),
            new ActionItem("¶", "clean_lines", "clean_lines"),
            new ActionItem("#", "hashtags", "hashtags")
        }));
        return card;
    }

    private View colorsCard() {
        LinearLayout card = sectionCard("🎨", t("colors"), 0xFFDB2777);
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

    private View paragraphCard() {
        LinearLayout card = sectionCard("☷", t("paragraph_tools"), 0xFFF59E0B);
        addSectionTitle(card, t("alignment"));
        card.addView(paragraphToolRow(
            new String[] {"↤", "↔", "↦", "☷"},
            new String[] {"align_left", "align_center", "align_right", "justify"},
            new String[] {"left", "center", "right", "justify"},
            true
        ));

        addSectionTitle(card, t("lists"));
        card.addView(paragraphToolRow(
            new String[] {"1.", "A.", "a.", "I.", "i.", "•", "○", "■", "▲", "–", "✓", "➜", "★", "◆", "♥", "⚡", "📌", "→", "←", "✕"},
            new String[] {"list_number", "list_letter", "list_letter_lower", "list_roman", "list_roman_lower", "list_bullet", "list_circle", "list_square", "list_triangle", "list_dash", "list_check", "list_arrow", "list_star", "list_diamond", "list_heart", "list_lightning", "list_pin", "list_indent", "list_outdent", "list_remove"},
            new String[] {"number", "letter", "letter_lower", "roman", "roman_lower", "bullet", "circle", "square", "triangle", "dash", "check", "arrow", "star", "diamond", "heart", "lightning", "pin", "indent", "outdent", "remove"},
            false
        ));
        return card;
    }

    private View previewCard() {
        LinearLayout card = sectionCard("👁", t("preview"), 0xFFB45309);
        card.setBackground(round(0xFFFFFDF7, 22, 0xFFF1D98C, 1));
        previewContainer = col();
        previewContainer.setMinimumHeight(dp(110));
        previewContainer.setPadding(dp(14), dp(12), dp(14), dp(12));
        previewContainer.setBackground(round(Color.WHITE, 16, 0xFFEEE4C6, 1));
        card.addView(previewContainer, new LinearLayout.LayoutParams(-1, -2));
        return card;
    }

    private View shareCard() {
        LinearLayout card = sectionCard("↗", t("share"), 0xFF16A34A);
        TextView note = text(t("rich_note"), 11, 0xFF667085);
        note.setPadding(0, 0, 0, dp(10));
        card.addView(note);

        LinearLayout actions = row();
        Button copy = button("⧉  " + t("copy"), 0xFF14B8A6, Color.WHITE, 14);
        copy.setTypeface(Typeface.DEFAULT_BOLD);
        copy.setOnClickListener(v -> copy());
        actions.addView(copy, new LinearLayout.LayoutParams(0, dp(54), 1));
        Button share = button("↗  " + t("share"), 0xFF7C3AED, Color.WHITE, 14);
        share.setTypeface(Typeface.DEFAULT_BOLD);
        share.setOnClickListener(v -> showShareSheet());
        LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(0, dp(54), 1);
        shareParams.setMargins(dp(9), 0, 0, 0);
        actions.addView(share, shareParams);
        card.addView(actions);

        Button image = button("▣  " + t("share_image"), 0xFFEC4899, Color.WHITE, 15);
        image.setTypeface(Typeface.DEFAULT_BOLD);
        image.setOnClickListener(v -> showImageExportDialog(null));
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
            button.setMinWidth(dp(116));
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
            button.setMinWidth(dp(124));
            button.setOnClickListener(v -> transform(item.operation));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(68));
            if (i > 0) params.setMargins(dp(8), 0, 0, 0);
            row.addView(button, params);
        }
        return scroll;
    }

    private View paragraphToolRow(String[] samples, String[] keys, String[] operations, boolean alignment) {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = row();
        scroll.addView(row);
        for (int i = 0; i < samples.length; i++) {
            final String operation = operations[i];
            Button button = button(samples[i] + "\n" + t(keys[i]), PALETTE[i % PALETTE.length], 0xFF1B1F3B, 12);
            button.setMinWidth(dp(124));
            button.setOnClickListener(v -> {
                if (alignment) applyAlignment(operation);
                else applyList(operation);
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(70));
            if (i > 0) params.setMargins(dp(8), 0, 0, 0);
            row.addView(button, params);
        }
        return scroll;
    }

    private void addSectionTitle(LinearLayout card, String label) {
        TextView section = text(label, 14, 0xFF475467);
        section.setTypeface(Typeface.DEFAULT_BOLD);
        section.setPadding(0, dp(15), 0, dp(7));
        card.addView(section);
    }

    private void addColorTool(LinearLayout row, String icon, String label, int background, Runnable action) {
        Button button = button(icon + "\n" + label, background, 0xFF1B1F3B, 12);
        button.setMinWidth(dp(136));
        button.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(68));
        if (row.getChildCount() > 0) params.setMargins(dp(8), 0, 0, 0);
        row.addView(button, params);
    }

    private void addContextAction(LinearLayout row, String icon, String label, int background, Runnable action) {
        Button button = button(icon + "\n" + label, background, 0xFF1B1F3B, 10);
        button.setMinWidth(dp(84));
        button.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(55));
        if (row.getChildCount() > 0) params.setMargins(dp(6), 0, 0, 0);
        row.addView(button, params);
    }

    private void addEqualAction(LinearLayout row, String label, int background, View.OnClickListener listener, int leftMargin) {
        Button button = button(label, background, 0xFF1B1F3B, 11);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(55), 1);
        params.setMargins(dp(leftMargin), 0, 0, 0);
        row.addView(button, params);
    }

    private void apply(TextStyle style) {
        if (!hasText()) return;
        resolveInlineRange(range -> applyToRange(style, range));
    }

    private void applyToRange(TextStyle style, int[] range) {
        if (style == TextStyle.UNDER) {
            applyPlainUnderline(range);
            return;
        }
        if (style == TextStyle.STRIKE) {
            applyStrike(range);
            return;
        }
        if (style == TextStyle.HIGHLIGHT) {
            openColorPicker(MODE_HIGHLIGHT, range);
            return;
        }
        if (style == TextStyle.NORMAL) {
            clearFormatting(range);
            return;
        }

        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        int start = clamp(range[0], 0, builder.length());
        int end = clamp(range[1], start, builder.length());
        String selected = builder.subSequence(start, end).toString();
        String styled = UnicodeStyler.apply(selected, style);
        builder.replace(start, end, styled);
        commit(previous, builder, start, start + styled.length());
    }

    private void applyPlainUnderline(int[] range) {
        TextState previous = capture();
        Editable editable = editor.getText();
        int start = clamp(range[0], 0, editable.length());
        int end = clamp(range[1], start, editable.length());
        internal = true;
        subtractSpans(editable, start, end, UnderlineSpan.class, span -> new UnderlineSpan());
        subtractSpans(editable, start, end, ColorUnderlineSpan.class, span -> new ColorUnderlineSpan(span.getColor()));
        editable.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        selectRange(start, end);
        refresh();
    }

    private void applyStrike(int[] range) {
        TextState previous = capture();
        Editable editable = editor.getText();
        int start = clamp(range[0], 0, editable.length());
        int end = clamp(range[1], start, editable.length());
        internal = true;
        subtractSpans(editable, start, end, StrikethroughSpan.class, span -> new StrikethroughSpan());
        editable.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        selectRange(start, end);
        refresh();
    }

    private void transform(String operation) {
        if (!hasText()) return;
        resolveInlineRange(range -> {
            TextState previous = capture();
            SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
            int start = clamp(range[0], 0, builder.length());
            int end = clamp(range[1], start, builder.length());
            String selected = builder.subSequence(start, end).toString();
            String result = TextTransformer.transform(selected, operation, Locale.forLanguageTag(currentCode()));
            builder.replace(start, end, result);
            commit(previous, builder, start, start + result.length());
        });
    }

    private void showColorPicker(int mode) {
        if (!hasText()) return;
        resolveInlineRange(range -> openColorPicker(mode, range));
    }

    private void openColorPicker(int mode, int[] sourceRange) {
        final int[] range = {sourceRange[0], sourceRange[1]};
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = Math.min(screenWidth - dp(20), dp(560));
        int columns = dialogWidth >= dp(430) ? 6 : 4;
        int cell = Math.max(dp(48), Math.min(dp(68), (dialogWidth - dp(44)) / columns));

        LinearLayout root = col();
        root.setPadding(dp(10), dp(4), dp(10), dp(10));
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(columns);
        grid.setUseDefaultMargins(false);
        grid.setPadding(dp(3), dp(3), dp(3), dp(3));

        List<Integer> colors = colorChoices(mode);
        for (int color : colors) {
            TextView swatch = new TextView(this);
            swatch.setGravity(Gravity.CENTER);
            swatch.setText(color == Color.WHITE || color == 0x00FFFFFF ? (color == 0x00FFFFFF ? "∅" : "✓") : "");
            swatch.setTextSize(18);
            swatch.setTextColor(0xFF667085);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(color);
            circle.setStroke(dp(color == Color.WHITE || color == 0x00FFFFFF ? 2 : 1), 0xFFD0D5DD);
            swatch.setBackground(circle);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cell - dp(8);
            params.height = cell - dp(8);
            params.setMargins(dp(4), dp(4), dp(4), dp(4));
            grid.addView(swatch, params);
            swatch.setOnClickListener(v -> {
                applyColor(range, mode, color);
                rememberColor(color);
                Dialog dialog = (Dialog) root.getTag();
                if (dialog != null) dialog.dismiss();
            });
        }

        ScrollView colorScroll = new ScrollView(this);
        colorScroll.setFillViewport(true);
        colorScroll.addView(grid, new ScrollView.LayoutParams(-1, -2));
        root.addView(colorScroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout custom = row();
        custom.setGravity(Gravity.CENTER_VERTICAL);
        custom.setPadding(0, dp(8), 0, 0);
        EditText hex = new EditText(this);
        hex.setSingleLine(true);
        hex.setHint("#7C3AED");
        hex.setTextSize(14);
        hex.setPadding(dp(10), 0, dp(10), 0);
        hex.setBackground(round(0xFFF9FAFB, 12, 0xFFD0D5DD, 1));
        custom.addView(hex, new LinearLayout.LayoutParams(0, dp(48), 1));
        Button applyHex = button(t("apply"), 0xFF7C3AED, Color.WHITE, 13);
        LinearLayout.LayoutParams applyParams = new LinearLayout.LayoutParams(dp(96), dp(48));
        applyParams.setMargins(dp(8), 0, 0, 0);
        custom.addView(applyHex, applyParams);
        root.addView(custom);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(t("color_title"))
            .setView(root)
            .setNegativeButton(t("close"), null)
            .create();
        root.setTag(dialog);
        applyHex.setOnClickListener(v -> {
            Integer color = parseColor(hex.getText().toString(), mode == MODE_HIGHLIGHT);
            if (color == null) {
                toast(t("invalid_color"));
                return;
            }
            applyColor(range, mode, color);
            rememberColor(color);
            dialog.dismiss();
        });
        dialog.setOnShowListener(ignored -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(dialogWidth, Math.min(getResources().getDisplayMetrics().heightPixels - dp(40), dp(620)));
                window.setGravity(Gravity.CENTER);
            }
        });
        dialog.show();
    }

    private List<Integer> colorChoices(int mode) {
        LinkedHashSet<Integer> values = new LinkedHashSet<>();
        values.addAll(loadRecentColors());
        int[] base = mode == MODE_HIGHLIGHT ? HIGHLIGHT_COLORS : TEXT_COLORS;
        for (int color : base) values.add(color);
        return new ArrayList<>(values);
    }

    private void applyColor(int[] range, int mode, int color) {
        TextState previous = capture();
        Editable editable = editor.getText();
        int start = clamp(range[0], 0, editable.length());
        int end = clamp(range[1], start, editable.length());
        internal = true;
        if (mode == MODE_TEXT) {
            subtractSpans(editable, start, end, ForegroundColorSpan.class, span -> new ForegroundColorSpan(span.getForegroundColor()));
            editable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (mode == MODE_HIGHLIGHT) {
            subtractSpans(editable, start, end, BackgroundColorSpan.class, span -> new BackgroundColorSpan(span.getBackgroundColor()));
            if (Color.alpha(color) != 0) editable.setSpan(new BackgroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            subtractSpans(editable, start, end, UnderlineSpan.class, span -> new UnderlineSpan());
            subtractSpans(editable, start, end, ColorUnderlineSpan.class, span -> new ColorUnderlineSpan(span.getColor()));
            editable.setSpan(new ColorUnderlineSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        selectRange(start, end);
        refresh();
    }

    private void clearColors() {
        if (!hasText()) return;
        resolveInlineRange(range -> {
            TextState previous = capture();
            Editable editable = editor.getText();
            int start = clamp(range[0], 0, editable.length());
            int end = clamp(range[1], start, editable.length());
            internal = true;
            subtractSpans(editable, start, end, ForegroundColorSpan.class, span -> new ForegroundColorSpan(span.getForegroundColor()));
            subtractSpans(editable, start, end, BackgroundColorSpan.class, span -> new BackgroundColorSpan(span.getBackgroundColor()));
            subtractSpans(editable, start, end, ColorUnderlineSpan.class, span -> new ColorUnderlineSpan(span.getColor()));
            internal = false;
            pushUndo(previous);
            redoStack.clear();
            selectRange(start, end);
            refresh();
        });
    }

    private void showFontPicker() {
        if (!hasText()) return;
        resolveInlineRange(this::openFontPicker);
    }

    private void openFontPicker(int[] sourceRange) {
        final int[] range = {sourceRange[0], sourceRange[1]};
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout root = col();
        root.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = title("𝑻  " + t("font_library"));
        header.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        Button close = button("✕", 0xFFF2F4F7, 0xFF1B1F3B, 18);
        close.setOnClickListener(v -> dialog.dismiss());
        header.addView(close, new LinearLayout.LayoutParams(dp(48), dp(42)));
        root.addView(header);

        TextView note = text(t("font_legal_note"), 11, 0xFF667085);
        note.setPadding(0, dp(6), 0, dp(8));
        root.addView(note);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint(t("search_font"));
        search.setTextSize(15);
        search.setPadding(dp(12), 0, dp(12), 0);
        search.setBackground(round(0xFFF9FAFB, 14, 0xFFD0D5DD, 1));
        root.addView(search, new LinearLayout.LayoutParams(-1, dp(50)));

        HorizontalScrollView categoryScroll = new HorizontalScrollView(this);
        categoryScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout categories = row();
        categoryScroll.addView(categories);
        LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(-1, dp(58));
        categoryParams.setMargins(0, dp(8), 0, dp(5));
        root.addView(categoryScroll, categoryParams);

        ScrollView fontScroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        int columns = getResources().getDisplayMetrics().widthPixels >= dp(700) ? 3 : 2;
        grid.setColumnCount(columns);
        grid.setPadding(dp(2), dp(2), dp(2), dp(20));
        fontScroll.addView(grid);
        root.addView(fontScroll, new LinearLayout.LayoutParams(-1, 0, 1));

        final String[] activeCategory = {"font_all"};
        final Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> renderFontGrid(grid, activeCategory[0], search.getText().toString(), range, dialog, refresh[0]);

        String[] categoryKeys = {
            "font_all", "font_recent", "font_favorites", "font_classics", "font_humanes", "font_garaldes",
            "font_reales", "font_didones", "font_mecanes", "font_lineales", "font_incises", "font_scriptes",
            "font_manuaires", "font_fractures", "font_non_latin"
        };
        for (int i = 0; i < categoryKeys.length; i++) {
            final String key = categoryKeys[i];
            Button tab = button(t(key), PALETTE[i % PALETTE.length], 0xFF1B1F3B, 11);
            tab.setMinWidth(dp(124));
            tab.setOnClickListener(v -> {
                activeCategory[0] = key;
                refresh[0].run();
                fontScroll.scrollTo(0, 0);
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(52));
            if (i > 0) params.setMargins(dp(7), 0, 0, 0);
            categories.addView(tab, params);
        }

        search.addTextChangedListener(new SimpleWatcher(refresh[0]));
        refresh[0].run();
        dialog.setContentView(root);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    private void renderFontGrid(GridLayout grid, String category, String query, int[] range, Dialog dialog, Runnable refresh) {
        grid.removeAllViews();
        List<FontCatalog.Option> options;
        if ("font_recent".equals(category)) {
            options = optionsFromIds(loadCsvList(KEY_RECENT_FONTS));
        } else if ("font_favorites".equals(category)) {
            options = optionsFromIds(new ArrayList<>(loadCsvSet(KEY_FAVORITE_FONTS)));
        } else {
            options = FontCatalog.filter(category, query);
        }
        if (!query.trim().isEmpty() && ("font_recent".equals(category) || "font_favorites".equals(category))) {
            String normalized = query.trim().toLowerCase(Locale.ROOT);
            List<FontCatalog.Option> filtered = new ArrayList<>();
            for (FontCatalog.Option option : options) if (option.label.toLowerCase(Locale.ROOT).contains(normalized)) filtered.add(option);
            options = filtered;
        }
        Set<String> favorites = loadCsvSet(KEY_FAVORITE_FONTS);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columns = grid.getColumnCount();
        int cellWidth = Math.max(dp(150), (screenWidth - dp(42)) / columns);

        if (options.isEmpty()) {
            TextView empty = text(t("no_font"), 14, 0xFF667085);
            empty.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams emptyParams = new GridLayout.LayoutParams();
            emptyParams.width = screenWidth - dp(32);
            emptyParams.height = dp(90);
            grid.addView(empty, emptyParams);
            return;
        }

        for (FontCatalog.Option option : options) {
            boolean favorite = favorites.contains(option.id);
            boolean warning = "ar".equals(currentCode()) && !option.arabicFriendly;
            Button item = button((favorite ? "★ " : "☆ ") + option.label + (warning ? "  ⚠" : ""), 0xFFFAFAFF, 0xFF1B1F3B, 14);
            item.setTypeface(option.typeface());
            item.setGravity(Gravity.CENTER);
            item.setContentDescription(option.label + (warning ? ", " + t("font_fallback_warning") : ""));
            item.setOnClickListener(v -> {
                applyFont(range, option);
                dialog.dismiss();
            });
            item.setOnLongClickListener(v -> {
                toggleCsvValue(KEY_FAVORITE_FONTS, option.id);
                toast(favorite ? t("removed_favorite") : t("added_favorite"));
                refresh.run();
                return true;
            });
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellWidth - dp(8);
            params.height = dp(70);
            params.setMargins(dp(4), dp(4), dp(4), dp(4));
            grid.addView(item, params);
        }
    }

    private void applyFont(int[] sourceRange, FontCatalog.Option option) {
        TextState previous = capture();
        Editable editable = editor.getText();
        int start = clamp(sourceRange[0], 0, editable.length());
        int end = clamp(sourceRange[1], start, editable.length());
        internal = true;
        subtractSpans(editable, start, end, SafeTypefaceSpan.class,
            span -> new SafeTypefaceSpan(span.getFontId(), span.getFamily(), span.getRequestedStyle()));
        if (!"system".equals(option.id)) {
            editable.setSpan(new SafeTypefaceSpan(option.id, option.family, option.style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        internal = false;
        pushUndo(previous);
        redoStack.clear();
        rememberRecentFont(option.id);
        if ("ar".equals(currentCode()) && !option.arabicFriendly) toast(t("font_fallback_warning"));
        selectRange(start, end);
        refresh();
    }

    private void applyAlignment(String operation) {
        if (!hasText()) return;
        resolveParagraphRange(range -> {
            TextState previous = capture();
            SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
            int start = clamp(range[0], 0, builder.length());
            int end = clamp(range[1], start, builder.length());
            subtractSpans(builder, start, end, AlignmentSpan.Standard.class,
                span -> new AlignmentSpan.Standard(span.getAlignment()));
            subtractSpans(builder, start, end, JustifySpan.class, span -> new JustifySpan());

            if ("justify".equals(operation)) {
                builder.setSpan(new JustifySpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                boolean rtl = "ar".equals(currentCode());
                Layout.Alignment alignment;
                if ("center".equals(operation)) alignment = Layout.Alignment.ALIGN_CENTER;
                else if ("right".equals(operation)) alignment = rtl ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_OPPOSITE;
                else alignment = rtl ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
                builder.setSpan(new AlignmentSpan.Standard(alignment), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            commit(previous, builder, start, end);
        });
    }

    private void applyList(String operation) {
        if (!hasText()) return;
        resolveParagraphRange(range -> {
            TextState previous = capture();
            SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
            int start = clamp(range[0], 0, builder.length());
            int end = clamp(range[1], start, builder.length());
            int newEnd = ListFormatter.apply(builder, start, end, operation);
            commit(previous, builder, start, Math.max(start, Math.min(newEnd, builder.length())));
        });
    }

    private void clearFormatting() {
        if (!hasText()) return;
        resolveInlineRange(this::clearFormatting);
    }

    private void clearFormatting(int[] sourceRange) {
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        int start = clamp(sourceRange[0], 0, builder.length());
        int end = clamp(sourceRange[1], start, builder.length());
        String normalized = UnicodeStyler.normal(builder.subSequence(start, end).toString());
        builder.replace(start, end, normalized);
        int newEnd = start + normalized.length();
        subtractSpans(builder, start, newEnd, ForegroundColorSpan.class, span -> new ForegroundColorSpan(span.getForegroundColor()));
        subtractSpans(builder, start, newEnd, BackgroundColorSpan.class, span -> new BackgroundColorSpan(span.getBackgroundColor()));
        subtractSpans(builder, start, newEnd, UnderlineSpan.class, span -> new UnderlineSpan());
        subtractSpans(builder, start, newEnd, ColorUnderlineSpan.class, span -> new ColorUnderlineSpan(span.getColor()));
        subtractSpans(builder, start, newEnd, StrikethroughSpan.class, span -> new StrikethroughSpan());
        subtractSpans(builder, start, newEnd, SafeTypefaceSpan.class,
            span -> new SafeTypefaceSpan(span.getFontId(), span.getFamily(), span.getRequestedStyle()));
        commit(previous, builder, start, newEnd);
    }

    private void resolveInlineRange(RangeAction action) {
        int[] selected = selectedRangeOrNull();
        if (selected != null) {
            action.run(selected);
            return;
        }
        int caret = currentCaret();
        String[] choices = {t("current_word"), t("current_paragraph"), t("all_text")};
        new AlertDialog.Builder(this)
            .setTitle(t("apply_to"))
            .setItems(choices, (dialog, which) -> {
                if (which == 0) action.run(wordRangeAt(caret));
                else if (which == 1) action.run(paragraphRangeAt(caret));
                else action.run(new int[] {0, editor.length()});
            })
            .setNegativeButton(t("cancel"), null)
            .show();
    }

    private void resolveParagraphRange(RangeAction action) {
        int[] selected = selectedRangeOrNull();
        if (selected != null) {
            action.run(paragraphRange(selected));
            return;
        }
        int caret = currentCaret();
        String[] choices = {t("current_paragraph"), t("all_text")};
        new AlertDialog.Builder(this)
            .setTitle(t("apply_to"))
            .setItems(choices, (dialog, which) -> {
                if (which == 0) action.run(paragraphRangeAt(caret));
                else action.run(new int[] {0, editor.length()});
            })
            .setNegativeButton(t("cancel"), null)
            .show();
    }

    private int[] selectedRangeOrNull() {
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        if (start > end) { int swap = start; start = end; end = swap; }
        if (start != end) {
            rememberSelection(start, end);
            return new int[] {start, end};
        }
        if (savedSelectionActive && savedSelectionStart >= 0 && savedSelectionEnd <= editor.length() && savedSelectionEnd > savedSelectionStart) {
            return new int[] {savedSelectionStart, savedSelectionEnd};
        }
        return null;
    }

    private int currentCaret() {
        int caret = Math.max(0, editor.getSelectionEnd());
        return clamp(caret, 0, editor.length());
    }

    private int[] wordRangeAt(int caret) {
        String value = editor.getText().toString();
        if (value.isEmpty()) return new int[] {0, 0};
        int position = clamp(caret, 0, value.length());
        if (position == value.length()) position--;
        while (position > 0 && !isWordChar(value.codePointAt(position))) position--;
        int start = position;
        int end = position;
        while (start > 0) {
            int cp = value.codePointBefore(start);
            if (!isWordChar(cp)) break;
            start -= Character.charCount(cp);
        }
        while (end < value.length()) {
            int cp = value.codePointAt(end);
            if (!isWordChar(cp)) break;
            end += Character.charCount(cp);
        }
        if (end <= start) return paragraphRangeAt(caret);
        return new int[] {start, end};
    }

    private boolean isWordChar(int cp) {
        return Character.isLetterOrDigit(cp) || cp == '_' || cp == '\'' || cp == 0x2019;
    }

    private int[] paragraphRangeAt(int caret) {
        return paragraphRange(new int[] {caret, caret});
    }

    private int[] paragraphRange(int[] source) {
        String value = editor.getText().toString();
        int start = clamp(source[0], 0, value.length());
        int end = clamp(source[1], start, value.length());
        while (start > 0 && value.charAt(start - 1) != '\n') start--;
        if (!(end > start && end <= value.length() && value.charAt(end - 1) == '\n')) {
            while (end < value.length() && value.charAt(end) != '\n') end++;
            if (end < value.length()) end++;
        }
        return new int[] {start, end};
    }

    private int[] selectionOrCaret() {
        int[] selected = selectedRangeOrNull();
        if (selected != null) return selected;
        int caret = currentCaret();
        return new int[] {caret, caret};
    }

    private void refresh() {
        if (editor == null || previewContainer == null || count == null) return;
        String raw = editor.getText().toString();
        int characters = raw.codePointCount(0, raw.length());
        int words = raw.trim().isEmpty() ? 0 : raw.trim().split("\\s+").length;
        int lines = raw.isEmpty() ? 0 : raw.split("\\R", -1).length;
        count.setText(characters + " " + t("chars") + " · " + words + " " + t("words") + " · " + lines + " " + t("lines"));
        populatePreview(previewContainer, false);
        refreshSelectionToolbar();
        refreshEditorJustification();
        saveRichDraft();
    }

    private void refreshSelectionToolbar() {
        if (selectionToolbar == null || editor == null) return;
        int[] selected = selectedRangeOrNull();
        if (selected == null) {
            selectionToolbar.setVisibility(View.GONE);
            return;
        }
        int selectedCharacters = editor.getText().subSequence(selected[0], selected[1]).toString().codePointCount(0, selected[1] - selected[0]);
        selectedCount.setText("✓ " + selectedCharacters + " " + t("selected_chars"));
        selectionToolbar.setVisibility(View.VISIBLE);
    }

    private void populatePreview(LinearLayout target, boolean image) {
        target.removeAllViews();
        Spanned text = editor.getText();
        if (text.length() == 0) {
            TextView empty = text(t("preview_empty"), image ? 24 : 19, 0xFF667085);
            empty.setPadding(0, dp(6), 0, dp(6));
            target.addView(empty);
            return;
        }

        int start = 0;
        while (start <= text.length()) {
            int newline = indexOfNewline(text, start);
            int end = newline < 0 ? text.length() : newline;
            CharSequence paragraphText = text.subSequence(start, end);
            TextView paragraph = text("", image ? 24 : 19, 0xFF1B1F3B);
            paragraph.setText(new SpannableString(paragraphText), TextView.BufferType.SPANNABLE);
            paragraph.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
            paragraph.setLineSpacing(0f, 1.16f);
            paragraph.setPadding(0, image ? dp(5) : dp(3), 0, image ? dp(5) : dp(3));

            int probeEnd = Math.max(start, Math.min(text.length(), Math.max(start + 1, end)));
            AlignmentSpan.Standard[] alignments = text.getSpans(start, probeEnd, AlignmentSpan.Standard.class);
            Layout.Alignment alignment = alignments.length == 0 ? Layout.Alignment.ALIGN_NORMAL : alignments[alignments.length - 1].getAlignment();
            if (alignment == Layout.Alignment.ALIGN_CENTER) paragraph.setGravity(Gravity.CENTER_HORIZONTAL);
            else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) paragraph.setGravity(Gravity.END);
            else paragraph.setGravity(Gravity.START);

            JustifySpan[] justified = text.getSpans(start, probeEnd, JustifySpan.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                paragraph.setJustificationMode(justified.length > 0 ? Layout.JUSTIFICATION_MODE_INTER_WORD : Layout.JUSTIFICATION_MODE_NONE);
            }
            target.addView(paragraph, new LinearLayout.LayoutParams(-1, -2));
            if (newline < 0) break;
            start = newline + 1;
            if (start == text.length()) {
                TextView blank = text(" ", image ? 24 : 19, 0xFF1B1F3B);
                target.addView(blank);
                break;
            }
        }
    }

    private int indexOfNewline(CharSequence value, int from) {
        for (int i = from; i < value.length(); i++) if (value.charAt(i) == '\n') return i;
        return -1;
    }

    private void refreshEditorJustification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || editor.length() == 0) return;
        JustifySpan[] spans = editor.getText().getSpans(0, editor.length(), JustifySpan.class);
        boolean all = false;
        for (JustifySpan span : spans) {
            if (editor.getText().getSpanStart(span) <= 0 && editor.getText().getSpanEnd(span) >= editor.length()) {
                all = true;
                break;
            }
        }
        editor.setJustificationMode(all ? Layout.JUSTIFICATION_MODE_INTER_WORD : Layout.JUSTIFICATION_MODE_NONE);
    }

    private void clearText() {
        if (editor.length() == 0) return;
        new AlertDialog.Builder(this)
            .setTitle(t("clear"))
            .setMessage(t("clear_confirm"))
            .setPositiveButton(t("clear"), (dialog, which) -> {
                TextState previous = capture();
                setRichText("", 0, 0);
                pushUndo(previous);
                redoStack.clear();
                RichTextStore.clear(this);
            })
            .setNegativeButton(t("cancel"), null)
            .show();
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

    private void hideKeyboardPreservingSelection() {
        rememberCurrentSelection();
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (keyboard != null) keyboard.hideSoftInputFromWindow(editor.getWindowToken(), 0);
        editor.postDelayed(() -> {
            if (savedSelectionActive && savedSelectionEnd <= editor.length()) {
                internal = true;
                editor.requestFocus();
                editor.setSelection(savedSelectionStart, savedSelectionEnd);
                internal = false;
            }
        }, 120);
        toast(t("selection_saved"));
    }

    private void rememberCurrentSelection() {
        if (editor == null) return;
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        if (start != end) rememberSelection(Math.min(start, end), Math.max(start, end));
    }

    private void rememberSelection(int start, int end) {
        if (editor == null || start < 0 || end <= start || end > editor.length()) return;
        savedSelectionStart = start;
        savedSelectionEnd = end;
        savedSelectionActive = true;
    }

    private void clearRememberedSelection() {
        savedSelectionStart = -1;
        savedSelectionEnd = -1;
        savedSelectionActive = false;
    }

    private void selectRange(int start, int end) {
        int safeStart = clamp(start, 0, editor.length());
        int safeEnd = clamp(end, safeStart, editor.length());
        internal = true;
        editor.requestFocus();
        editor.setSelection(safeStart, safeEnd);
        internal = false;
        if (safeStart != safeEnd) rememberSelection(safeStart, safeEnd);
        else clearRememberedSelection();
        refreshSelectionToolbar();
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
        if (start == end && savedSelectionActive && savedSelectionEnd <= editor.length()) {
            start = savedSelectionStart;
            end = savedSelectionEnd;
        }
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
        int start = clamp(selectionStart, 0, length);
        int end = clamp(selectionEnd, 0, length);
        editor.setSelection(Math.min(start, end), Math.max(start, end));
        internal = false;
        if (start != end) rememberSelection(Math.min(start, end), Math.max(start, end));
        else clearRememberedSelection();
        refresh();
    }

    private boolean hasText() {
        if (editor.getText().toString().trim().isEmpty()) {
            toast(t("empty"));
            return false;
        }
        return true;
    }

    private void copy() {
        if (!hasText()) return;
        copyToClipboard(exportPlainText());
        toast(t("copied"));
    }

    private void copySelection() {
        int[] range = selectedRangeOrNull();
        if (range == null) {
            toast(t("select_text_first"));
            return;
        }
        copyToClipboard(exportPlainText(range[0], range[1]));
        toast(t("copied"));
    }

    private void copyToClipboard(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("TextAura", value));
    }

    private String exportPlainText() {
        return exportPlainText(0, editor.length());
    }

    private String exportPlainText(int from, int to) {
        Spanned text = editor.getText();
        int start = clamp(from, 0, text.length());
        int endLimit = clamp(to, start, text.length());
        StringBuilder out = new StringBuilder();
        for (int index = start; index < endLimit;) {
            int cp = Character.codePointAt(text, index);
            int charCount = Character.charCount(cp);
            int end = Math.min(endLimit, index + charCount);
            boolean underlined = text.getSpans(index, end, UnderlineSpan.class).length > 0
                || text.getSpans(index, end, ColorUnderlineSpan.class).length > 0;
            boolean struck = text.getSpans(index, end, StrikethroughSpan.class).length > 0;
            out.appendCodePoint(cp);
            int type = Character.getType(cp);
            boolean combining = type == Character.NON_SPACING_MARK
                || type == Character.COMBINING_SPACING_MARK
                || type == Character.ENCLOSING_MARK;
            if (cp != '\n' && cp != '\r' && !combining) {
                if (underlined) out.appendCodePoint(0x332);
                if (struck) out.appendCodePoint(0x336);
            }
            index = end;
        }
        return out.toString();
    }

    private void showShareSheet() {
        if (!hasText()) return;
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout root = col();
        root.setPadding(dp(14), dp(14), dp(14), dp(14));
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(title("↗  " + t("share_with")), new LinearLayout.LayoutParams(0, -2, 1));
        Button close = button("✕", 0xFFF2F4F7, 0xFF1B1F3B, 18);
        close.setOnClickListener(v -> dialog.dismiss());
        header.addView(close, new LinearLayout.LayoutParams(dp(48), dp(42)));
        root.addView(header);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        grid.setPadding(0, dp(10), 0, dp(10));
        addShareOption(grid, "🟢", "WhatsApp", 0xFFE8FFF4, () -> shareText("com.whatsapp"));
        addShareOption(grid, "in", "LinkedIn", 0xFFEAF4FF, () -> shareText("com.linkedin.android"));
        addShareOption(grid, "f", "Facebook", 0xFFEDF2FF, () -> shareText("com.facebook.katana"));
        addShareOption(grid, "𝕏", "X", 0xFFF1F3F5, () -> shareText("com.twitter.android"));
        addShareOption(grid, "✈", "Telegram", 0xFFEAF8FF, () -> shareText("org.telegram.messenger"));
        addShareOption(grid, "◎", "Instagram", 0xFFFFEFF7, () -> shareText("com.instagram.android"));
        addShareOption(grid, "@", t("email"), 0xFFFFFBEB, this::sendEmail);
        addShareOption(grid, "💬", "SMS", 0xFFF0FDFA, this::sendSms);
        addShareOption(grid, "⧉", t("copy"), 0xFFF5F3FF, this::copy);
        addShareOption(grid, "▣", "PNG", 0xFFFDF2F8, () -> showImageExportDialog(null));
        addShareOption(grid, "↗", t("other_apps"), 0xFFEFF6FF, () -> shareText(null));
        root.addView(grid);
        dialog.setContentView(root);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            window.setLayout(Math.min(getResources().getDisplayMetrics().widthPixels - dp(20), dp(560)), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
    }

    private void addShareOption(GridLayout grid, String icon, String label, int background, Runnable action) {
        Button button = button(icon + "\n" + label, background, 0xFF1B1F3B, 12);
        button.setOnClickListener(v -> action.run());
        int width = Math.max(dp(96), (getResources().getDisplayMetrics().widthPixels - dp(56)) / 3);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = width;
        params.height = dp(72);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        grid.addView(button, params);
    }

    private void shareText(String packageName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, exportPlainText());
        if (packageName != null) intent.setPackage(packageName);
        if (packageName != null && intent.resolveActivity(getPackageManager()) == null) {
            toast(t("app_missing"));
            shareText(null);
            return;
        }
        try {
            startActivity(packageName == null ? Intent.createChooser(intent, t("share_with")) : intent);
        } catch (Exception exception) {
            toast(t("share_error"));
        }
    }

    private void sendEmail() {
        Uri uri = Uri.parse("mailto:?subject=" + Uri.encode("TextAura") + "&body=" + Uri.encode(exportPlainText()));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        try {
            startActivity(intent);
        } catch (Exception exception) {
            toast(t("app_missing"));
        }
    }

    private void sendSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", exportPlainText());
        try {
            startActivity(intent);
        } catch (Exception exception) {
            toast(t("app_missing"));
        }
    }

    private void showImageExportDialog(String packageName) {
        if (!hasText()) return;
        String[] labels = {t("image_auto"), "1:1", "4:5", "9:16", "16:9"};
        String[] formats = {"auto", "1:1", "4:5", "9:16", "16:9"};
        new AlertDialog.Builder(this)
            .setTitle(t("image_format"))
            .setItems(labels, (dialog, which) -> shareImage(packageName, formats[which]))
            .setNegativeButton(t("cancel"), null)
            .show();
    }

    private void shareImage(String packageName, String format) {
        try {
            Uri uri = createShareImage(format);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, exportPlainText());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setClipData(ClipData.newUri(getContentResolver(), "TextAura", uri));
            if (packageName != null) intent.setPackage(packageName);
            if (packageName != null && intent.resolveActivity(getPackageManager()) == null) {
                toast(t("app_missing"));
                shareImage(null, format);
                return;
            }
            startActivity(packageName == null ? Intent.createChooser(intent, t("share_with")) : intent);
        } catch (Exception exception) {
            toast(t("image_error"));
        }
    }

    private Uri createShareImage(String format) throws Exception {
        LinearLayout imageCard = col();
        imageCard.setPadding(dp(28), dp(24), dp(28), dp(28));
        imageCard.setBackground(round(Color.WHITE, 0, 0, 0));

        TextView brand = text("TextAura  ✦", 20, 0xFF7C3AED);
        brand.setTypeface(Typeface.DEFAULT_BOLD);
        imageCard.addView(brand);
        TextView subtitle = text(t("tag"), 11, 0xFF667085);
        subtitle.setPadding(0, dp(2), 0, dp(14));
        imageCard.addView(subtitle);
        LinearLayout content = col();
        populatePreview(content, true);
        imageCard.addView(content, new LinearLayout.LayoutParams(-1, -2));

        int width = 1080;
        imageCard.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int measuredHeight = Math.max(dp(260), imageCard.getMeasuredHeight());
        int ratioHeight = ratioHeight(width, format);
        int height = "auto".equals(format) ? measuredHeight : Math.max(measuredHeight, ratioHeight);
        imageCard.layout(0, 0, width, measuredHeight);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xFFFDFBFF);
        float top = Math.max(0, (height - measuredHeight) / 2f);
        canvas.save();
        canvas.translate(0, top);
        imageCard.draw(canvas);
        canvas.restore();

        File directory = new File(getCacheDir(), "shared");
        if (!directory.exists() && !directory.mkdirs()) throw new IllegalStateException("Cache error");
        File file = new File(directory, "TextAura-publication.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
        bitmap.recycle();
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
    }

    private int ratioHeight(int width, String format) {
        switch (format) {
            case "1:1": return width;
            case "4:5": return width * 5 / 4;
            case "9:16": return width * 16 / 9;
            case "16:9": return width * 9 / 16;
            default: return 0;
        }
    }

    private void restore(Intent intent) {
        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            CharSequence incoming = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
            if (incoming != null) {
                String value = incoming.toString();
                setRichText(value, value.length(), value.length());
                return;
            }
        }
        RichTextStore.SavedState saved = RichTextStore.load(this);
        setRichText(saved.text, saved.selectionStart, saved.selectionEnd);
    }

    private void saveRichDraft() {
        if (editor == null) return;
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        if (start == end && savedSelectionActive) {
            start = savedSelectionStart;
            end = savedSelectionEnd;
        }
        RichTextStore.save(this, editor.getText(), start, end);
    }

    private void emojis() {
        rememberCurrentSelection();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout root = col();
        root.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = title("😊  " + t("emoji_title"));
        header.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        Button close = button("✕", 0xFFF2F4F7, 0xFF1B1F3B, 18);
        close.setOnClickListener(v -> dialog.dismiss());
        header.addView(close, new LinearLayout.LayoutParams(dp(48), dp(42)));
        root.addView(header);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint(t("search_emoji"));
        search.setTextSize(15);
        search.setPadding(dp(12), 0, dp(12), 0);
        search.setBackground(round(0xFFF9FAFB, 14, 0xFFD0D5DD, 1));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(50));
        searchParams.setMargins(0, dp(8), 0, dp(6));
        root.addView(search, searchParams);

        TextView quickTitle = text(t("recent_favorites"), 13, 0xFF475467);
        quickTitle.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(quickTitle);
        HorizontalScrollView quickScroll = new HorizontalScrollView(this);
        quickScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout quickRow = row();
        quickScroll.addView(quickRow);
        root.addView(quickScroll, new LinearLayout.LayoutParams(-1, dp(58)));

        HorizontalScrollView categories = new HorizontalScrollView(this);
        categories.setHorizontalScrollBarEnabled(false);
        LinearLayout categoryRow = row();
        categories.addView(categoryRow);
        LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(-1, dp(58));
        categoryParams.setMargins(0, dp(6), 0, dp(3));
        root.addView(categories, categoryParams);

        TextView categoryTitle = text(t(EmojiData.KEYS[0]), 14, 0xFF475467);
        categoryTitle.setTypeface(Typeface.DEFAULT_BOLD);
        categoryTitle.setPadding(dp(3), dp(3), 0, dp(5));
        root.addView(categoryTitle);

        ScrollView emojiScroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setPadding(dp(2), dp(2), dp(2), dp(18));
        emojiScroll.addView(grid);
        root.addView(emojiScroll, new LinearLayout.LayoutParams(-1, 0, 1));

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columns = screenWidth >= dp(700) ? 10 : 6;
        int cell = Math.max(dp(48), (screenWidth - dp(44)) / columns);
        final int[] activeCategory = {0};
        final Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            fillEmojiQuickRow(quickRow, dialog, refresh[0]);
            fillEmojiGrid(grid, activeCategory[0], search.getText().toString(), columns, cell, refresh[0]);
        };

        for (int i = 0; i < EmojiData.KEYS.length; i++) {
            final int index = i;
            Button tab = button(EmojiData.ICONS[i] + "\n" + t(EmojiData.KEYS[i]), PALETTE[i % PALETTE.length], 0xFF1B1F3B, 11);
            tab.setMinWidth(dp(120));
            tab.setOnClickListener(v -> {
                activeCategory[0] = index;
                categoryTitle.setText(t(EmojiData.KEYS[index]));
                refresh[0].run();
                emojiScroll.scrollTo(0, 0);
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(54));
            if (i > 0) params.setMargins(dp(7), 0, 0, 0);
            categoryRow.addView(tab, params);
        }

        search.addTextChangedListener(new SimpleWatcher(refresh[0]));
        refresh[0].run();
        dialog.setContentView(root);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    private void fillEmojiQuickRow(LinearLayout row, Dialog dialog, Runnable refresh) {
        row.removeAllViews();
        LinkedHashSet<String> values = new LinkedHashSet<>();
        values.addAll(loadEmojiList(KEY_FAVORITE_EMOJIS));
        values.addAll(loadEmojiList(KEY_RECENT_EMOJIS));
        int shown = 0;
        for (String emoji : values) {
            if (emoji.isEmpty()) continue;
            TextView item = text(emoji, 25, Color.BLACK);
            item.setGravity(Gravity.CENTER);
            styleEmojiCell(item, editor.getText().toString().contains(emoji), loadEmojiSet(KEY_FAVORITE_EMOJIS).contains(emoji));
            item.setOnClickListener(v -> {
                insertEmoji(emoji);
                rememberRecentEmoji(emoji);
                refresh.run();
            });
            item.setOnLongClickListener(v -> {
                toggleEmojiFavorite(emoji);
                refresh.run();
                return true;
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(50), dp(50));
            if (shown > 0) params.setMargins(dp(5), 0, 0, 0);
            row.addView(item, params);
            if (++shown >= 18) break;
        }
        if (shown == 0) {
            TextView empty = text(t("no_recent_emoji"), 12, 0xFF98A2B3);
            row.addView(empty, new LinearLayout.LayoutParams(-2, dp(50)));
        }
    }

    private void fillEmojiGrid(GridLayout grid, int category, String query, int columns, int cell, Runnable refresh) {
        grid.removeAllViews();
        grid.setColumnCount(columns);
        String currentText = editor.getText().toString();
        Set<String> favorites = loadEmojiSet(KEY_FAVORITE_EMOJIS);
        String normalized = query == null ? "" : query.trim();
        for (String emoji : EmojiData.EMOJIS[category]) {
            if (!normalized.isEmpty() && !emoji.contains(normalized) && !t(EmojiData.KEYS[category]).toLowerCase(Locale.ROOT).contains(normalized.toLowerCase(Locale.ROOT))) continue;
            boolean alreadyUsed = currentText.contains(emoji);
            boolean favorite = favorites.contains(emoji);
            TextView item = text(emoji, 26, Color.BLACK);
            item.setGravity(Gravity.CENTER);
            styleEmojiCell(item, alreadyUsed, favorite);
            item.setContentDescription(emoji + (alreadyUsed ? ", " + t("emoji_added") : "") + (favorite ? ", " + t("favorite") : ""));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cell - dp(6);
            params.height = cell - dp(6);
            params.setMargins(dp(3), dp(3), dp(3), dp(3));
            grid.addView(item, params);
            item.setOnClickListener(v -> {
                insertEmoji(emoji);
                rememberRecentEmoji(emoji);
                refresh.run();
            });
            item.setOnLongClickListener(v -> {
                toggleEmojiFavorite(emoji);
                refresh.run();
                return true;
            });
        }
    }

    private void styleEmojiCell(TextView item, boolean used, boolean favorite) {
        int background = used ? 0xFFD7F7EF : favorite ? 0xFFFFF4CC : 0xFFF8F8FD;
        int border = used ? 0xFF0F9D8B : favorite ? 0xFFF59E0B : 0xFFE4E4EE;
        item.setBackground(round(background, 13, border, used || favorite ? 2 : 1));
        item.setElevation(used || favorite ? dp(2) : 0);
    }

    private void insertEmoji(String value) {
        int[] range = selectionOrCaret();
        int start = range[0];
        int end = range[1];
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        builder.replace(start, end, value);
        commit(previous, builder, start + value.length(), start + value.length());
        clearRememberedSelection();
    }

    private void languages() {
        String[] names = {
            "🇫🇷  Français", "🇬🇧  English", "🇪🇸  Español", "🇩🇪  Deutsch", "🇸🇦  العربية"
        };
        String[] codes = {"fr", "en", "es", "de", "ar"};
        new AlertDialog.Builder(this)
            .setTitle(t("language"))
            .setSingleChoiceItems(names, langIndex, (dialog, which) -> {
                saveRichDraft();
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_LANG, codes[which]).apply();
                dialog.dismiss();
                recreate();
            })
            .show();
    }

    private String languageLabel(String code) {
        switch (code) {
            case "en": return "🇬🇧 EN";
            case "es": return "🇪🇸 ES";
            case "de": return "🇩🇪 DE";
            case "ar": return "🇸🇦 AR";
            default: return "🇫🇷 FR";
        }
    }

    private <T> void subtractSpans(Spannable text, int start, int end, Class<T> type, SpanCopier<T> copier) {
        T[] spans = text.getSpans(start, end, type);
        for (T span : spans) {
            int spanStart = text.getSpanStart(span);
            int spanEnd = text.getSpanEnd(span);
            int flags = text.getSpanFlags(span);
            text.removeSpan(span);
            if (spanStart < start) text.setSpan(copier.copy(span), spanStart, start, flags);
            if (spanEnd > end) text.setSpan(copier.copy(span), end, spanEnd, flags);
        }
    }

    private Integer parseColor(String value, boolean translucent) {
        try {
            String normalized = value == null ? "" : value.trim();
            if (!normalized.startsWith("#")) normalized = "#" + normalized;
            int color = Color.parseColor(normalized);
            if (translucent && Color.alpha(color) == 255) color = (0x66 << 24) | (color & 0x00FFFFFF);
            return color;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void rememberColor(int color) {
        List<String> values = loadCsvList(KEY_RECENT_COLORS);
        String encoded = Integer.toString(color);
        values.remove(encoded);
        values.add(0, encoded);
        while (values.size() > 8) values.remove(values.size() - 1);
        saveCsvList(KEY_RECENT_COLORS, values);
    }

    private List<Integer> loadRecentColors() {
        List<Integer> result = new ArrayList<>();
        for (String value : loadCsvList(KEY_RECENT_COLORS)) {
            try { result.add(Integer.parseInt(value)); } catch (Exception ignored) {}
        }
        return result;
    }

    private List<FontCatalog.Option> optionsFromIds(List<String> ids) {
        List<FontCatalog.Option> result = new ArrayList<>();
        for (String id : ids) {
            FontCatalog.Option option = FontCatalog.find(id);
            if (!"system".equals(option.id) || "system".equals(id)) result.add(option);
        }
        return result;
    }

    private void rememberRecentFont(String id) {
        List<String> values = loadCsvList(KEY_RECENT_FONTS);
        values.remove(id);
        values.add(0, id);
        while (values.size() > 10) values.remove(values.size() - 1);
        saveCsvList(KEY_RECENT_FONTS, values);
    }

    private List<String> loadCsvList(String key) {
        String value = getSharedPreferences(PREFS, MODE_PRIVATE).getString(key, "");
        if (value == null || value.isEmpty()) return new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String item : value.split(",")) if (!item.trim().isEmpty()) result.add(item.trim());
        return result;
    }

    private Set<String> loadCsvSet(String key) {
        return new LinkedHashSet<>(loadCsvList(key));
    }

    private void saveCsvList(String key, List<String> values) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(key, String.join(",", values)).apply();
    }

    private void toggleCsvValue(String key, String value) {
        List<String> values = loadCsvList(key);
        if (values.contains(value)) values.remove(value); else values.add(0, value);
        saveCsvList(key, values);
    }

    private List<String> loadEmojiList(String key) {
        String value = getSharedPreferences(PREFS, MODE_PRIVATE).getString(key, "");
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split("\\|", -1)));
    }

    private Set<String> loadEmojiSet(String key) {
        return new LinkedHashSet<>(loadEmojiList(key));
    }

    private void saveEmojiList(String key, List<String> values) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(key, String.join("|", values)).apply();
    }

    private void rememberRecentEmoji(String emoji) {
        List<String> values = loadEmojiList(KEY_RECENT_EMOJIS);
        values.remove(emoji);
        values.add(0, emoji);
        while (values.size() > 30) values.remove(values.size() - 1);
        saveEmojiList(KEY_RECENT_EMOJIS, values);
    }

    private void toggleEmojiFavorite(String emoji) {
        List<String> values = loadEmojiList(KEY_FAVORITE_EMOJIS);
        if (values.contains(emoji)) {
            values.remove(emoji);
            toast(t("removed_favorite"));
        } else {
            values.add(0, emoji);
            toast(t("added_favorite"));
        }
        saveEmojiList(KEY_FAVORITE_EMOJIS, values);
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

    private String t(String key) {
        return Texts.get(key, langIndex);
    }

    private void toast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

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

    private LinearLayout sectionCard(String icon, String heading, int strokeColor) {
        LinearLayout card = col();
        card.setPadding(dp(15), dp(14), dp(15), dp(15));
        card.setBackground(round(Color.WHITE, 22, strokeColor, 1));
        card.setElevation(dp(3));
        LinearLayout titleRow = row();
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView iconView = text(icon, 19, strokeColor);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(round((strokeColor & 0x00FFFFFF) | 0x19000000, 12, strokeColor, 1));
        titleRow.addView(iconView, new LinearLayout.LayoutParams(dp(42), dp(38)));
        TextView title = title(heading);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, -2, 1);
        titleParams.setMargins(dp(9), 0, 0, 0);
        titleRow.addView(title, titleParams);
        card.addView(titleRow);
        gap(card, 10);
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
        button.setPadding(dp(9), 0, dp(9), 0);
        button.setMinWidth(0);
        button.setMinHeight(dp(44));
        button.setFocusable(false);
        button.setFocusableInTouchMode(false);
        button.setBackground(round(background, 15, 0xFFE2E2EC, 1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) button.setStateListAnimator(null);
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

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
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

    private final class SimpleWatcher implements TextWatcher {
        private final Runnable action;

        SimpleWatcher(Runnable action) {
            this.action = action;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { action.run(); }
    }
}

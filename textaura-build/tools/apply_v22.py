from pathlib import Path
import re

path = Path("app/src/main/java/com/textaura/social/MainActivity.java")
text = path.read_text(encoding="utf-8")


def replace_once(old: str, new: str, label: str) -> None:
    global text
    if old not in text:
        raise SystemExit(f"TextAura v2.2 patch failed: {label} not found")
    text = text.replace(old, new, 1)


replace_once(
    "import android.os.Bundle;\n",
    "import android.os.Build;\nimport android.os.Bundle;\nimport android.text.Layout;\n",
    "Android layout imports",
)
replace_once(
    "import android.text.style.BackgroundColorSpan;\n",
    "import android.text.style.AlignmentSpan;\nimport android.text.style.BackgroundColorSpan;\n",
    "alignment span import",
)

replace_once(
    '''        body.addView(colorsCard());
        gap(body, 12);
        body.addView(previewCard());''',
    '''        body.addView(colorsCard());
        gap(body, 12);
        body.addView(paragraphCard());
        gap(body, 12);
        body.addView(previewCard());''',
    "paragraph card insertion",
)

paragraph_card = r'''
    private View paragraphCard() {
        LinearLayout card = card();
        card.addView(title(t("paragraph_tools")));

        addSection(card, t("alignment"));
        card.addView(paragraphToolRow(
            new String[] {"↤", "↔", "↦", "☷"},
            new String[] {"align_left", "align_center", "align_right", "justify"},
            new String[] {"left", "center", "right", "justify"},
            true
        ));

        addSection(card, t("lists"));
        card.addView(paragraphToolRow(
            new String[] {"1.", "A.", "I.", "•", "–", "✓", "➜", "★", "◆", "✕"},
            new String[] {"list_number", "list_letter", "list_roman", "list_bullet", "list_dash", "list_check", "list_arrow", "list_star", "list_diamond", "list_remove"},
            new String[] {"number", "letter", "roman", "bullet", "dash", "check", "arrow", "star", "diamond", "remove"},
            false
        ));
        return card;
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

'''
replace_once(
    "    private View previewCard() {\n",
    paragraph_card + "    private View previewCard() {\n",
    "paragraph card methods",
)

format_methods = r'''
    private void applyAlignment(String operation) {
        if (!hasText()) return;
        int[] original = selectionOrAll();
        int[] range = paragraphRange(original);
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());

        removeSpans(builder, 0, builder.length(), JustifySpan.class);
        if ("justify".equals(operation)) {
            removeSpans(builder, 0, builder.length(), AlignmentSpan.class);
            if (builder.length() > 0) {
                builder.setSpan(new JustifySpan(), 0, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        } else {
            removeSpans(builder, range[0], range[1], AlignmentSpan.class);
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            if ("center".equals(operation)) alignment = Layout.Alignment.ALIGN_CENTER;
            else if ("right".equals(operation)) alignment = Layout.Alignment.ALIGN_OPPOSITE;
            if (!"left".equals(operation)) {
                builder.setSpan(new AlignmentSpan.Standard(alignment), range[0], range[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        commit(previous, builder, original[0], original[1]);
    }

    private void applyList(String operation) {
        if (!hasText()) return;
        int[] original = selectionOrAll();
        int[] range = paragraphRange(original);
        TextState previous = capture();
        SpannableStringBuilder builder = new SpannableStringBuilder(editor.getText());
        int newEnd = ListFormatter.apply(builder, range[0], range[1], operation);
        commit(previous, builder, range[0], Math.max(range[0], Math.min(newEnd, builder.length())));
    }

    private int[] paragraphRange(int[] source) {
        String value = editor.getText().toString();
        int start = Math.max(0, Math.min(source[0], value.length()));
        int end = Math.max(start, Math.min(source[1], value.length()));
        while (start > 0 && value.charAt(start - 1) != '\n') start--;
        if (!(end > start && end <= value.length() && value.charAt(end - 1) == '\n')) {
            while (end < value.length() && value.charAt(end) != '\n') end++;
            if (end < value.length()) end++;
        }
        return new int[] {start, end};
    }

    private boolean hasJustificationMarker() {
        if (editor == null || editor.length() == 0) return false;
        return editor.getText().getSpans(0, editor.length(), JustifySpan.class).length > 0;
    }

    private void applyJustificationMode(TextView view) {
        if (view == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        view.setJustificationMode(
            hasJustificationMarker()
                ? Layout.JUSTIFICATION_MODE_INTER_WORD
                : Layout.JUSTIFICATION_MODE_NONE
        );
    }

'''
replace_once(
    "    private void showColorPicker(int mode) {\n",
    format_methods + "    private void showColorPicker(int mode) {\n",
    "alignment and list methods",
)

pattern = re.compile(
    r"    private void showColorPicker\(int mode\) \{.*?\n    \}\n\n    private void applyColor",
    re.S,
)
new_color_picker = r'''    private void showColorPicker(int mode) {
        if (!hasText()) return;
        rememberCurrentSelection();

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = Math.min(screenWidth - dp(24), dp(520));
        int columns = dialogWidth >= dp(430) ? 6 : 4;
        int cell = Math.max(dp(48), Math.min(dp(64), (dialogWidth - dp(52)) / columns));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(columns);
        grid.setUseDefaultMargins(false);
        grid.setPadding(dp(8), dp(8), dp(8), dp(8));

        ScrollView colorScroll = new ScrollView(this);
        colorScroll.setFillViewport(true);
        colorScroll.addView(grid, new ScrollView.LayoutParams(-1, -2));

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(t("color_title"))
            .setView(colorScroll)
            .setNegativeButton(t("close"), null)
            .create();

        for (int baseColor : COLORS) {
            int applied = mode == MODE_HIGHLIGHT ? translucent(baseColor) : baseColor;
            TextView swatch = new TextView(this);
            swatch.setGravity(Gravity.CENTER);
            swatch.setText(baseColor == Color.WHITE ? "✓" : "");
            swatch.setTextSize(18);
            swatch.setTextColor(0xFF667085);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(applied);
            circle.setStroke(dp(baseColor == Color.WHITE ? 2 : 1), 0xFFD0D5DD);
            swatch.setBackground(circle);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cell - dp(10);
            params.height = cell - dp(10);
            params.setMargins(dp(5), dp(5), dp(5), dp(5));
            grid.addView(swatch, params);
            swatch.setOnClickListener(v -> {
                applyColor(mode, applied);
                dialog.dismiss();
            });
        }

        dialog.setOnShowListener(ignored -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
            }
        });
        dialog.show();
    }

    private void applyColor'''
text, count = pattern.subn(new_color_picker, text, count=1)
if count != 1:
    raise SystemExit("TextAura v2.2 patch failed: color picker method not replaced")

replace_once(
    '''        if (raw.isEmpty()) {
            preview.setText(t("preview_empty"));
            preview.setTextColor(0xFF667085);
        } else {
            preview.setText(new SpannableString(editor.getText()), TextView.BufferType.SPANNABLE);
            preview.setTextColor(0xFF1B1F3B);
        }
    }''',
    '''        if (raw.isEmpty()) {
            preview.setText(t("preview_empty"));
            preview.setTextColor(0xFF667085);
        } else {
            preview.setText(new SpannableString(editor.getText()), TextView.BufferType.SPANNABLE);
            preview.setTextColor(0xFF1B1F3B);
        }
        applyJustificationMode(editor);
        applyJustificationMode(preview);
    }''',
    "justification refresh",
)

replace_once(
    '''        content.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        content.setGravity(Gravity.START);
        content.setPadding(0, dp(18), 0, 0);''',
    '''        content.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        content.setGravity(Gravity.START);
        applyJustificationMode(content);
        content.setPadding(0, dp(18), 0, 0);''',
    "image justification",
)

path.write_text(text, encoding="utf-8")
print("TextAura v2.2 source patch applied successfully")

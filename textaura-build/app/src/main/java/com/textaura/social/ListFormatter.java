package com.textaura.social;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ListFormatter {
    private static final Pattern PREFIX = Pattern.compile(
        "^(\\s*)(?:(?:\\d+|[A-Za-z]|[IVXLCDMivxlcdm]+)[.)]|[•◦▪▫●○◆◇★☆✓✔➜→–—-])\\s+"
    );

    private ListFormatter() {}

    static int apply(SpannableStringBuilder builder, int start, int end, String mode) {
        if (builder == null || builder.length() == 0) return end;
        int safeStart = Math.max(0, Math.min(start, builder.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, builder.length()));

        List<Integer> lineStarts = new ArrayList<>();
        lineStarts.add(safeStart);
        for (int i = safeStart; i < safeEnd; i++) {
            if (builder.charAt(i) == '\n' && i + 1 < safeEnd) lineStarts.add(i + 1);
        }

        int delta = 0;
        for (int index = lineStarts.size() - 1; index >= 0; index--) {
            int lineStart = lineStarts.get(index);
            int lineEnd = findLineEnd(builder, lineStart, safeEnd + delta);
            String line = builder.subSequence(lineStart, lineEnd).toString();
            if (line.trim().isEmpty()) continue;

            Matcher matcher = PREFIX.matcher(line);
            int prefixEnd = matcher.find() ? matcher.end() : 0;
            String indentation = matcher.find(0) ? matcher.group(1) : leadingWhitespace(line);
            if (prefixEnd > 0) {
                builder.delete(lineStart, lineStart + prefixEnd);
                delta -= prefixEnd;
            }

            if (!"remove".equals(mode)) {
                String prefix = indentation + prefixFor(mode, index + 1);
                builder.insert(lineStart, prefix);
                delta += prefix.length();
            }
        }
        return safeEnd + delta;
    }

    private static int findLineEnd(SpannableStringBuilder builder, int start, int limit) {
        int end = Math.min(limit, builder.length());
        for (int i = start; i < end; i++) if (builder.charAt(i) == '\n') return i;
        return end;
    }

    private static String leadingWhitespace(String line) {
        int index = 0;
        while (index < line.length() && Character.isWhitespace(line.charAt(index)) && line.charAt(index) != '\n') index++;
        return line.substring(0, index);
    }

    private static String prefixFor(String mode, int number) {
        switch (mode) {
            case "number": return number + ". ";
            case "letter": return letters(number) + ". ";
            case "roman": return roman(number) + ". ";
            case "dash": return "– ";
            case "check": return "✓ ";
            case "arrow": return "➜ ";
            case "star": return "★ ";
            case "diamond": return "◆ ";
            default: return "• ";
        }
    }

    private static String letters(int number) {
        StringBuilder result = new StringBuilder();
        int n = Math.max(1, number);
        while (n > 0) {
            n--;
            result.insert(0, (char) ('A' + (n % 26)));
            n /= 26;
        }
        return result.toString();
    }

    private static String roman(int value) {
        int number = Math.max(1, Math.min(value, 3999));
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] symbols = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                result.append(symbols[i]);
                number -= values[i];
            }
        }
        return result.toString().toUpperCase(Locale.ROOT);
    }
}

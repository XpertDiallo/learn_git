package com.textaura.social;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ListFormatter {
    private static final Pattern PREFIX = Pattern.compile(
        "^(\\s*)(?:(?:\\d+|[A-Za-z]|[IVXLCDMivxlcdm]+)[.)]|[•◦▪▫●○◆◇★☆✓✔➜→–—♥❤⚡📌▲△■□-])\\s+"
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

            if ("indent".equals(mode)) {
                builder.insert(lineStart, "    ");
                delta += 4;
                continue;
            }
            if ("outdent".equals(mode)) {
                int removable = Math.min(4, leadingWhitespaceLength(line));
                if (removable > 0) {
                    builder.delete(lineStart, lineStart + removable);
                    delta -= removable;
                }
                continue;
            }

            Matcher matcher = PREFIX.matcher(line);
            int insertionPoint;
            if (matcher.find()) {
                String indentation = matcher.group(1);
                int prefixEnd = matcher.end();
                builder.delete(lineStart, lineStart + prefixEnd);
                delta -= prefixEnd;
                builder.insert(lineStart, indentation);
                delta += indentation.length();
                insertionPoint = lineStart + indentation.length();
            } else {
                insertionPoint = lineStart + leadingWhitespaceLength(line);
            }

            if (!"remove".equals(mode)) {
                String prefix = prefixFor(mode, index + 1);
                builder.insert(insertionPoint, prefix);
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

    private static int leadingWhitespaceLength(String line) {
        int index = 0;
        while (index < line.length() && Character.isWhitespace(line.charAt(index)) && line.charAt(index) != '\n') index++;
        return index;
    }

    private static String prefixFor(String mode, int number) {
        switch (mode) {
            case "number": return number + ". ";
            case "letter": return letters(number, true) + ". ";
            case "letter_lower": return letters(number, false) + ". ";
            case "roman": return roman(number, true) + ". ";
            case "roman_lower": return roman(number, false) + ". ";
            case "circle": return "○ ";
            case "square": return "■ ";
            case "triangle": return "▲ ";
            case "dash": return "– ";
            case "check": return "✓ ";
            case "arrow": return "➜ ";
            case "star": return "★ ";
            case "diamond": return "◆ ";
            case "heart": return "♥ ";
            case "lightning": return "⚡ ";
            case "pin": return "📌 ";
            default: return "• ";
        }
    }

    private static String letters(int number, boolean upper) {
        StringBuilder result = new StringBuilder();
        int n = Math.max(1, number);
        while (n > 0) {
            n--;
            result.insert(0, (char) ((upper ? 'A' : 'a') + (n % 26)));
            n /= 26;
        }
        return result.toString();
    }

    private static String roman(int value, boolean upper) {
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
        String output = result.toString();
        return upper ? output.toUpperCase(Locale.ROOT) : output.toLowerCase(Locale.ROOT);
    }
}

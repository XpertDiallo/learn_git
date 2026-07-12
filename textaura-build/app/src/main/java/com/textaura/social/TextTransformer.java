package com.textaura.social;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class TextTransformer {
    private TextTransformer() {}

    static String transform(String input, String operation, Locale locale) {
        if (input == null || input.isEmpty()) return input == null ? "" : input;
        Locale safeLocale = locale == null ? Locale.ROOT : locale;
        switch (operation) {
            case "sentence": return sentenceCase(input, safeLocale);
            case "lower": return input.toLowerCase(safeLocale);
            case "upper": return input.toUpperCase(safeLocale);
            case "title": return titleCase(input, safeLocale);
            case "toggle": return toggleCase(input, safeLocale);
            case "sponge": return spongeCase(input, safeLocale);
            case "snake": return joinWords(input, safeLocale, "_", false, false);
            case "kebab": return joinWords(input, safeLocale, "-", false, false);
            case "camel": return joinWords(input, safeLocale, "", true, false);
            case "pascal": return joinWords(input, safeLocale, "", true, true);
            case "no_accents": return removeAccents(input);
            case "no_spaces": return input.replaceAll("\\s+", "");
            case "trim": return trimLines(input);
            case "collapse_spaces": return collapseSpaces(input);
            case "punctuation": return normalizePunctuation(input, safeLocale);
            case "clean_lines": return cleanLines(input);
            case "hashtags": return hashtags(input, safeLocale);
            default: return input;
        }
    }

    private static String sentenceCase(String input, Locale locale) {
        String lower = input.toLowerCase(locale);
        StringBuilder out = new StringBuilder();
        boolean capitalize = true;
        for (int i = 0; i < lower.length();) {
            int cp = lower.codePointAt(i);
            String ch = new String(Character.toChars(cp));
            if (capitalize && Character.isLetter(cp)) {
                out.append(ch.toUpperCase(locale));
                capitalize = false;
            } else {
                out.append(ch);
            }
            if (cp == '.' || cp == '!' || cp == '?' || cp == '\n') capitalize = true;
            i += Character.charCount(cp);
        }
        return out.toString();
    }

    private static String titleCase(String input, Locale locale) {
        String lower = input.toLowerCase(locale);
        StringBuilder out = new StringBuilder();
        boolean capitalize = true;
        for (int i = 0; i < lower.length();) {
            int cp = lower.codePointAt(i);
            String ch = new String(Character.toChars(cp));
            if (Character.isLetterOrDigit(cp)) {
                out.append(capitalize ? ch.toUpperCase(locale) : ch);
                capitalize = false;
            } else {
                out.append(ch);
                capitalize = true;
            }
            i += Character.charCount(cp);
        }
        return out.toString();
    }

    private static String toggleCase(String input, Locale locale) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.length();) {
            int cp = input.codePointAt(i);
            String ch = new String(Character.toChars(cp));
            if (Character.isUpperCase(cp)) out.append(ch.toLowerCase(locale));
            else if (Character.isLowerCase(cp)) out.append(ch.toUpperCase(locale));
            else out.append(ch);
            i += Character.charCount(cp);
        }
        return out.toString();
    }

    private static String spongeCase(String input, Locale locale) {
        StringBuilder out = new StringBuilder();
        boolean upper = true;
        for (int i = 0; i < input.length();) {
            int cp = input.codePointAt(i);
            String ch = new String(Character.toChars(cp));
            if (Character.isLetter(cp)) {
                out.append(upper ? ch.toUpperCase(locale) : ch.toLowerCase(locale));
                upper = !upper;
            } else {
                out.append(ch);
            }
            i += Character.charCount(cp);
        }
        return out.toString();
    }

    private static String joinWords(String input, Locale locale, String separator, boolean camelStyle, boolean capitalizeFirst) {
        String[] raw = input.trim().split("[^\\p{L}\\p{N}]+");
        List<String> words = new ArrayList<>();
        for (String word : raw) if (!word.isEmpty()) words.add(word.toLowerCase(locale));
        if (words.isEmpty()) return "";

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (camelStyle && (i > 0 || capitalizeFirst)) word = capitalizeWord(word, locale);
            if (i > 0) out.append(separator);
            out.append(word);
        }
        return out.toString();
    }

    private static String capitalizeWord(String word, Locale locale) {
        if (word.isEmpty()) return word;
        int cp = word.codePointAt(0);
        int count = Character.charCount(cp);
        return new String(Character.toChars(cp)).toUpperCase(locale) + word.substring(count);
    }

    private static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    }

    private static String trimLines(String input) {
        String[] lines = input.split("\\R", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) result.append('\n');
            result.append(lines[i].trim());
        }
        return result.toString().trim();
    }

    private static String collapseSpaces(String input) {
        return input
            .replaceAll("[\\t\\x0B\\f\\r ]+", " ")
            .replaceAll(" *\\n *", "\\n");
    }

    private static String normalizePunctuation(String input, Locale locale) {
        String value = collapseSpaces(input);
        value = value.replaceAll("\\s+([,\\.])", "$1");
        if ("fr".equals(locale.getLanguage())) {
            value = value.replaceAll("\\s*([;:!?])", " $1");
        } else {
            value = value.replaceAll("\\s+([;:!?])", "$1");
        }
        value = value.replaceAll("([,;:!?])(?=[\\p{L}\\p{N}])", "$1 ");
        value = value.replaceAll("\\.(?=[\\p{L}\\p{N}])", ". ");
        return value;
    }

    private static String cleanLines(String input) {
        String value = trimLines(collapseSpaces(input));
        return value.replaceAll("\\n{3,}", "\\n\\n");
    }

    private static String hashtags(String input, Locale locale) {
        String[] raw = removeAccents(input).split("[^\\p{L}\\p{N}]+");
        Set<String> unique = new LinkedHashSet<>();
        for (String word : raw) {
            if (word.length() < 3) continue;
            unique.add("#" + word.toLowerCase(locale));
            if (unique.size() >= 20) break;
        }
        return String.join(" ", unique);
    }
}

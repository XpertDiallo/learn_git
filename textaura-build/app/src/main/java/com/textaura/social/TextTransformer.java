package com.textaura.social;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

final class TextTransformer {
    private TextTransformer() {}

    static String transform(String input, String operation, Locale locale) {
        if (input == null || input.isEmpty()) return input == null ? "" : input;
        switch (operation) {
            case "sentence": return sentenceCase(input, locale);
            case "lower": return input.toLowerCase(locale);
            case "upper": return input.toUpperCase(locale);
            case "title": return titleCase(input, locale);
            case "toggle": return toggleCase(input, locale);
            case "sponge": return spongeCase(input, locale);
            case "snake": return joinWords(input, locale, "_", false, false);
            case "kebab": return joinWords(input, locale, "-", false, false);
            case "camel": return joinWords(input, locale, "", true, false);
            case "pascal": return joinWords(input, locale, "", true, true);
            case "no_accents": return removeAccents(input);
            case "no_spaces": return input.replaceAll("\\s+", "");
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
        boolean upper = new Random().nextBoolean();
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
}

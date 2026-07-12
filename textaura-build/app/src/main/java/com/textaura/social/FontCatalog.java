package com.textaura.social;

import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class FontCatalog {
    static final class Option {
        final String id;
        final String label;
        final String categoryKey;
        final String family;
        final int style;
        final boolean arabicFriendly;

        Option(String id, String label, String categoryKey, String family, int style, boolean arabicFriendly) {
            this.id = id;
            this.label = label;
            this.categoryKey = categoryKey;
            this.family = family;
            this.style = style;
            this.arabicFriendly = arabicFriendly;
        }

        Typeface typeface() {
            Typeface resolved = Typeface.create(family, style);
            return resolved == null ? Typeface.create(Typeface.DEFAULT, style) : resolved;
        }
    }

    private static final List<Option> ALL;

    static {
        List<Option> fonts = new ArrayList<>();
        add(fonts, "system", "Police système", "font_system", "sans-serif", Typeface.NORMAL, true);
        add(fonts, "times", "Times New Roman", "font_classics", "serif", Typeface.NORMAL, true);
        add(fonts, "arial", "Arial", "font_classics", "sans-serif", Typeface.NORMAL, true);
        add(fonts, "arial_black", "Arial Black", "font_classics", "sans-serif-black", Typeface.BOLD, true);
        add(fonts, "century_gothic", "Century Gothic", "font_classics", "sans-serif-light", Typeface.NORMAL, true);
        add(fonts, "bookman", "Bookman", "font_classics", "serif", Typeface.BOLD, true);
        add(fonts, "cooper_black", "Cooper Black", "font_classics", "serif", Typeface.BOLD, true);

        add(fonts, "centaur", "Centaur", "font_humanes", "serif", Typeface.NORMAL, true);
        add(fonts, "golden_type", "Golden Type", "font_humanes", "serif", Typeface.NORMAL, true);
        add(fonts, "hadriano", "Hadriano", "font_humanes", "serif", Typeface.ITALIC, true);

        add(fonts, "bembo", "Bembo", "font_garaldes", "serif", Typeface.NORMAL, true);
        add(fonts, "garamond", "Garamond", "font_garaldes", "serif", Typeface.NORMAL, true);
        add(fonts, "plantin", "Plantin", "font_garaldes", "serif", Typeface.NORMAL, true);
        add(fonts, "sabon", "Sabon", "font_garaldes", "serif", Typeface.NORMAL, true);

        add(fonts, "baskerville", "Baskerville", "font_reales", "serif", Typeface.NORMAL, true);
        add(fonts, "perpetua", "Perpetua", "font_reales", "serif", Typeface.NORMAL, true);

        add(fonts, "bodoni", "Bodoni", "font_didones", "serif", Typeface.NORMAL, true);
        add(fonts, "didot", "Didot", "font_didones", "serif", Typeface.NORMAL, true);
        add(fonts, "walbaum", "Walbaum", "font_didones", "serif", Typeface.NORMAL, true);

        add(fonts, "clarendon", "Clarendon", "font_mecanes", "serif-monospace", Typeface.BOLD, true);
        add(fonts, "rockwell", "Rockwell", "font_mecanes", "serif-monospace", Typeface.NORMAL, true);
        add(fonts, "serifa", "Serifa", "font_mecanes", "serif-monospace", Typeface.NORMAL, true);

        add(fonts, "futura", "Futura", "font_lineales", "sans-serif", Typeface.NORMAL, true);
        add(fonts, "gill_sans", "Gill Sans", "font_lineales", "sans-serif-light", Typeface.NORMAL, true);
        add(fonts, "kabel", "Kabel", "font_lineales", "sans-serif-condensed", Typeface.NORMAL, true);
        add(fonts, "univers", "Univers", "font_lineales", "sans-serif", Typeface.NORMAL, true);

        add(fonts, "albertus", "Albertus", "font_incises", "sans-serif-medium", Typeface.NORMAL, true);
        add(fonts, "optima", "Optima", "font_incises", "sans-serif-light", Typeface.NORMAL, true);

        add(fonts, "isadora", "Isadora", "font_scriptes", "cursive", Typeface.NORMAL, false);
        add(fonts, "shelley", "Shelley", "font_scriptes", "cursive", Typeface.ITALIC, false);

        add(fonts, "banco", "Banco", "font_manuaires", "casual", Typeface.BOLD, false);
        add(fonts, "libre", "Libre", "font_manuaires", "casual", Typeface.NORMAL, true);
        add(fonts, "ondine", "Ondine", "font_manuaires", "cursive", Typeface.ITALIC, false);
        add(fonts, "post_antiqua", "Post Antiqua", "font_manuaires", "serif", Typeface.ITALIC, true);

        add(fonts, "wilhelm", "Wilhelm Klingspor Gotisch", "font_fractures", "serif", Typeface.BOLD, false);
        add(fonts, "fette_fraktur", "Fette Fraktur", "font_fractures", "serif", Typeface.BOLD, false);

        add(fonts, "garamond_greek", "Garamond grec", "font_non_latin", "serif", Typeface.NORMAL, false);
        add(fonts, "hebraica", "Hebraica", "font_non_latin", "serif", Typeface.NORMAL, false);

        ALL = Collections.unmodifiableList(fonts);
    }

    private FontCatalog() {}

    private static void add(List<Option> list, String id, String label, String categoryKey, String family, int style, boolean arabicFriendly) {
        list.add(new Option(id, label, categoryKey, family, style, arabicFriendly));
    }

    static List<Option> all() {
        return ALL;
    }

    static Option find(String id) {
        if (id == null) return ALL.get(0);
        for (Option option : ALL) if (option.id.equals(id)) return option;
        return ALL.get(0);
    }

    static List<Option> filter(String categoryKey, String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Option> result = new ArrayList<>();
        for (Option option : ALL) {
            boolean categoryMatches = categoryKey == null || categoryKey.isEmpty() || "font_all".equals(categoryKey) || option.categoryKey.equals(categoryKey);
            boolean queryMatches = normalized.isEmpty() || option.label.toLowerCase(Locale.ROOT).contains(normalized);
            if (categoryMatches && queryMatches) result.add(option);
        }
        return result;
    }
}

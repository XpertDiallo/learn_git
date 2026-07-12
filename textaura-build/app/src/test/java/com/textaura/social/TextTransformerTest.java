package com.textaura.social;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class TextTransformerTest {
    @Test
    public void transformsCaseAndNamingConventions() {
        Locale fr = Locale.FRENCH;
        assertEquals("Bonjour. Salut !", TextTransformer.transform("BONJOUR. SALUT !", "sentence", fr));
        assertEquals("smileys_et_émotions", TextTransformer.transform("Smileys et émotions", "snake", fr));
        assertEquals("smileysEtÉmotions", TextTransformer.transform("Smileys et émotions", "camel", fr));
        assertEquals("SmileysEtÉmotions", TextTransformer.transform("Smileys et émotions", "pascal", fr));
    }

    @Test
    public void cleansTextDeterministically() {
        Locale fr = Locale.FRENCH;
        assertEquals("Bonjour le monde", TextTransformer.transform("  Bonjour   le monde  ", "trim", fr));
        assertEquals("Bonjour le monde", TextTransformer.transform("Bonjour   le monde", "collapse_spaces", fr));
        assertEquals("Bonjour ! Comment ?", TextTransformer.transform("Bonjour!Comment?", "punctuation", fr));
        assertEquals("#assurance #afrique #innovation", TextTransformer.transform("Assurance Afrique innovation", "hashtags", fr));
        assertEquals("AbCd", TextTransformer.transform("abcd", "sponge", fr));
    }
}

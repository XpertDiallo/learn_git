package com.textaura.social;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnicodeStylerTest {
    @Test
    public void appliesAndRemovesUnicodeStyles() {
        assertEquals("𝗔𝗯𝗰 𝟭𝟮", UnicodeStyler.apply("Abc 12", TextStyle.BOLD));
        assertEquals("Abc 12", UnicodeStyler.normal("𝗔𝗯𝗰 𝟭𝟮"));
        assertEquals("Ａｂｃ", UnicodeStyler.apply("Abc", TextStyle.FULL));
        assertEquals("Ⓐⓑⓒ", UnicodeStyler.apply("Abc", TextStyle.CIRCLE));
    }
}

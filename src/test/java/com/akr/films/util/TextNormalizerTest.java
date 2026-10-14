package com.akr.films.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TextNormalizerTest {

    @Test
    void shouldReplaceAnyLineBreakWithOneSpace() {
        assertEquals("a b c d", TextNormalizer.singleLine("a\r\nb\n\nc\r\r\n d"));
    }

    @Test
    void shouldTrimTheEnds() {
        assertEquals("a b", TextNormalizer.singleLine("  a\nb\r\n"));
    }

    @Test
    void shouldKeepTextWithoutLineBreaksAndNull() {
        assertEquals("a  b", TextNormalizer.singleLine("a  b"));
        assertNull(TextNormalizer.singleLine(null));
    }

}

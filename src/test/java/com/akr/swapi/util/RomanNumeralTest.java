package com.akr.swapi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RomanNumeralTest {

    @Test
    void shouldConvertOneToTen() {
        String[] expected = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
        for (int i = 1; i <= 10; i++) {
            assertEquals(expected[i - 1], RomanNumeral.toRoman(i));
        }
    }

    @Test
    void shouldRejectOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(0));
        assertThrows(IllegalArgumentException.class, () -> RomanNumeral.toRoman(11));
    }

}

package com.akr.films.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass 
public class RomanNumeral {

    public static final int MIN = 1;
    public static final int MAX = 10;

    private static final Map<Integer, String> ROMAN_BY_NUMBER = Map.of(
            1, "I",
            2, "II",
            3, "III",
            4, "IV",
            5, "V",
            6, "VI",
            7, "VII",
            8, "VIII",
            9, "IX",
            10, "X");

    private static final Map<String, Integer> NUMBER_BY_ROMAN = invert(ROMAN_BY_NUMBER);

    private Map<String, Integer> invert(Map<Integer, String> romanMap) {
        Map<String, Integer> inverted = new HashMap<>();
        romanMap.forEach((number, roman) -> inverted.put(roman, number));
        return inverted;
    }

    public String toRoman(int number) {
        String roman = ROMAN_BY_NUMBER.get(number);
        if (roman == null) {
            throw new IllegalArgumentException("Number must be between " + MIN + " and " + MAX + ": " + number);
        }
        return roman;
    }

    public int toInt(String roman) {
        if(roman == null){
            throw new IllegalArgumentException("Roman numeral can't be null");
        }
        Integer number = NUMBER_BY_ROMAN.get(roman.toUpperCase(Locale.ROOT));
        if (number == null) {
            throw new IllegalArgumentException("Invalid roman numeral: " + roman);
        }
        return number;
    }

}

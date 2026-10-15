package com.akr.films.util;

import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass 
public class TextNormalizer {

    private final Pattern LINE_BREAKS = Pattern.compile("\\s*[\\r\\n]+\\s*");

    public String singleLine(String text) {
        return text == null ? null : LINE_BREAKS.matcher(text).replaceAll(" ").strip();
    }

}

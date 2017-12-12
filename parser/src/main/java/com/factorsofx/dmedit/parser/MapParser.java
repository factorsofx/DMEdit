package com.factorsofx.dmedit.parser;

import java.util.regex.Pattern;

public class MapParser
{
    /**
     * Group 1 is the
     */
    private static final Pattern MAP_KEY_PATTERN = Pattern.compile("\"(\\p{L}+)\"\\s*=\\s*\\((.+)\\)");
}

package com.factorsofx.dmedit.parser.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DMMLoader
{
    /**
     * Group 1 is the TileState key, group 2 is the list of instances in said tilestate
     */
    private static final Pattern MAP_KEY_PATTERN = Pattern.compile("^\"(\\p{L}+)\"\\s*=\\s*\\((.+)\\)");

    /**
     * Groups 1, 2, and 3 are the x, y, and z of the section respectively.
     */
    private static final Pattern MAP_LOCATION_PATTERN = Pattern.compile("^\\((\\d+),(\\d+),(\\d+)\\)\\s*=\\s*\\{\"");

    private File toParse;

    public DMMLoader(File toParse)
    {
        this.toParse = toParse;
    }

    public DMM parseMap() throws IOException
    {
        BufferedReader lineReader = new BufferedReader(new FileReader(toParse));

        String currentLine;
        while((currentLine = lineReader.readLine()) != null)
        {
            Matcher mapKeyMatcher = MAP_KEY_PATTERN.matcher(currentLine);
            if(mapKeyMatcher.matches())
            {

            }
        }

        return null;
    }
}

package com.factorsofx.dmedit.parser;

import java.io.File;

public class ParserTest
{
    public static void main(String... args) throws Exception
    {
        FastDMMParser parser = new FastDMMParser();
        parser.parseDME(new File("Users/Collin/Documents/Programming/ss13_crap/goonstation/goonstation.dme"));
    }
}

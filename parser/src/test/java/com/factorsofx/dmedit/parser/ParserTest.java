package com.factorsofx.dmedit.parser;

import java.io.File;

public class ParserTest
{
    public static void main(String... args) throws Exception
    {
        //ObjectTreeParser parser = new ObjectTreeParser(new File("/Users/Collin/Documents/Programming/ss13_crap/goonstation/goonstation.dme"));
        ObjectTreeParser parser = new ObjectTreeParser(new File("test.dm"));
        ObjectTree tree = parser.parse().get();
        System.out.println(tree);
    }
}

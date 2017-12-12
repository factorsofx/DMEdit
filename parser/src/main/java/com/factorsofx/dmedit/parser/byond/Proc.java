package com.factorsofx.dmedit.parser.byond;

public class Proc
{
    private final ObjectNode parent;
    private final String name;

    public Proc(ObjectNode parent, String name)
    {
        this.parent = parent;
        this.name = name;
    }

    public ObjectNode getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }
}

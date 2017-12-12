package com.factorsofx.dmedit.parser.byond;

import java.util.*;

public class ObjectNode
{
    private ObjectNode parent;
    private String name;

    private Map<String, String> vars = new HashMap<>();
    private Map<String, ObjectNode> subtypes = new HashMap<>();

    private List<ObjectNode> children = new ArrayList<>();

    /**
     * Creates a new object node with the given name and parent.
     * Automatically adds itself to the parent's child list.
     * @param parent
     * @param name
     */
    public ObjectNode(ObjectNode parent, String name)
    {
        this.parent = parent;
        this.name = name;
    }

    public void setVar(String name, String value)
    {
        vars.put(name, value);
    }

    public String getVar(String name)
    {
        if(vars.containsKey(name))
        {
            return vars.get(name);
        }
        else
        {
            if(parent == null) return null;
            return parent.getVar(name);
        }
    }

    public ObjectNode getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public List<ObjectNode> getChildren()
    {
        return Collections.unmodifiableList(children);
    }
}

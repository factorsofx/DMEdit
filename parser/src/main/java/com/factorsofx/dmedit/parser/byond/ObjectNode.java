package com.factorsofx.dmedit.parser.byond;

import java.util.*;

public class ObjectNode extends Observable
{
    private ObjectNode parent;
    private String name;

    private Map<String, String> vars = new HashMap<>();
    private Map<String, ObjectNode> subtypes = new HashMap<>();

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

        if(parent != null)
        {
            parent.subtypes.put(name, this);
        }
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

    public Map<String, ObjectNode> getChildren()
    {
        return Collections.unmodifiableMap(subtypes);
    }
}

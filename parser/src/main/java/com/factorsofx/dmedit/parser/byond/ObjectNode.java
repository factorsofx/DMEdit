package com.factorsofx.dmedit.parser.byond;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

import java.util.*;

/**
 * Represents an object type in the BYOND path tree.
 */
public class ObjectNode implements Comparable<ObjectNode>
{
    private ObjectNode parent;
    private String name;

    private Map<String, String> vars = new HashMap<>();
    private SortedList<ObjectNode> children = new SortedList<>(new BasicEventList<>());

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
            parent.children.add(this);
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

    /**
     * @return The parent of this {@code ObjectNode}
     */
    public ObjectNode getParent()
    {
        return parent;
    }

    /**
     * Returns {@code true} if {@code potentialParent} is anywhere in the parent hierarchy of this node.
     */
    public boolean isParent(ObjectNode potentialParent)
    {
        return parent != null && (parent.equals(potentialParent) || parent.isParent(potentialParent));
    }

    public String getName()
    {
        return name;
    }

    public List<ObjectNode> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    public Optional<ObjectNode> getChild(String name)
    {
        return children.stream().filter((node) -> node.name.equals(name)).findFirst();
    }

    @Override
    public int compareTo(ObjectNode o)
    {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }
}

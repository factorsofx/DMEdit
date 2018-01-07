package com.factorsofx.dmedit.editor.ui.view.tree;

import com.factorsofx.dmedit.parser.code.ObjectNode;

import javax.swing.tree.DefaultMutableTreeNode;

public class ObjectTreeTreeNode extends DefaultMutableTreeNode
{
    private ObjectNode node;

    public ObjectTreeTreeNode(ObjectNode node)
    {
        super(node.getName());
        this.node = node;
    }

    public ObjectNode getNode()
    {
        return node;
    }
}

package com.factorsofx.dmedit.editor.ui.view.tree;

import com.factorsofx.dmedit.parser.code.ObjectTree;
import com.factorsofx.dmedit.parser.code.ObjectTreeListener;
import com.factorsofx.dmedit.parser.code.ObjectNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ObjectTreeTreeModel extends DefaultTreeModel implements ObjectTreeListener
{
    private Map<ObjectNode, ObjectTreeTreeNode> nodeMap = new HashMap<>();

    private List<Runnable> actionList = Collections.synchronizedList(new ArrayList<>());

    public ObjectTreeTreeModel(ObjectTree tree)
    {
        super(null);
        this.setRoot(generateTree(tree.getRootNode()));
        tree.addListener(this);
    }

    private ObjectTreeTreeNode generateTree(ObjectNode parent)
    {
        ObjectTreeTreeNode parentTreeNode = new ObjectTreeTreeNode(parent);
        for(ObjectNode child : parent.getChildren())
        {
            parentTreeNode.add(generateTree(child));
        }
        nodeMap.put(parent, parentTreeNode);
        return parentTreeNode;
    }

    @Override
    public void onNodeAdded(ObjectNode node)
    {
        try
        {
            SwingUtilities.invokeAndWait(() ->
            {
                this.insertNodeInto(generateTree(node), nodeMap.get(node.getParent()), node.getParent().getChildren().indexOf(node));
            });
        }
        catch(InterruptedException e)
        {
            Thread.interrupted();
        }
        catch(InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onNodeRemoved(ObjectNode node)
    {
        SwingUtilities.invokeLater(() ->
                this.removeNodeFromParent(nodeMap.get(node)));
    }
}

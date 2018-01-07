package com.factorsofx.dmedit.editor.ui.view.tree;

import com.factorsofx.dmedit.parser.code.ObjectTree;

import javax.swing.*;
import java.awt.*;

public class ObjectTreeDisplay extends JPanel
{
    private ObjectTree tree;

    public ObjectTreeDisplay(ObjectTree tree)
    {
        this.tree = tree;

        this.setLayout(new BorderLayout());

        JTree jtree;
        this.add(new JScrollPane(jtree = new JTree(new ObjectTreeTreeModel(tree))), BorderLayout.CENTER);

        jtree.setCellRenderer(new ObjectNodeCellRenderer());

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("All nodes");
        comboBox.addItem("Atomic nodes");
        this.add(comboBox, BorderLayout.NORTH);
    }
}

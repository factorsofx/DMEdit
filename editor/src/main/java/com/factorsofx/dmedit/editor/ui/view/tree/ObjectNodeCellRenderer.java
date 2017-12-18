package com.factorsofx.dmedit.editor.ui.view.tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ObjectNodeCellRenderer extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        this.setText(((ObjectTreeTreeNode)value).getNode().getName());
        return this;
    }
}

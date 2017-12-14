package com.factorsofx.dmedit.editor.ui.view;

import com.factorsofx.dmedit.parser.ObjectTree;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that displays the object tree and file tree on the left-hand side.
 */
public class ObjectTreePanel extends JPanel
{
    private JButton refreshButton;



    public ObjectTreePanel(ObjectTree tree)
    {
        this.setLayout(new BorderLayout());
    }
}

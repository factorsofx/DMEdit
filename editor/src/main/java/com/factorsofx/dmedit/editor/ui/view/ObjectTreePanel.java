package com.factorsofx.dmedit.editor.ui.view;

import com.factorsofx.dmedit.editor.ui.controller.ProjectController;
import com.factorsofx.dmedit.editor.ui.view.tree.FileTreeDisplay;
import com.factorsofx.dmedit.editor.ui.view.tree.ObjectNodeCellRenderer;
import com.factorsofx.dmedit.editor.ui.view.tree.ObjectTreeDisplay;
import com.factorsofx.dmedit.editor.ui.view.tree.ObjectTreeTreeModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that displays the object tree and file tree on the left-hand side.
 */
public class ObjectTreePanel extends JPanel
{

    private JTree fileTree;
    private JPanel objectTree;

    private static final String FILE_TREE_KEY = "File Tree";
    private static final String OBJECT_TREE_KEY = "Object Tree";

    public ObjectTreePanel(ProjectController controller)
    {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton refreshButton = new JButton("⟲");
        // refreshButton.addActionListener((actionEvent) -> controller.refreshObjectTree());

        fileTree = new FileTreeDisplay(controller.getDME().getParentFile(), controller);

        objectTree = new ObjectTreeDisplay(controller.getObjectTree());

        JPanel treePanel = new JPanel();
        treePanel.setLayout(new CardLayout());
        treePanel.add(new JScrollPane(fileTree), FILE_TREE_KEY);
        treePanel.add(objectTree, OBJECT_TREE_KEY);

        JComboBox<String> modeSelector = new JComboBox<>(new String[] {FILE_TREE_KEY, OBJECT_TREE_KEY});
        modeSelector.addItemListener((itemEvent) -> ((CardLayout)treePanel.getLayout()).show(treePanel, (String)itemEvent.getItem()));

        gbc.weightx = gbc.weighty = 0;
        gbc.gridx = gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = gbc.gridheight = 1;
        this.add(modeSelector, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 1;
        this.add(refreshButton, gbc);

        gbc.weightx = gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        this.add(treePanel, gbc);
    }
}

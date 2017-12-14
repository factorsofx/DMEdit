package com.factorsofx.dmedit.editor.ui.view;

import com.factorsofx.dmedit.editor.ui.controller.ProjectController;
import com.factorsofx.dmedit.editor.ui.view.editor.Changelog;
import com.factorsofx.dmedit.editor.ui.view.editor.EditorPanel;

import javax.swing.*;

public class MainFrame extends JFrame
{
    private JTabbedPane tabbedPane;

    public MainFrame(ProjectController controller)
    {
        this.setTitle("DMEdit v1.0.0");
        this.setSize(1200, 750);

        tabbedPane = new JTabbedPane();

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, tabbedPane));
        this.setVisible(true);

        openPanel(new Changelog(null));
    }

    public void openPanel(EditorPanel newPanel)
    {
        tabbedPane.addTab(newPanel.getTitle(), newPanel);
    }
}

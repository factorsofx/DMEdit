package com.factorsofx.dmedit.editor.ui.view;

import com.factorsofx.dmedit.editor.ui.controller.ProjectController;
import com.factorsofx.dmedit.editor.ui.view.editor.Changelog;
import com.factorsofx.dmedit.editor.ui.view.editor.EditorAction;
import com.factorsofx.dmedit.editor.ui.view.editor.EditorPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MainFrame extends JFrame
{
    private EditorPanelTabbedPane tabbedPane;

    public MainFrame(ProjectController controller)
    {
        this.setTitle("DMEdit v1.0.0");
        this.setSize(1200, 750);

        tabbedPane = new EditorPanelTabbedPane();

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenu newMenu = new JMenu("New");
        JMenuItem newDM = new JMenuItem("DM (Code file)");
        newMenu.add(newDM);
        JMenuItem newDMM = new JMenuItem("DMM (Map file)");
        newMenu.add(newDMM);
        JMenuItem newDMI = new JMenuItem("DMI (Icon file)");
        newMenu.add(newDMI);
        fileMenu.add(newMenu);

        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);

        this.setLayout(new BorderLayout());
        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new ObjectTreePanel(controller), tabbedPane), BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        for(EditorAction action : EditorAction.values())
        {
            toolBar.add(controller.getActionForEditorAction(action));
        }

        toolBar.addSeparator();

        this.add(toolBar, BorderLayout.NORTH);
        this.setVisible(true);

        openPanel(new Changelog(null));
    }

    public void addTabbedPanelChangeListener(ChangeListener listener)
    {
        tabbedPane.addChangeListener(listener);
    }

    public void openPanel(EditorPanel newPanel)
    {
        tabbedPane.add(newPanel);
    }

    public EditorPanel getActivePanel()
    {
        return tabbedPane.getSelectedPanel();
    }
}

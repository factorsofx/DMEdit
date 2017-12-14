package com.factorsofx.dmedit.editor.ui.view;

import javax.swing.*;

public class MainFrame extends JFrame
{
    private JTabbedPane tabbedPane;

    public MainFrame()
    {
        this.setTitle("DMEdit v1.0.0");
        this.setSize(1200, 750);

        JTabbedPane tabbedPane = new JTabbedPane();


        this.setVisible(true);
    }
}

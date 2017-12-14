package com.factorsofx.dmedit.editor.ui.controller;

import com.factorsofx.dmedit.editor.ui.model.Project;
import com.factorsofx.dmedit.editor.ui.view.MainFrame;

import java.io.File;

/**
 * Overall owner of all the stuff for a single project/window.
 */
public class ProjectController
{
    private final MainFrame mainFrame;

    private final Project project;

    public ProjectController(File dme)
    {
        project = new Project(dme, null);
        mainFrame = new MainFrame(this);
    }
}

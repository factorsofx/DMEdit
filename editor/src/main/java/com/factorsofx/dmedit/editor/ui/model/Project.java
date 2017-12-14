package com.factorsofx.dmedit.editor.ui.model;

import com.factorsofx.dmedit.parser.ObjectTree;

import java.io.File;

public class Project
{
    private ObjectTree tree;

    private final File dme;

    public Project(File dme, ObjectTree tree)
    {
        this.tree = tree;
        this.dme = dme;
    }

    public ObjectTree getTree()
    {
        return tree;
    }

    public File getDme()
    {
        return dme;
    }
}

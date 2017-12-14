package com.factorsofx.dmedit.editor.ui.model;

import com.factorsofx.dmedit.editor.dmi.DMICache;
import com.factorsofx.dmedit.parser.ObjectTree;

import java.io.File;
import java.util.List;

/**
 * A single BYOND project and associated data.
 */
public class Project
{
    private ObjectTree tree;
    private DMICache dmiCache;
    private List<File> openFiles;

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

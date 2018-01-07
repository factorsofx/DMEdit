package com.factorsofx.dmedit.editor.ui.view.editor;

import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;

public enum EditorAction
{
    SAVE("Save", FontIcon.of(Material.SAVE)),
    COPY("Copy", FontIcon.of(Material.CONTENT_COPY)),
    PASTE("Paste", FontIcon.of(Material.CONTENT_PASTE)),
    UNDO("Undo", FontIcon.of(Material.UNDO)),
    REDO("Redo", FontIcon.of(Material.REDO));

    private final String name;
    private final Icon icon;

    EditorAction(String name, Icon icon)
    {
        this.name = name;
        this.icon = icon;
    }

    public String getName()
    {
        return name;
    }

    public Icon getIcon()
    {
        return icon;
    }
}

package com.factorsofx.dmedit.editor.ui.view.editor;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumSet;

public class Changelog extends EditorPanel
{
    public Changelog(File ignored)
    {
        super(ignored, "Changelog");

        this.setLayout(new BorderLayout());
        JEditorPane editorPane;
        try
        {
            String logText = IOUtils.resourceToString("/changelog.html", Charset.defaultCharset());
            editorPane = new JEditorPane("text/html", logText);
            editorPane.setEditable(false);
            this.add(editorPane, BorderLayout.CENTER);
        }
        catch(IOException e)
        {
            JLabel errorLabel = new JLabel("Error loading changelog");
            errorLabel.setForeground(Color.RED);
            this.add(errorLabel, BorderLayout.CENTER);
        }
    }

    @Override
    public EnumSet<EditorAction> getSupportedActions()
    {
        return EnumSet.noneOf(EditorAction.class);
    }

    @Override
    public void editorActionPerformed(EditorAction action) {}
}

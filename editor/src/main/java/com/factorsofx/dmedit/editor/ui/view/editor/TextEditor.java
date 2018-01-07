package com.factorsofx.dmedit.editor.ui.view.editor;

import com.factorsofx.dmedit.editor.DMEdit;
import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class TextEditor extends EditorPanel
{
    private RSyntaxTextArea textArea;

    public TextEditor(File toOpen, String syntaxStyle) throws IOException
    {
        super(toOpen, toOpen.getName());

        textArea = new RSyntaxTextArea(FileUtils.readFileToString(toOpen, Charsets.ISO_8859_1));
        textArea.setSyntaxEditingStyle(syntaxStyle);
        textArea.setCodeFoldingEnabled(true);

        /*try {
            Theme theme = Theme.load(RSyntaxTextArea.class.getResourceAsStream(
                    "/darkTheme.xml"));
            theme.apply(textArea);
        } catch (IOException ioe) { // Never happens
            ioe.printStackTrace();
        }*/

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setFoldIndicatorEnabled(true);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.invalidate();
    }

    @Override
    public EnumSet<EditorAction> getSupportedActions()
    {
        return EnumSet.of(EditorAction.SAVE, EditorAction.UNDO, EditorAction.REDO);
    }

    @Override
    public void editorActionPerformed(EditorAction action)
    {
        switch(action)
        {
            case SAVE:
            {
                save();
            }
            break;
            case UNDO:
            {
                textArea.undoLastAction();
            }
            break;
            case REDO:
            {
                textArea.redoLastAction();
            }
            break;
        }
    }

    private void save()
    {
        new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                return null;
            }
        }.execute();
    }
}

package com.factorsofx.dmedit.editor.ui.view.editor;

import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class DMEditor extends EditorPanel
{
    private RSyntaxTextArea textArea;

    public DMEditor(File toOpen)
    {
        RSyntaxDocument doc = new RSyntaxDocument("text/dm");
        try
        {
            doc.insertString(0, FileUtils.readFileToString(toOpen, StandardCharsets.ISO_8859_1), null);
        }
        catch(BadLocationException ignored) {} // Can't be thrown
        catch(IOException e)
        {
            throw new UncheckedIOException(e);
        }
        doc.getDocumentProperties().put("tabSize", 4);
        RTextScrollPane textScrollPane = new RTextScrollPane();
        textArea = new RSyntaxTextArea(doc);
        textScrollPane.add(textArea);
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

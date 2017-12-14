package com.factorsofx.dmedit.editor.ui.view.editor;

import com.factorsofx.dmedit.parser.util.Observable;
import com.factorsofx.dmedit.parser.util.Observer;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class EditorPanel extends JPanel implements Observable<String>
{
    private String title;
    protected final File file;

    public EditorPanel(File file, String title)
    {
        this.file = file;
        this.title = title;
    }

    private List<Observer<String>> observerList = new ArrayList<>();

    protected void updateTitle(String title)
    {
        this.title = title;
        observerList.forEach((observer) -> observer.notify(this, title));
    }

    public String getTitle()
    {
        return title;
    }

    public abstract EnumSet<EditorAction> getSupportedActions();

    public abstract void editorActionPerformed(EditorAction action);

    @Override
    public void addObserver(Observer<String> observer)
    {
        observerList.add(observer);
    }
}

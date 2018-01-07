package com.factorsofx.dmedit.editor.ui.controller;

import com.factorsofx.dmedit.editor.ui.model.Project;
import com.factorsofx.dmedit.editor.ui.view.MainFrame;
import com.factorsofx.dmedit.editor.ui.view.editor.TextEditor;
import com.factorsofx.dmedit.editor.ui.view.editor.EditorAction;
import com.factorsofx.dmedit.parser.code.ObjectTree;
import com.factorsofx.dmedit.parser.code.ObjectTreeParser;
import com.factorsofx.dmedit.parser.code.ObjectNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Overall owner of all the stuff for a single project/window.
 */
public class ProjectController
{
    private final MainFrame mainFrame;

    private final Project project;

    private Map<EditorAction, Action> editorActionMap = new EnumMap<>(EditorAction.class);
    {
        for(EditorAction action : EditorAction.values())
        {
            editorActionMap.put(action, new EditorActionAction(action));
        }
    }

    public ProjectController(File dme)
    {
        ObjectTree tree = new ObjectTree();
        ObjectTreeParser parser = new ObjectTreeParser(dme, tree);
        SwingWorker<Void, ObjectNode> worker = new SwingWorker<Void, ObjectNode>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                try
                {
                    parser.parseSynchronously();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done()
            {
                //mainFrame.
            }
        };
        worker.execute();
        project = new Project(dme, tree);
        mainFrame = new MainFrame(this);

        mainFrame.addTabbedPanelChangeListener(new ControllerTabListener());
    }

    public File getDME()
    {
        return project.getDme();
    }

    public void open(File file)
    {
        //JOptionPane.showMessageDialog(null, "opening file " + file.getPath());
        String[] splitName = file.getName().split("\\.");
        try
        {
            switch(splitName[splitName.length - 1])
            {
                case "dmi":
                    JOptionPane.showMessageDialog(mainFrame, "Icon editor is not yet implemented", "Sorry 🙁", JOptionPane.ERROR_MESSAGE);
                    break;
                case "dmm":
                    JOptionPane.showMessageDialog(mainFrame, "Map editor is not yet implemented", "Sorry 🙁", JOptionPane.ERROR_MESSAGE);
                    break;
                case "dm":
                case "dme":
                    mainFrame.openPanel(new TextEditor(file, "text/dm"));
                    break;
                case "css":
                    mainFrame.openPanel(new TextEditor(file, "text/css"));
                    break;
                case "html":
                    mainFrame.openPanel(new TextEditor(file, "text/html"));
                    break;
                case "js":
                    mainFrame.openPanel(new TextEditor(file, "text/javascript"));
                    break;
                case "json":
                    mainFrame.openPanel(new TextEditor(file, "text/json"));
                    break;
                default:
                    JOptionPane.showMessageDialog(mainFrame, "Whatever format this is is not yet implemented", "Sorry 🙁", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Exception opening file - " + e.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ObjectTree getObjectTree()
    {
        return project.getTree();
    }

    public Action getActionForEditorAction(EditorAction action)
    {
        return editorActionMap.get(action);
    }

    private class ControllerTabListener implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent e)
        {
            editorActionMap.forEach((ea, a) -> a.setEnabled(a.isEnabled()));
        }
    }

    private class EditorActionAction extends AbstractAction
    {
        private final EditorAction action;

        EditorActionAction(EditorAction action)
        {
            this.action = action;
            this.putValue(Action.SMALL_ICON, action.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(mainFrame.getActivePanel().getSupportedActions().contains(action))
            {
                mainFrame.getActivePanel().editorActionPerformed(action);
            }
        }

        @Override
        public boolean isEnabled()
        {
            return mainFrame != null && mainFrame.getActivePanel() != null && mainFrame.getActivePanel()   .getSupportedActions().contains(action);
        }
    }
}

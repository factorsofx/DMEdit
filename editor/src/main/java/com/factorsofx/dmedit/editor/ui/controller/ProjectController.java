package com.factorsofx.dmedit.editor.ui.controller;

import com.factorsofx.dmedit.editor.ui.model.Project;
import com.factorsofx.dmedit.editor.ui.view.MainFrame;
import com.factorsofx.dmedit.editor.ui.view.editor.DMEditor;
import com.factorsofx.dmedit.parser.ObjectTree;
import com.factorsofx.dmedit.parser.ObjectTreeParser;
import com.factorsofx.dmedit.parser.byond.ObjectNode;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Overall owner of all the stuff for a single project/window.
 */
public class ProjectController
{
    private final MainFrame mainFrame;

    private final Project project;

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
                    mainFrame.openPanel(new DMEditor(file));
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

    public void refreshObjectTree()
    {

    }
}

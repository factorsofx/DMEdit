package com.factorsofx.dmedit.editor.ui.view;

import com.factorsofx.dmedit.parser.util.AbstractObservable;
import com.factorsofx.dmedit.parser.util.Observer;
import com.factorsofx.dmedit.parser.ObjectTree;

import javax.swing.*;
import java.awt.*;

public class ObjectTreePanel extends JPanel implements Observer<ObjectTree>
{
    private JButton refreshButton;


    public ObjectTreePanel(ObjectTree tree)
    {
        tree.addObserver(this);

        this.setLayout(new BorderLayout());


    }

    @Override
    public void notify(AbstractObservable<ObjectTree> observable, ObjectTree arg)
    {
        SwingUtilities.invokeLater(() ->
        {

        });
    }
}

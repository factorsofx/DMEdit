package com.factorsofx.dmedit.editor.ui.view.tree;

import com.factorsofx.dmedit.editor.ui.controller.ProjectController;
import com.google.common.collect.Ordering;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class FileTreeDisplay extends JTree
{
    public FileTreeDisplay(File file, ProjectController controller)
    {
        super(scan(file));
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2)
                {
                    NamedMutableTreeNode<File> node = (NamedMutableTreeNode<File>)FileTreeDisplay.this.getLastSelectedPathComponent();
                    controller.open(node.getValue());
                }
            }
        });
    }

    private static MutableTreeNode scan(File node)
    {
        NamedMutableTreeNode<File> ret = new NamedMutableTreeNode<>(node, node.getName());

        if(node.isDirectory())
        {
            List<File> files = Arrays.asList(Optional.ofNullable(node.listFiles((FileFilter) FileFilterUtils.or(
                            new SuffixFileFilter(new String[]{"dm", "dmm", "dmi", "dme", "dms", "css", "html", "js", "json"}),
                            DirectoryFileFilter.INSTANCE))).orElse(new File[]{}));
            files.sort(Ordering.from((f1, f2) ->
            {
                //A comparator that puts directories above files in the tree,
                //for ease of navigation.
                File file1 = (File)f1;
                File file2 = (File)f2;
                if(file1.isDirectory() && file2.isFile()) return -1;
                if(file1.isFile() && file2.isDirectory()) return 1;
                return 0;
            }).compound(Comparator.comparing((file) -> file.getName().toLowerCase())));
            for(File subNode : files)
            {
                ret.add(scan(subNode));
            }
        }
        return ret;
    }

    private static class NamedMutableTreeNode<T> extends DefaultMutableTreeNode
    {
        private T value;

        NamedMutableTreeNode(T value, String name)
        {
            super(name);
            this.value = value;
        }

        T getValue()
        {
            return value;
        }
    }
}
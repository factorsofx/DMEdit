package com.factorsofx.dmedit.editor.ui.view;

import com.factorsofx.dmedit.editor.ui.view.editor.EditorPanel;
import com.factorsofx.dmedit.parser.util.Observable;
import com.factorsofx.dmedit.parser.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorPanelTabbedPane extends JPanel
{
    private JTabbedPane tabbedPane;

    public EditorPanelTabbedPane()
    {
        this.tabbedPane = new JTabbedPane();
        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    public void add(EditorPanel tab)
    {
        tabbedPane.add(tab.getTitle(), tab);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(tab), new CloseableTabComponent(tab));
        tabbedPane.setSelectedComponent(tab);
    }

    private class TabCloseButton extends JButton
    {
        private Font defaultFont;
        private Font boldFont;

        public TabCloseButton(EditorPanel ourPanel)
        {
            this.setText("◎");
            this.setUI(new BasicButtonUI());
            this.setBorder(new EmptyBorder(0, 5, 0, 0));
            this.setOpaque(false);
            this.addActionListener((action) -> SwingUtilities.invokeLater(() -> tabbedPane.remove(ourPanel)));
            this.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {
                    TabCloseButton.this.setBold(true);
                    TabCloseButton.this.setText("◉");
                }

                @Override
                public void mouseExited(MouseEvent e)
                {
                    TabCloseButton.this.setBold(false);
                    TabCloseButton.this.setText("◎");
                }
            });
        }

        public void setBold(boolean bold)
        {
            if (defaultFont == null)
            {
                defaultFont = this.getFont();
                boldFont = defaultFont.deriveFont(Font.BOLD);
            }
            this.setFont(bold? boldFont : defaultFont);
        }
    }

    private class CloseableTabComponent extends JPanel implements Observer<String>
    {
        private TabCloseButton closeButton;
        private JLabel nameLabel;
        private EditorPanel ourTab;

        public CloseableTabComponent(EditorPanel ourTab)
        {
            this.ourTab = ourTab;
            ourTab.addObserver(this);

            this.setLayout(new GridBagLayout());
            this.setOpaque(false);

            closeButton = new TabCloseButton(ourTab);
            nameLabel = new JLabel()
            {
                @Override
                public String getText()
                {
                    return ourTab.getTitle();
                }
            };

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;

            add(nameLabel, gbc);

            gbc.gridx++;
            gbc.weightx = 0;
            add(closeButton, gbc);
        }

        @Override
        public void notify(Observable observable, String arg)
        {
            nameLabel.setText(ourTab.getTitle());
        }
    }

    // Delegate stuff

    public void addChangeListener(ChangeListener l)
    {
        tabbedPane.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l)
    {
        tabbedPane.removeChangeListener(l);
    }

    public ChangeListener[] getChangeListeners()
    {
        return tabbedPane.getChangeListeners();
    }
}

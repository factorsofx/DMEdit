package com.factorsofx.dmedit.editor;

import com.factorsofx.dmedit.editor.ui.MainFrame;

import javax.swing.*;
import java.util.Arrays;

public class DreamGenesis
{
    public static void main(String... args)
    {
        // Set LAF to Nimbus
        Arrays.stream(UIManager.getInstalledLookAndFeels())
                .filter((info) -> info.getName().equals("Nimbus"))
                .map(UIManager.LookAndFeelInfo::getClassName)
                .findFirst()
                .ifPresent(LambdaUtils.uncheckedConsumer(UIManager::setLookAndFeel));

        // Create editor frame
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

package com.factorsofx.dmedit.editor;

import com.factorsofx.dmedit.editor.ui.view.MainFrame;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import javax.swing.*;

public class DMEdit
{
    public static void main(String... args)
    {

        SubstanceLookAndFeel.setSkin("org.pushingpixels.substance.api.skin.GraphiteSkin");

        // Set LAF to Nimbus
        /*Arrays.stream(UIManager.getInstalledLookAndFeels())
                .filter((info) -> info.getName().equals("Nimbus"))
                .map(UIManager.LookAndFeelInfo::getClassName)
                .findFirst()
                .ifPresent(LambdaUtils.uncheckedConsumer(UIManager::setLookAndFeel));*/

        // Create editor frame
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

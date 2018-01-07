package com.factorsofx.dmedit.editor;

import com.factorsofx.dmedit.editor.lexer.DMFoldParser;
import com.factorsofx.dmedit.editor.ui.controller.ProjectController;
import com.factorsofx.dmedit.editor.ui.view.MainFrame;
import com.factorsofx.dmedit.editor.util.LambdaUtils;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessSkin;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DMEdit
{
    public static void main(String... args)
    {
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/dm", "com.factorsofx.dmedit.editor.lexer.DMTokenMaker");
        FoldParserManager.get().addFoldParserMapping("text/dm", new DMFoldParser());

        SubstanceLookAndFeel.setSkin(org.pushingpixels.substance.api.skin.BusinessSkin.class.getName());

        // Set LAF to Nimbus
        /*Arrays.stream(UIManager.getInstalledLookAndFeels())
                .filter((info) -> info.getName().equals("Nimbus"))
                .map(UIManager.LookAndFeelInfo::getClassName)
                .findFirst()
                .ifPresent(LambdaUtils.uncheckedConsumer(UIManager::setLookAndFeel));*/

        SwingUtilities.invokeLater(() ->
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load DME");
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f)
                {
                    return f.getName().endsWith(".dme");
                }

                @Override
                public String getDescription()
                {
                    return "DME - BYOND Environments";
                }
            });
            if(fileChooser.showDialog(null, "Load") == JFileChooser.APPROVE_OPTION)
            {
                File dme = fileChooser.getSelectedFile();
                new ProjectController(dme);
            }
            else
            {
                System.exit(0);
            }
        });
    }
}

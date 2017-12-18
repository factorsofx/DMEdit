package com.factorsofx.dmedit.editor.lexer;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;

import javax.swing.text.BadLocationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DMFoldParser implements FoldParser
{
    @Override
    public List<Fold> getFolds(RSyntaxTextArea textArea)
    {
        try
        {
            List<Fold> folds = new ArrayList<>();
            Fold currentFold = null;

            String[] lines = textArea.getText().split("\n");
            int indents = 0;
            int lastLineOffset = 0;
            int currentLineOffset = 0;
            for(int i = 0; i < lines.length; i++)
            {
                String line = lines[i];
                int currentIndents = 0;
                for(int j = 0; j < line.length(); j++)
                {
                    if(line.charAt(j) == '\t')
                    {
                        currentIndents++;
                    }
                    else
                    {
                        break;
                    }
                }
                if(currentIndents > indents && !line.trim().isEmpty())
                {
                    if(currentFold != null)
                    {
                        currentFold = currentFold.createChild(FoldType.CODE, currentLineOffset - 1);
                    }
                    else
                    {
                        currentFold = new Fold(FoldType.CODE, textArea, currentLineOffset - 1);
                    }
                    folds.add(currentFold);
                }
                if(currentIndents < indents && !line.trim().isEmpty())
                {
                    if(currentFold != null)
                    {
                        for(int j = currentIndents; j < indents; j++)
                        {
                            currentFold.setEndOffset(lastLineOffset);
                            currentFold = currentFold.getParent();
                        }
                    }
                    else
                    {
                        assert currentIndents == 0;
                    }
                }
                if(!line.trim().isEmpty())
                {
                    indents = currentIndents;
                }
                lastLineOffset = currentLineOffset;
                currentLineOffset += line.length() + 1;
            }
            return folds;
        }
        catch(BadLocationException e)
        {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
}

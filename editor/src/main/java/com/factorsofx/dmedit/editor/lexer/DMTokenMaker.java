package com.factorsofx.dmedit.editor.lexer;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.swing.text.Segment;
import java.util.Arrays;

public class DMTokenMaker extends AbstractTokenMaker
{
    @Override
    public TokenMap getWordsToHighlight()
    {
        TokenMap tokenMap = new TokenMap();

        // Keywords
        tokenMap.put("as", Token.RESERVED_WORD);
        tokenMap.put("null", Token.RESERVED_WORD);
        tokenMap.put("new", Token.RESERVED_WORD);
        tokenMap.put("set", Token.RESERVED_WORD);

        // Special types
        tokenMap.put("usr", Token.RESERVED_WORD_2);
        tokenMap.put("src", Token.RESERVED_WORD_2);

        // Preprocessor
        tokenMap.put("#define", Token.PREPROCESSOR);
        tokenMap.put("#if", Token.PREPROCESSOR);
        tokenMap.put("#elif", Token.PREPROCESSOR);
        tokenMap.put("#ifdef", Token.PREPROCESSOR);
        tokenMap.put("#ifndef", Token.PREPROCESSOR);
        tokenMap.put("#else", Token.PREPROCESSOR);
        tokenMap.put("#endif", Token.PREPROCESSOR);
        tokenMap.put("#include", Token.PREPROCESSOR);
        tokenMap.put("#error", Token.PREPROCESSOR);
        tokenMap.put("#warn", Token.PREPROCESSOR);

        // Operators
        Arrays.asList("!", "<", ">", ">=", "<=", "==", "!=", "<>", "&&", "||", "+", "-", "*", "/", "**", "%", "++", "--",
                "~", "<<", ">>", "&", "|", "^").forEach((op) -> tokenMap.put(op, Token.OPERATOR));

        return tokenMap;
    }

    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset)
    {
        return null;
    }
}

package com.joshuac.deduce.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Accepts perfectly formed sentences and returns tokens. A perfectly formed
 * sentence has spaces after sentences and no extra punctuation.
 * 
 * @author jac
 *
 */
public class SimpleScanner implements Scanner

{

    private static final String DELIMITER = " ";

    @Override
    public List<Token> tokenize(String data)
    {
        String[] possibleWords = data.split(DELIMITER);

        if (dataContainsNoTokens(possibleWords))
        {
            return new ArrayList<Token>();
        }

        List<Token> tokens = new ArrayList<Token>(possibleWords.length);

        for (String possibleWord : possibleWords)
        {
            int lastValidIndex = possibleWord.length() - 1;

            char lastCharacterInWord = possibleWord.charAt(lastValidIndex);

            if (lastCharacterIsLetter(lastCharacterInWord))
            {
                tokens.add(new Token(possibleWord));
            }
            else
            {
                tokens.add(new Token(possibleWord.substring(0, lastValidIndex)));
                tokens.add(new Token(possibleWord.charAt(lastValidIndex)));
            }
        }

        return tokens;
    }

    private boolean dataContainsNoTokens(String[] data)
    {
        return data.length == 1 && data[0].equals("");
    }

    private boolean lastCharacterIsLetter(char character)
    {
        return Character.isUpperCase(character) || Character.isLowerCase(character);
    }

}

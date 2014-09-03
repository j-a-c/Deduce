package com.joshuac.deduce.parser;

import com.joshuac.deduce.classifier.WordType;
import com.joshuac.deduce.scanner.Token;

public class ParseException extends Exception
{

    private static final long serialVersionUID = -7031241751205575594L;

    public ParseException(Token token, WordType actualWordType)
    {
        super(String.format("Error when parsing '%s' with WordType %s", token.data, actualWordType));
    }

    public ParseException(WordType expectedWordType, Token token, WordType actualWordType)
    {
        super(String.format("Expected WordType %s when parsing '%s', but received %s",
                expectedWordType, token.data, actualWordType));
    }
}

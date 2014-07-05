package com.joshuac.deduce.classifier;

import com.joshuac.deduce.scanner.Token;

public class HardcodedClassifier implements Classifier
{

    @Override
    public WordType classify(Token token)
    {
        switch (token.data)
        {
        case "The":
            return WordType.Determiner;
        case "ball":
        case "color":
            return WordType.Noun;
        case "is":
            return WordType.Verb;
        case "red":
        case "round":
            return WordType.Adjective;
        case ".":
            return WordType.Period;
        default:
            throw new RuntimeException("Word not recognized! : " + token.data);
        }
    }
}

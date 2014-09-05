package com.joshuac.deduce.classifier;

import com.joshuac.deduce.scanner.Token;

public class HardcodedClassifier implements Classifier
{

    @Override
    public boolean isArticle(Token token)
    {
        switch (token.data)
        {
        case "The":
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isNoun(Token token)
    {
        switch (token.data)
        {
        case "ball":
        case "color":
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isPronoun(Token token)
    {
        switch (token.data)
        {
        default:
            return false;
        }
    }

    @Override
    public boolean isAdjective(Token token)
    {
        switch (token.data)
        {
        case "red":
        case "round":
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isVerb(Token token)
    {
        switch (token.data)
        {
        case "is":
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isAdverb(Token token)
    {
        switch (token.data)
        {
        default:
            return false;
        }
    }

    @Override
    public boolean isConjunction(Token token)
    {
        switch (token.data)
        {
        default:
            return false;
        }
    }

    @Override
    public boolean isPreposition(Token token)
    {
        switch (token.data)
        {
        default:
            return false;
        }
    }

    @Override
    public boolean isInterjection(Token token)
    {
        switch (token.data)
        {
        default:
            return false;
        }
    }

    @Override
    public boolean isPunctuation(Token token)
    {
        switch (token.data)
        {
        case ".":
            return true;
        default:
            return false;
        }
    }
}

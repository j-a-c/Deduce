package com.joshuac.deduce.classifier;

import com.joshuac.deduce.scanner.Token;

public class HardcodedClassifier implements Classifier
{

    @Override
    public boolean isArticle(Token token)
    {
        switch (token.data)
        {
        case "the":
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
        case "cars":
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
        case "race":
        case "rolls":
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

    @Override
    public boolean isLinkingVerb(Token token)
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
    public boolean isIntransitiveVerb(Token token)
    {
        switch (token.data)
        {
        case "race":
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isEndingPunctuation(Token token)
    {
        switch (token.data)
        {
        case ("."):
        case ("?"):
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isSubordinateConjunction(Token token)
    {
        switch (token.data)
        {
        case ("when"):
        case ("whenever"):
        case ("where"):
        case ("wherever"):
        case ("whether"):
            return true;
        default:
            return false;
        }
    }
}

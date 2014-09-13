package com.joshuac.deduce.classifier;

import com.joshuac.deduce.scanner.Token;

public class HardcodedClassifier implements Classifier
{

    @Override
    public boolean isDeterminer(Token token)
    {
        // TODO Add all determiner types.
        return isArticle(token);
    }

    @Override
    public boolean isVerb(Token token)
    {
        // TODO Add all verb types.
        return isIntransitiveVerb(token) || isLinkingVerb(token) || isTransitiveVerb(token);
    }

    @Override
    public boolean isArticle(Token token)
    {
        switch (token.data)
        {
        case "a":
        case "A":
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
        case "apple":
        case "ball":
        case "book":
        case "cars":
        case "color":
        case "floor":
        case "fruit":
        case "horse":
        case "pasture":
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
        case "small":
        case "smallest":
        case "wet":
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
        case "about":
        case "above":
        case "across":
        case "after":
        case "against":
        case "in":
        case "on":
            return true;
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
    public boolean isTransitiveVerb(Token token)
    {
        switch (token.data)
        {
        case "ate":
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
    public boolean isComma(Token token)
    {
        switch (token.data)
        {
        case (","):
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
        case ("When"):
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

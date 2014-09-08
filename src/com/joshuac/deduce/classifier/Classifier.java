package com.joshuac.deduce.classifier;

import com.joshuac.deduce.scanner.Token;

public interface Classifier
{
    public boolean isArticle(Token token);

    public boolean isNoun(Token token);

    public boolean isPronoun(Token token);

    public boolean isAdjective(Token token);

    public boolean isVerb(Token token);

    public boolean isAdverb(Token token);

    public boolean isConjunction(Token token);

    public boolean isPreposition(Token token);

    public boolean isInterjection(Token token);

    public boolean isPunctuation(Token token);

    // Verb Types

    public boolean isLinkingVerb(Token token);

    public boolean isIntransitiveVerb(Token token);

    // Conjunction Types

    public boolean isSubordinateConjunction(Token token);

    // Punctuation Types

    public boolean isEndingPunctuation(Token token);

    public boolean isComma(Token token);

}

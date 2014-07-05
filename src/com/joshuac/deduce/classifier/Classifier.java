package com.joshuac.deduce.classifier;

import com.joshuac.deduce.scanner.Token;

public interface Classifier
{
    public WordType classify(Token token);
}

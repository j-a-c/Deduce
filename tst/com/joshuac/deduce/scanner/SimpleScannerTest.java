package com.joshuac.deduce.scanner;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SimpleScannerTest
{
    private Scanner scanner;

    @Before
    public void setup()
    {
        scanner = new SimpleScanner();
    }

    @Test
    public void emptyData()
    {
        List<Token> tokens = scanner.tokenize("");
        assertTrue(tokens.size() == 0);
    }

    @Test
    public void spaceData()
    {
        List<Token> tokens = scanner.tokenize("");
        assertTrue(tokens.size() == 0);
    }

    @Test
    public void singleSentence()
    {
        String sentence = "The ball is red.";
        int tokensInSentence = 5;

        List<Token> tokens = scanner.tokenize(sentence);

        assertTrue(tokens.size() == tokensInSentence);
    }

    @Test
    public void twoSentences()
    {
        String sentence = "The ball is red. Hello, my name is Josh.";
        int tokensInSentence = 12;

        List<Token> tokens = scanner.tokenize(sentence);

        assertTrue(tokens.size() == tokensInSentence);
    }
}

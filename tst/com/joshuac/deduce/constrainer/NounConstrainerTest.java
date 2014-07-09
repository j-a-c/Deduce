package com.joshuac.deduce.constrainer;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.joshuac.deduce.Node;
import com.joshuac.deduce.classifier.HardcodedClassifier;
import com.joshuac.deduce.parser.Parser;
import com.joshuac.deduce.parser.SimpleParser;
import com.joshuac.deduce.scanner.Scanner;
import com.joshuac.deduce.scanner.SimpleScanner;
import com.joshuac.deduce.scanner.Token;

public class NounConstrainerTest
{

    private Scanner scanner;
    private Parser parser;
    private Constrainer constrainer;

    @Before
    public void setup()
    {
        scanner = new SimpleScanner();
        parser = new SimpleParser(new HardcodedClassifier());
        constrainer = new NounConstrainer();
    }

    @Test
    public void todo()
    {

    }

    private Node parse(String sentence)
    {
        List<Token> tokens = scanner.tokenize(sentence);
        return parser.parse(tokens);
    }
}

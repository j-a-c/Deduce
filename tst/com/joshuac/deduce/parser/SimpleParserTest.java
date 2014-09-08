package com.joshuac.deduce.parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.joshuac.deduce.Node;
import com.joshuac.deduce.classifier.HardcodedClassifier;
import com.joshuac.deduce.parser.node.*;
import com.joshuac.deduce.scanner.Scanner;
import com.joshuac.deduce.scanner.SimpleScanner;
import com.joshuac.deduce.scanner.Token;

public class SimpleParserTest
{
    private Scanner scanner;
    private Parser parser;

    @Before
    public void setup()
    {
        scanner = new SimpleScanner();
        parser = new SimpleParser(new HardcodedClassifier());
    }

    @Test
    public void emptySentence()
    {
        String sentence = "";
        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 0);
    }

    @Test
    public void singleSentence()
    {
        String sentence = "The ball is red.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode instanceof SentenceNode);
        assertTrue(sentenceNode.getNumberOfChildren() == 2);

        Node clauseNode = sentenceNode.getChild(0);
        assertTrue(clauseNode instanceof ClauseNode);
        assertTrue(clauseNode.getNumberOfChildren() == 2);
        assertTrue(clauseNode.getChild(0) instanceof NounPhraseNode);
        assertTrue(clauseNode.getChild(1) instanceof VerbPhraseNode);

        Node punctuationNode = sentenceNode.getChild(1);
        assertTrue(punctuationNode instanceof PunctuationNode);
        assertTrue(punctuationNode.getNumberOfChildren() == 1);

    }

    @Test
    public void twoSentences()
    {
        String sentence = "The ball is red. The ball is round.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 2);

    }

    @Test
    public void intransitiveVerb()
    {
        String sentence = "The cars race.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);
    }

    private Node parse(String sentence)
    {
        List<Token> tokens = scanner.tokenize(sentence);
        return parser.parse(tokens);
    }
}

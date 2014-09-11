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

        // [The ball is red.]
        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        // [The ball is red][.]
        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode instanceof SentenceNode);
        assertTrue(sentenceNode.getNumberOfChildren() == 2);

        // [The ball] [is red]
        Node clauseNode = sentenceNode.getChild(0);
        assertTrue(clauseNode instanceof ClauseNode);
        assertTrue(clauseNode.getNumberOfChildren() == 2);
        assertTrue(clauseNode.getChild(0) instanceof NounPhraseNode);
        assertTrue(clauseNode.getChild(1) instanceof VerbPhraseNode);
        // TODO Break down

        // [.]
        Node punctuationNode = sentenceNode.getChild(1);
        assertTrue(punctuationNode instanceof EndingPunctuationNode);
        assertTrue(punctuationNode.getNumberOfChildren() == 1);

    }

    @Test
    public void twoSentences()
    {
        String sentence = "The ball is red. The ball is round.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        // [The ball is red.] [The ball is round.]
        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 2);
        // TODO Break down

    }

    @Test
    public void intransitiveVerb()
    {
        String sentence = "The cars race.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        // [The cars race.]
        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);
        // TODO Break down
    }

    @Test
    public void clause_subordinate_clause_endingPunctuation()
    {
        String sentence = "The ball is red when the cars race.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        // [The ball is red when the cars race.]
        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        // [The ball is red] [when the cars race][.]
        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode.getNumberOfChildren() == 3);

        assertTrue(sentenceNode.getChild(0) instanceof ClauseNode);
        // TODO Break down

        assertTrue(sentenceNode.getChild(1) instanceof SubordinateClauseNode);
        // TODO Break down

        assertTrue(sentenceNode.getChild(2) instanceof EndingPunctuationNode);
    }

    @Test
    public void subordinateClause_comma_clause_endingPunctuation()
    {
        String sentence = "When the cars race, the ball is red.";

        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        // [When the cars race, the ball is red.]
        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        // [When the cars race,] [the ball is red][.]
        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode.getNumberOfChildren() == 3);

        assertTrue(sentenceNode.getChild(0) instanceof SubordinateClauseNode);
        // TODO Break down

        assertTrue(sentenceNode.getChild(1) instanceof ClauseNode);
        // TODO Break down

        assertTrue(sentenceNode.getChild(2) instanceof EndingPunctuationNode);
    }

    @Test
    public void appositiveMiddle()
    {
        // TODO Implement test
        String sentence = "The horse, the smallest horse in the pasture, ate the apple.";
        Node rootNode = parse(sentence);
    }

    @Test
    public void appositiveEnd()
    {
        // TODO Implement test
        String sentence = "The horse ate the apple, a small fruit.";
        Node rootNode = parse(sentence);
    }

    @Test
    public void NP_PP_VP_ADJP()
    {
        String sentence = "The book on the floor is wet.";
        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        // [The book on the floor is wet][.]
        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode.getNumberOfChildren() == 2);

        // [The book on the floor] [is wet]
        Node clauseNode = sentenceNode.getChild(0);
        assertTrue(clauseNode instanceof ClauseNode);
        assertTrue(clauseNode.getNumberOfChildren() == 2);

        // [The] [book] [on the floor]
        Node nounPhraseNode = clauseNode.getChild(0);
        assertTrue(nounPhraseNode instanceof NounPhraseNode);
        assertTrue(nounPhraseNode.getNumberOfChildren() == 3);
        // TODO Break down

        // [is wet]
        Node verbPhraseNode = clauseNode.getChild(1);
        assertTrue(verbPhraseNode instanceof VerbPhraseNode);
        assertTrue(verbPhraseNode.getNumberOfChildren() == 2);
        // TODO Break down

        // [.]
        assertTrue(sentenceNode.getChild(1) instanceof EndingPunctuationNode);

    }

    private Node parse(String sentence)
    {
        List<Token> tokens = scanner.tokenize(sentence);
        Node rootNode = parser.parse(tokens);
        return rootNode;
    }
}

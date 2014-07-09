package com.joshuac.injector;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.joshuac.deduce.Node;
import com.joshuac.deduce.classifier.HardcodedClassifier;
import com.joshuac.deduce.injector.EntireInjector;
import com.joshuac.deduce.injector.Injector;
import com.joshuac.deduce.injector.concepts.ConceptsNode;
import com.joshuac.deduce.parser.Parser;
import com.joshuac.deduce.parser.SimpleParser;
import com.joshuac.deduce.parser.node.SentencesNode;
import com.joshuac.deduce.scanner.Scanner;
import com.joshuac.deduce.scanner.SimpleScanner;
import com.joshuac.deduce.scanner.Token;

public class EntireInjectorTest
{
    private static final int TOTAL_CONCEPTS = 1;
    private static final int CONCEPTS_NODE_INDEX = 0;
    private static final int SENTENCES_NODE_INDEX = 1;

    private Scanner scanner;
    private Parser parser;
    private Injector injector;

    @Before
    public void setup()
    {
        scanner = new SimpleScanner();
        parser = new SimpleParser(new HardcodedClassifier());
        injector = new EntireInjector();
    }

    @Test
    public void testAdditionOfConcepts()
    {
        String sentence = "The ball is red.";

        Node rootNode = parse(sentence);
        Node rooNode = inject(rootNode);

        assertTrue(rootNode.getNumberOfChildren() == 2);

        assertTrue(rootNode.getChild(CONCEPTS_NODE_INDEX) instanceof ConceptsNode);
        assertTrue(rootNode.getChild(SENTENCES_NODE_INDEX) instanceof SentencesNode);
        assertTrue(rootNode.getChild(CONCEPTS_NODE_INDEX).getNumberOfChildren() == TOTAL_CONCEPTS);

    }

    private Node parse(String sentence)
    {
        List<Token> tokens = scanner.tokenize(sentence);
        return parser.parse(tokens);
    }

    private Node inject(Node rootNode)
    {
        return injector.injectConceptsTo(rootNode);
    }
}

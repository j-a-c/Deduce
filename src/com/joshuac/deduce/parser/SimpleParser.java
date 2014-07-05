package com.joshuac.deduce.parser;

import java.util.List;

import com.google.common.base.Preconditions;
import com.joshuac.deduce.classifier.Classifier;
import com.joshuac.deduce.classifier.WordType;
import com.joshuac.deduce.parser.node.*;
import com.joshuac.deduce.scanner.Token;

public class SimpleParser implements Parser
{
    private final int SENTENCES_NODE_INDEX = 0;

    private Classifier classifier;

    public SimpleParser(final Classifier classifier)
    {
        Preconditions.checkNotNull(classifier);
        this.classifier = classifier;
    }

    @Override
    public Node parse(List<Token> tokens)
    {
        Node root = new RootNode();
        Node sentencesNode = new SentencesNode();

        root.insertChild(SENTENCES_NODE_INDEX, sentencesNode);

        while (!tokens.isEmpty())
        {
            tokens = parseSentence(tokens, sentencesNode);
        }

        return root;
    }

    private List<Token> parseSentence(List<Token> tokens, Node currentNode)
    {
        int newIndex = currentNode.getNumberOfChildren();
        Node sentenceNode = new SentenceNode();
        currentNode.insertChild(newIndex, sentenceNode);

        tokens = parseVerbPhrase(tokens, sentenceNode);
        tokens = parsePunctuation(tokens, sentenceNode);
        return tokens;
    }

    private List<Token> parseVerbPhrase(List<Token> tokens, Node currentNode)
    {
        Node verbPhraseNode = new VerbPhraseNode();
        currentNode.insertChild(0, verbPhraseNode);

        tokens = parseNounPhrase(tokens, verbPhraseNode);
        tokens = parseVerb(tokens, verbPhraseNode);
        tokens = parseAdjectivePhrase(tokens, verbPhraseNode);
        return tokens;
    }

    private List<Token> parseNounPhrase(List<Token> tokens, Node currentNode)
    {
        Node nounPhraseNode = new NounPhraseNode();
        currentNode.insertChild(currentNode.getNumberOfChildren(), nounPhraseNode);

        WordType currentWordType = classifier.classify(tokens.get(0));

        switch (currentWordType)
        {
        case Determiner:
            tokens.remove(0);
            tokens = parseNoun(tokens, nounPhraseNode);
            return tokens;
        case Noun:
            tokens = parseNoun(tokens, nounPhraseNode);
            return tokens;
        default:
            throw new RuntimeException("Cannot parse noun phrase.");
        }
    }

    private List<Token> parseAdjectivePhrase(List<Token> tokens, Node currentNode)
    {
        Node adjectivePhraseNode = new AdjectivePhraseNode();
        currentNode.insertChild(currentNode.getNumberOfChildren(), adjectivePhraseNode);

        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Adjective)
        {
            tokens = parseAdjective(tokens, adjectivePhraseNode);
            return tokens;
        }
        else
        {
            throw new RuntimeException("Expected word is not a adjective. : " + tokens.get(0));
        }
    }

    private List<Token> parseVerb(List<Token> tokens, Node currentNode)
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Verb)
        {
            Token verb = tokens.remove(0);
            Node verbNode = new VerbNode();
            Node verbTerminal = new TerminalNode(verb);
            verbNode.insertChild(0, verbTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), verbNode);
            return tokens;
        }
        else
        {
            throw new RuntimeException("Expected word is not a verb.");
        }
    }

    private List<Token> parsePunctuation(List<Token> tokens, Node currentNode)
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Period)
        {
            Token period = tokens.remove(0);
            Node punctuationNode = new PunctuationNode();
            Node periodTerminal = new TerminalNode(period);
            punctuationNode.insertChild(0, periodTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), punctuationNode);
            return tokens;
        }
        else
        {
            throw new RuntimeException("Expected word is not punctuation. " + tokens.get(0));
        }
    }

    private List<Token> parseNoun(List<Token> tokens, Node currentNode)
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Noun)
        {
            Token noun = tokens.remove(0);
            Node nounNode = new NounNode();
            Node nounTerminal = new TerminalNode(noun);
            nounNode.insertChild(0, nounTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), nounNode);
            return tokens;
        }
        else
        {
            throw new RuntimeException("Expected word is not a noun. " + tokens.get(0));
        }
    }

    private List<Token> parseAdjective(List<Token> tokens, Node currentNode)
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Adjective)
        {
            Token noun = tokens.remove(0);
            Node adjectiveNode = new AdjectiveNode();
            Node adjectiveTerminal = new TerminalNode(noun);
            adjectiveNode.insertChild(0, adjectiveTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), adjectiveTerminal);
            return tokens;
        }
        else
        {
            throw new RuntimeException("Expected word is not a noun. " + tokens.get(0));
        }
    }
}

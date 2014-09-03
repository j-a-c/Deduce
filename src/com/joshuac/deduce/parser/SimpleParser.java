package com.joshuac.deduce.parser;

import java.util.List;

import com.google.common.base.Preconditions;
import com.joshuac.deduce.Node;
import com.joshuac.deduce.classifier.Classifier;
import com.joshuac.deduce.classifier.WordType;
import com.joshuac.deduce.parser.node.*;
import com.joshuac.deduce.scanner.Token;

/**
 * Only modify the original token list and current parse tree AFTER a sentence
 * has been successfully parsed!
 * 
 * @author jac
 *
 */
public class SimpleParser implements Parser
{
    private static final char SPACE = ' ';
    private static final int SENTENCES_NODE_INDEX = 0;
    private static final int FIRST_INDEX = 0;

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
            try
            {
                tokens = parseSentence(tokens, sentencesNode);
            }
            catch (ParseException e)
            {
                StringBuilder sentence = new StringBuilder();
                while (!tokens.isEmpty() && !isEndingPunctuation(tokens.get(FIRST_INDEX)))
                {
                    sentence.append(tokens.get(FIRST_INDEX).data);
                    sentence.append(SPACE);
                    tokens.remove(FIRST_INDEX);
                }

                // Pop the final punctuation.
                if (!tokens.isEmpty())
                {
                    sentence.append(tokens.get(FIRST_INDEX).data);
                    tokens.remove(FIRST_INDEX);
                }
                System.err.println(e);
                System.err.println("Cannot parse sentence: " + sentence.toString());
            }
        }

        return root;
    }

    private List<Token> parseSentence(List<Token> tokens, Node currentNode) throws ParseException
    {
        try
        {
            int newIndex = currentNode.getNumberOfChildren();
            Node sentenceNode = new SentenceNode();

            List<Token> unparsedTokens = parseVerbPhrase(tokens, sentenceNode);
            unparsedTokens = parsePunctuation(unparsedTokens, sentenceNode);

            currentNode.insertChild(newIndex, sentenceNode);
            return unparsedTokens;
        }
        catch (ParseException ex)
        {
            throw ex;
        }
    }

    private List<Token> parseVerbPhrase(List<Token> tokens, Node currentNode) throws ParseException
    {
        try
        {
            Node verbPhraseNode = new VerbPhraseNode();

            List<Token> unparsedTokens = parseNounPhrase(tokens, verbPhraseNode);
            unparsedTokens = parseVerb(unparsedTokens, verbPhraseNode);
            unparsedTokens = parseAdjectivePhrase(unparsedTokens, verbPhraseNode);

            currentNode.insertChild(0, verbPhraseNode);
            return unparsedTokens;
        }
        catch (ParseException ex)
        {
            throw ex;
        }
    }

    private List<Token> parseNounPhrase(List<Token> tokens, Node currentNode) throws ParseException
    {
        try
        {
            Node nounPhraseNode = new NounPhraseNode();

            WordType currentWordType = classifier.classify(tokens.get(0));
            List<Token> unparsedTokens;

            switch (currentWordType)
            {
            case Determiner:
                unparsedTokens = tokens.subList(1, tokens.size());
                unparsedTokens = parseNoun(unparsedTokens, nounPhraseNode);
                break;
            case Noun:
                unparsedTokens = parseNoun(tokens, nounPhraseNode);
                break;
            default:
                throw new ParseException(tokens.get(0), currentWordType);
            }
            currentNode.insertChild(currentNode.getNumberOfChildren(), nounPhraseNode);
            return unparsedTokens;
        }
        catch (ParseException ex)
        {
            throw ex;
        }
    }

    private List<Token> parseAdjectivePhrase(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        Node adjectivePhraseNode = new AdjectivePhraseNode();

        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Adjective)
        {
            List<Token> unparsedTokens = parseAdjective(tokens, adjectivePhraseNode);

            currentNode.insertChild(currentNode.getNumberOfChildren(), adjectivePhraseNode);
            return unparsedTokens;
        }
        else
        {
            throw new ParseException(WordType.Adjective, tokens.get(0), currentWordType);
        }
    }

    private List<Token> parseVerb(List<Token> tokens, Node currentNode) throws ParseException
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Verb)
        {
            Token verb = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());
            Node verbNode = new VerbNode();
            Node verbTerminal = new TerminalNode(verb);
            verbNode.insertChild(0, verbTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), verbNode);
            return unparsedTokens;
        }
        else
        {
            throw new ParseException(WordType.Verb, tokens.get(0), currentWordType);
        }
    }

    private List<Token> parsePunctuation(List<Token> tokens, Node currentNode)
            throws ParseException
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
            throw new ParseException(WordType.Period, tokens.get(0), currentWordType);
        }
    }

    private List<Token> parseNoun(List<Token> tokens, Node currentNode) throws ParseException
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Noun)
        {
            Token noun = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());
            Node nounNode = new NounNode();
            Node nounTerminal = new TerminalNode(noun);
            nounNode.insertChild(0, nounTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), nounNode);
            return unparsedTokens;
        }
        else
        {
            throw new ParseException(WordType.Noun, tokens.get(0), currentWordType);
        }
    }

    private List<Token> parseAdjective(List<Token> tokens, Node currentNode) throws ParseException
    {
        WordType currentWordType = classifier.classify(tokens.get(0));
        if (currentWordType == WordType.Adjective)
        {
            Token noun = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());
            Node adjectiveNode = new AdjectiveNode();
            Node adjectiveTerminal = new TerminalNode(noun);
            adjectiveNode.insertChild(0, adjectiveTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), adjectiveNode);
            return unparsedTokens;
        }
        else
        {
            throw new ParseException(WordType.Adjective, tokens.get(0), currentWordType);
        }
    }

    private boolean isEndingPunctuation(Token token)
    {
        WordType currentWordType = classifier.classify(token);

        switch (currentWordType)
        {
        case Period:
            return true;
        default:
            return false;
        }

    }
}

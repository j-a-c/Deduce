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
            // Remove sentence that could not be parsed.
            catch (ParseException e)
            {
                StringBuilder sentence = new StringBuilder();
                while (!tokens.isEmpty()
                        && !classifier.isEndingPunctuation(tokens.get(FIRST_INDEX)))
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

            List<Token> unparsedTokens = parseClause(tokens, sentenceNode);
            unparsedTokens = parseEndingPunctuation(unparsedTokens, sentenceNode);

            currentNode.insertChild(newIndex, sentenceNode);
            return unparsedTokens;
        }
        catch (ParseException ex)
        {
            throw ex;
        }
    }

    private List<Token> parseClause(List<Token> tokens, Node currentNode) throws ParseException
    {
        try
        {
            Node clauseNode = new ClauseNode();

            List<Token> unparsedTokens = parseNounPhrase(tokens, clauseNode);
            unparsedTokens = parseVerbPhrase(unparsedTokens, clauseNode);

            currentNode.insertChild(0, clauseNode);
            return unparsedTokens;
        }
        catch (ParseException ex)
        {
            throw ex;
        }
    }

    private List<Token> parseNounPhrase(List<Token> tokens, Node currentNode) throws ParseException
    {

        Node nounPhraseNode = new NounPhraseNode();

        Token currentToken = tokens.get(0);
        List<Token> unparsedTokens;

        if (classifier.isArticle(currentToken))
        {
            try
            {
                unparsedTokens = tokens.subList(1, tokens.size());
                unparsedTokens = parseNoun(unparsedTokens, nounPhraseNode);

                DeterminerNode determinerNode = new DeterminerNode();
                ArticleNode articleNode = new ArticleNode();
                articleNode.insertChild(0, new TerminalNode(currentToken));
                determinerNode.insertChild(0, articleNode);

                nounPhraseNode.insertChild(0, determinerNode);
                currentNode.insertChild(currentNode.getNumberOfChildren(), nounPhraseNode);
                return unparsedTokens;
            }
            catch (ParseException parseException)
            {
                // Try next case.
            }
        }

        // TODO Add the rest of determiners..

        if (classifier.isNoun(currentToken))
        {
            try
            {
                unparsedTokens = parseNoun(tokens, nounPhraseNode);
                currentNode.insertChild(currentNode.getNumberOfChildren(), nounPhraseNode);
                return unparsedTokens;
            }
            catch (ParseException parseException)
            {
                // Try next case.
            }
        }

        throw new ParseException(tokens.get(0), WordType.UNKNOWN);

    }

    private List<Token> parseVerbPhrase(List<Token> tokens, Node currentNode) throws ParseException
    {
        Node verbPhraseNode = new VerbPhraseNode();

        Token currentToken = tokens.get(0);
        if (classifier.isIntransitiveVerb(currentToken))
        {
            try
            {
                List<Token> unparsedTokens = parseVerb(tokens, verbPhraseNode);
                currentNode.insertChild(currentNode.getNumberOfChildren(), verbPhraseNode);
                return unparsedTokens;
            }
            catch (ParseException parseException)
            {
                // Try next case.
            }
        }

        if (classifier.isLinkingVerb(currentToken))
        {
            try
            {
                List<Token> unparsedTokens = parseVerb(tokens, verbPhraseNode);
                unparsedTokens = parseAdjectivePhrase(unparsedTokens, verbPhraseNode);
                currentNode.insertChild(currentNode.getNumberOfChildren(), verbPhraseNode);
                return unparsedTokens;
            }
            catch (ParseException parseException)
            {
                // Try next case.
            }
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);

    }

    private List<Token> parseAdjectivePhrase(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        Node adjectivePhraseNode = new AdjectivePhraseNode();

        Token currentToken = tokens.get(0);
        if (classifier.isAdjective(currentToken))
        {
            try
            {
                List<Token> unparsedTokens = parseAdjective(tokens, adjectivePhraseNode);
                currentNode.insertChild(currentNode.getNumberOfChildren(), adjectivePhraseNode);
                return unparsedTokens;
            }
            catch (ParseException parseException)
            {
                // Try next case.
            }
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);

    }

    private List<Token> parseVerb(List<Token> tokens, Node currentNode) throws ParseException
    {
        Token currentToken = tokens.get(0);
        if (classifier.isVerb(currentToken))
        {

            Token verb = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());
            Node verbNode = new VerbNode();
            Node verbTerminal = new TerminalNode(verb);
            verbNode.insertChild(0, verbTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), verbNode);
            return unparsedTokens;

        }

        throw new ParseException(currentToken, WordType.UNKNOWN);

    }

    private List<Token> parseEndingPunctuation(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        Token currentToken = tokens.get(0);
        if (classifier.isEndingPunctuation(currentToken))
        {
            Token period = tokens.remove(0);
            Node endingPunctuationNode = new EndingPunctuationNode();
            Node periodTerminal = new TerminalNode(period);
            endingPunctuationNode.insertChild(0, periodTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), endingPunctuationNode);
            return tokens;
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);

    }

    private List<Token> parseNoun(List<Token> tokens, Node currentNode) throws ParseException
    {
        Token currentToken = tokens.get(0);
        if (classifier.isNoun(currentToken))
        {
            Token noun = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());
            Node nounNode = new NounNode();
            Node nounTerminal = new TerminalNode(noun);
            nounNode.insertChild(0, nounTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), nounNode);
            return unparsedTokens;
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);

    }

    private List<Token> parseAdjective(List<Token> tokens, Node currentNode) throws ParseException
    {
        Token currentToken = tokens.get(0);
        if (classifier.isAdjective(currentToken))
        {
            Token noun = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());
            Node adjectiveNode = new AdjectiveNode();
            Node adjectiveTerminal = new TerminalNode(noun);
            adjectiveNode.insertChild(0, adjectiveTerminal);
            currentNode.insertChild(currentNode.getNumberOfChildren(), adjectiveNode);
            return unparsedTokens;
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);

    }

}

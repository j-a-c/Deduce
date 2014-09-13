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
        // Sentence = Clause EndingPunctuation
        try
        {
            Node sentenceNode = new SentenceNode();

            List<Token> unparsedTokens = parseClause(tokens, sentenceNode);
            unparsedTokens = parseEndingPunctuation(unparsedTokens, sentenceNode);

            currentNode.insertChild(currentNode.getNumberOfChildren(), sentenceNode);
            return unparsedTokens;
        }
        catch (ParseException parseException)
        {
            // Try next case.
        }

        // Sentence = Clause SubordinateClause EndingPunctuation
        try
        {
            Node sentenceNode = new SentenceNode();

            List<Token> unparsedTokens = parseClause(tokens, sentenceNode);
            unparsedTokens = parseSubordinateClause(unparsedTokens, sentenceNode);
            unparsedTokens = parseEndingPunctuation(unparsedTokens, sentenceNode);

            currentNode.insertChild(currentNode.getNumberOfChildren(), sentenceNode);
            return unparsedTokens;
        }
        catch (ParseException parseException)
        {

        }

        // Sentence = SubordinateClause ~Comma Clause EndingPunctuation
        try
        {
            Node sentenceNode = new SentenceNode();

            List<Token> unparsedTokens = parseSubordinateClause(tokens, sentenceNode);
            unparsedTokens = parseComma(unparsedTokens, sentenceNode);

            unparsedTokens = parseClause(unparsedTokens, sentenceNode);
            unparsedTokens = parseEndingPunctuation(unparsedTokens, sentenceNode);

            currentNode.insertChild(currentNode.getNumberOfChildren(), sentenceNode);
            return unparsedTokens;
        }
        catch (ParseException parseException)
        {

        }

        throw new ParseException(tokens.get(0), WordType.UNKNOWN);
    }

    private List<Token> parseClause(List<Token> tokens, Node currentNode) throws ParseException
    {
        try
        {
            Node clauseNode = new ClauseNode();

            List<Token> unparsedTokens = parseNounPhrase(tokens, clauseNode);
            unparsedTokens = parseVerbPhrase(unparsedTokens, clauseNode);

            currentNode.insertChild(currentNode.getNumberOfChildren(), clauseNode);
            return unparsedTokens;
        }
        catch (ParseException parseException)
        {
            throw parseException;
        }
    }

    private List<Token> parseSubordinateClause(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        try
        {
            Node subordinateClauseNode = new SubordinateClauseNode();

            List<Token> unparsedTokens = parseSubordinateConjuction(tokens, subordinateClauseNode);
            unparsedTokens = parseNounPhrase(unparsedTokens, subordinateClauseNode);
            unparsedTokens = parseVerbPhrase(unparsedTokens, subordinateClauseNode);

            currentNode.insertChild(currentNode.getNumberOfChildren(), subordinateClauseNode);
            return unparsedTokens;
        }
        catch (ParseException parseException)
        {
            throw parseException;
        }

    }

    private List<Token> parseSubordinateConjuction(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        Token currentToken = tokens.get(0);

        if (classifier.isSubordinateConjunction(currentToken))
        {
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());

            Node subordinateConjunctionNode = new SubordinateConjunctionNode();
            Node subordinateConjunctionTerminal = new TerminalNode(currentToken);
            subordinateConjunctionNode.insertChild(0, subordinateConjunctionTerminal);

            currentNode.insertChild(currentNode.getNumberOfChildren(), subordinateConjunctionNode);
            return unparsedTokens;
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);
    }

    private List<Token> parseNounPhrase(List<Token> tokens, Node currentNode) throws ParseException
    {
        Token currentToken = tokens.get(0);
        List<Token> unparsedTokens;

        if (classifier.isDeterminer(currentToken))
        {
            try
            {
                Node nounPhraseNode = new NounPhraseNode();

                DeterminerNode determinerNode = new DeterminerNode();

                // TODO Add the rest of determiners..
                if (classifier.isArticle(currentToken))
                {
                    ArticleNode articleNode = new ArticleNode();
                    articleNode.insertChild(0, new TerminalNode(currentToken));
                    determinerNode.insertChild(0, articleNode);
                }

                nounPhraseNode.insertChild(0, determinerNode);

                unparsedTokens = tokens.subList(1, tokens.size());
                unparsedTokens = parseNoun(unparsedTokens, nounPhraseNode);

                // PrepositionalPhrase?
                try
                {
                    unparsedTokens = parsePrepositionalPhrase(unparsedTokens, nounPhraseNode);
                }
                catch (ParseException ignoreException)
                {

                }

                // Appositive?
                try
                {
                    unparsedTokens = parseAppositive(unparsedTokens, nounPhraseNode);
                }
                catch (ParseException ignoreException)
                {

                }

                currentNode.insertChild(currentNode.getNumberOfChildren(), nounPhraseNode);
                return unparsedTokens;
            }
            catch (ParseException parseException)
            {
                // Try next case.
            }
        }

        if (classifier.isNoun(currentToken))
        {
            try
            {
                Node nounPhraseNode = new NounPhraseNode();

                unparsedTokens = parseNoun(tokens, nounPhraseNode);

                // PrepositionalPhrase?
                try
                {
                    unparsedTokens = parsePrepositionalPhrase(unparsedTokens, currentNode);
                }
                catch (ParseException ignoreException)
                {

                }

                // Appositive?
                try
                {
                    unparsedTokens = parseAppositive(unparsedTokens, currentNode);
                }
                catch (ParseException ignoreException)
                {

                }

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

    private List<Token> parsePrepositionalPhrase(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        Node prepositionalPhraseNode = new PrepositionalPhraseNode();
        List<Token> unparsedTokens = parsePreposition(tokens, prepositionalPhraseNode);

        // Modifier?
        try
        {
            unparsedTokens = parseModifier(unparsedTokens, prepositionalPhraseNode);
        }
        catch (ParseException parseException)
        {
            // Ignore, no Modifier.
        }

        unparsedTokens = parseNounPhrase(unparsedTokens, prepositionalPhraseNode);

        currentNode.insertChild(currentNode.getNumberOfChildren(), prepositionalPhraseNode);
        return unparsedTokens;
    }

    private List<Token> parseModifier(List<Token> tokens, Node currentNode) throws ParseException
    {
        Node modifierPhraseNode = new ModifierPhraseNode();

        List<Token> unparsedTokens = parsePrepositionalPhrase(tokens, modifierPhraseNode);

        currentNode.insertChild(currentNode.getNumberOfChildren(), modifierPhraseNode);
        return unparsedTokens;
    }

    private List<Token> parsePreposition(List<Token> tokens, Node currentNode)
            throws ParseException
    {
        Token currentToken = tokens.get(0);
        if (classifier.isPreposition(currentToken))
        {
            Token noun = tokens.get(0);
            List<Token> unparsedTokens = tokens.subList(1, tokens.size());

            Node prepositionNode = new PrepositionNode();
            Node prepositionTerminal = new TerminalNode(noun);
            prepositionNode.insertChild(0, prepositionTerminal);

            currentNode.insertChild(currentNode.getNumberOfChildren(), prepositionNode);
            return unparsedTokens;
        }

        throw new ParseException(currentToken, WordType.UNKNOWN);
    }

    private List<Token> parseAppositive(List<Token> tokens, Node currentNode) throws ParseException
    {
        Node appositivePhrase = new AppositivePhraseNode();

        Token currentToken = tokens.get(0);

        if (classifier.isComma(currentToken))
        {
            List<Token> unparsedTokens = parseComma(tokens, appositivePhrase);
            unparsedTokens = parseNounPhrase(unparsedTokens, appositivePhrase);

            if (classifier.isEndingPunctuation(unparsedTokens.get(0)))
            {
                currentNode.insertChild(currentNode.getNumberOfChildren(), appositivePhrase);
                return unparsedTokens;
            }
            else if (classifier.isComma(unparsedTokens.get(0)))
            {
                currentNode.insertChild(currentNode.getNumberOfChildren(), appositivePhrase);
                unparsedTokens = parseComma(unparsedTokens, appositivePhrase);
                return unparsedTokens;
            }

        }

        throw new ParseException(currentToken, WordType.UNKNOWN);
    }

    private List<Token> parseVerbPhrase(List<Token> tokens, Node currentNode) throws ParseException
    {

        Token currentToken = tokens.get(0);
        if (classifier.isIntransitiveVerb(currentToken))
        {
            try
            {
                Node verbPhraseNode = new VerbPhraseNode();

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
                Node verbPhraseNode = new VerbPhraseNode();

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

        if (classifier.isTransitiveVerb(currentToken))
        {
            try
            {
                Node verbPhraseNode = new VerbPhraseNode();

                List<Token> unparsedTokens = parseVerb(tokens, verbPhraseNode);
                unparsedTokens = parseNounPhrase(unparsedTokens, verbPhraseNode);

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
        Token currentToken = tokens.get(0);
        if (classifier.isAdjective(currentToken))
        {
            try
            {
                Node adjectivePhraseNode = new AdjectivePhraseNode();

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

    private List<Token> parseComma(List<Token> tokens, Node currentNode) throws ParseException
    {
        Token currentToken = tokens.get(0);
        if (classifier.isComma(currentToken))
        {
            return tokens.subList(1, tokens.size());
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

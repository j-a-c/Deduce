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

        // [The ball]
        Node nounPhraseNode = clauseNode.getChild(0);
        assertTrue(nounPhraseNode.getNumberOfChildren() == 2);
        assertTrue(nounPhraseNode.getChild(0) instanceof DeterminerNode);
        assertTrue(nounPhraseNode.getChild(1) instanceof NounNode);
        // TODO Break down

        // [is red]
        Node verbPhraseNode = clauseNode.getChild(1);
        assertTrue(verbPhraseNode.getNumberOfChildren() == 2);
        assertTrue(verbPhraseNode.getChild(0) instanceof VerbNode);
        assertTrue(verbPhraseNode.getChild(1) instanceof AdjectivePhraseNode);
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

        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode.getNumberOfChildren() == 2);
        assertTrue(sentenceNode.getChild(0) instanceof ClauseNode);
        assertTrue(sentenceNode.getChild(1) instanceof EndingPunctuationNode);

        // [The cars race]
        Node clauseNode = sentenceNode.getChild(0);
        assertTrue(clauseNode.getNumberOfChildren() == 2);
        assertTrue(clauseNode.getChild(0) instanceof NounPhraseNode);
        assertTrue(clauseNode.getChild(1) instanceof VerbPhraseNode);
        // TODO Break down

        // [.]
        Node endingPunctuationNode = sentenceNode.getChild(1);
        assertTrue(endingPunctuationNode.getNumberOfChildren() == 1);
        assertTrue(endingPunctuationNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(endingPunctuationNode.getChild(0), "."));
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

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        // [The horse, the smallest horse in the pasture, ate the apple][.]
        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode.getNumberOfChildren() == 2);
        assertTrue(sentenceNode.getChild(0) instanceof ClauseNode);
        assertTrue(sentenceNode.getChild(1) instanceof EndingPunctuationNode);

        // [The horse, the smallest horse in the pasture, ate the apple]
        Node clauseNode = sentenceNode.getChild(0);
        assertTrue(clauseNode.getNumberOfChildren() == 2);
        assertTrue(clauseNode.getChild(0) instanceof NounPhraseNode);
        assertTrue(clauseNode.getChild(1) instanceof VerbPhraseNode);

        // [The][horse][, the smallest horse in the pasture,]
        Node nounPhraseNode = clauseNode.getChild(0);
        assertTrue(nounPhraseNode.getNumberOfChildren() == 3);
        assertTrue(nounPhraseNode.getChild(0) instanceof DeterminerNode);
        assertTrue(nounPhraseNode.getChild(1) instanceof NounNode);
        assertTrue(nounPhraseNode.getChild(2) instanceof AppositivePhraseNode);

        // [The]
        Node determinerNode = nounPhraseNode.getChild(0);
        assertTrue(determinerNode.getNumberOfChildren() == 1);
        assertTrue(determinerNode.getChild(0) instanceof ArticleNode);
        Node articleNode = determinerNode.getChild(0);
        assertTrue(articleNode.getNumberOfChildren() == 1);
        assertTrue(articleNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(articleNode.getChild(0), "The"));

        // [horse]
        Node nounNode = nounPhraseNode.getChild(1);
        assertTrue(nounNode.getNumberOfChildren() == 1);
        assertTrue(nounNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(nounNode.getChild(0), "horse"));

        // [the smallest horse in the pasture]
        Node appositivePhraseNode = nounPhraseNode.getChild(2);
        assertTrue(appositivePhraseNode.getNumberOfChildren() == 1);
        assertTrue(appositivePhraseNode.getChild(0) instanceof NounPhraseNode);

        // [the] [smallest] [horse] [in the pasture]
        Node nounPhraseNode2 = appositivePhraseNode.getChild(0);
        assertTrue(nounPhraseNode2.getNumberOfChildren() == 4);
        assertTrue(nounPhraseNode2.getChild(0) instanceof DeterminerNode);
        // TODO Break down
        assertTrue(nounPhraseNode2.getChild(1) instanceof AdjectivePhraseNode);
        assertTrue(nounPhraseNode2.getChild(2) instanceof NounNode);
        assertTrue(nounPhraseNode2.getChild(3) instanceof PrepositionalPhraseNode);

        // [ate] [the apple]
        Node verbPhrase = clauseNode.getChild(1);
        assertTrue(verbPhrase.getNumberOfChildren() == 2);
        assertTrue(verbPhrase.getChild(0) instanceof VerbNode);
        assertTrue(verbPhrase.getChild(1) instanceof NounPhraseNode);

        // [ate]
        Node verbNode = verbPhrase.getChild(0);
        assertTrue(verbNode.getNumberOfChildren() == 1);
        assertTrue(verbNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(verbNode.getChild(0), "ate"));

        // [the apple]
        Node nounPhraseNode1 = verbPhrase.getChild(1);
        assertTrue(nounPhraseNode1.getNumberOfChildren() == 2);
        assertTrue(nounPhraseNode1.getChild(0) instanceof DeterminerNode);
        assertTrue(nounPhraseNode1.getChild(1) instanceof NounNode);

        // [the]
        Node determinerNode1 = nounPhraseNode1.getChild(0);
        assertTrue(determinerNode1.getNumberOfChildren() == 1);
        assertTrue(determinerNode1.getChild(0) instanceof ArticleNode);
        Node articleNode1 = determinerNode1.getChild(0);
        assertTrue(articleNode1.getNumberOfChildren() == 1);
        assertTrue(articleNode1.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(articleNode1.getChild(0), "the"));

        // [apple]
        nounNode = nounPhraseNode1.getChild(1);
        assertTrue(nounNode.getNumberOfChildren() == 1);
        assertTrue(nounNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(nounNode.getChild(0), "apple"));

        // [.]
        Node endingPunctuationNode = sentenceNode.getChild(1);
        assertTrue(endingPunctuationNode.getNumberOfChildren() == 1);
        assertTrue(endingPunctuationNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(endingPunctuationNode.getChild(0), "."));
    }

    @Test
    public void appositiveEnd()
    {
        // TODO Implement test
        String sentence = "The horse ate the apple, a small fruit.";
        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);

        // [The horse ate the apple, a small fruit][.]
        Node sentenceNode = sentencesNode.getChild(0);
        assertTrue(sentenceNode.getNumberOfChildren() == 2);
        assertTrue(sentenceNode.getChild(0) instanceof ClauseNode);
        assertTrue(sentenceNode.getChild(1) instanceof EndingPunctuationNode);

        // [The horse ate the apple, a small fruit]
        Node clauseNode = sentenceNode.getChild(0);
        assertTrue(clauseNode.getNumberOfChildren() == 2);
        assertTrue(clauseNode.getChild(0) instanceof NounPhraseNode);
        assertTrue(clauseNode.getChild(1) instanceof VerbPhraseNode);

        // [The horse]
        // TODO Break down

        // [ate the apple, a small fruit]
        Node verbNode = clauseNode.getChild(1);
        assertTrue(verbNode.getNumberOfChildren() == 2);
        assertTrue(verbNode.getChild(0) instanceof VerbNode);
        assertTrue(verbNode.getChild(1) instanceof NounPhraseNode);

        // [ate]
        // TODO Break down

        // [the][apple][, a small fruit]
        Node nounPhraseNode = verbNode.getChild(1);
        assertTrue(nounPhraseNode.getNumberOfChildren() == 3);
        // TODO Break down
        assertTrue(nounPhraseNode.getChild(0) instanceof DeterminerNode);
        assertTrue(nounPhraseNode.getChild(1) instanceof NounNode);
        assertTrue(nounPhraseNode.getChild(2) instanceof AppositivePhraseNode);

        // [.]
        Node endingPunctuationNode = sentenceNode.getChild(1);
        assertTrue(endingPunctuationNode.getNumberOfChildren() == 1);
        assertTrue(endingPunctuationNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(endingPunctuationNode.getChild(0), "."));
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
        assertTrue(nounPhraseNode.getChild(0) instanceof DeterminerNode);
        assertTrue(nounPhraseNode.getChild(1) instanceof NounNode);
        assertTrue(nounPhraseNode.getChild(2) instanceof PrepositionalPhraseNode);

        // TODO [The]

        // TODO [book]

        // [on] [the floor]
        Node prepositionalPhraseNode = nounPhraseNode.getChild(2);
        assertTrue(prepositionalPhraseNode.getNumberOfChildren() == 2);
        assertTrue(prepositionalPhraseNode.getChild(0) instanceof PrepositionNode);
        assertTrue(prepositionalPhraseNode.getChild(1) instanceof NounPhraseNode);

        // [the floor]
        nounPhraseNode = prepositionalPhraseNode.getChild(1);
        assertTrue(nounPhraseNode.getNumberOfChildren() == 2);
        assertTrue(nounPhraseNode.getChild(0) instanceof DeterminerNode);
        assertTrue(nounPhraseNode.getChild(1) instanceof NounNode);

        // [the]
        Node determinerNode = nounPhraseNode.getChild(0);
        assertTrue(determinerNode.getNumberOfChildren() == 1);
        assertTrue(determinerNode.getChild(0) instanceof ArticleNode);
        Node articleNode = determinerNode.getChild(0);
        assertTrue(articleNode.getNumberOfChildren() == 1);
        assertTrue(articleNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(articleNode.getChild(0), "the"));

        // [floor]
        Node nounNode = nounPhraseNode.getChild(1);
        assertTrue(nounNode.getNumberOfChildren() == 1);
        assertTrue(nounNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(nounNode.getChild(0), "floor"));

        // [is wet]
        Node verbPhraseNode = clauseNode.getChild(1);
        assertTrue(verbPhraseNode instanceof VerbPhraseNode);
        assertTrue(verbPhraseNode.getNumberOfChildren() == 2);
        assertTrue(verbPhraseNode.getChild(0) instanceof VerbNode);
        assertTrue(verbPhraseNode.getChild(1) instanceof AdjectivePhraseNode);

        // [is]
        Node verbNode = verbPhraseNode.getChild(0);
        assertTrue(verbNode.getNumberOfChildren() == 1);
        assertTrue(verbNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(verbNode.getChild(0), "is"));

        // [wet]
        Node adjectivePhraseNode = verbPhraseNode.getChild(1);
        assertTrue(adjectivePhraseNode.getNumberOfChildren() == 1);
        assertTrue(adjectivePhraseNode.getChild(0) instanceof AdjectiveNode);
        Node adjectiveNode = adjectivePhraseNode.getChild(0);
        assertTrue(adjectiveNode.getNumberOfChildren() == 1);
        assertTrue(adjectiveNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(adjectiveNode.getChild(0), "wet"));

        // [.]
        assertTrue(sentenceNode.getChild(1) instanceof EndingPunctuationNode);
        Node endingPunctuationNode = sentenceNode.getChild(1);
        assertTrue(endingPunctuationNode.getNumberOfChildren() == 1);
        assertTrue(endingPunctuationNode.getChild(0) instanceof TerminalNode);
        assertTrue(checkTerminalData(endingPunctuationNode.getChild(0), "."));

    }

    @Test
    public void longSentence1()
    {
        String sentence = "Poor Stephen, who just wanted a quick meal to get "
                + "through his three-hour biology lab, quickly dropped his fork"
                + " on the cafeteria tray, gagging with disgust as a tarantula"
                + " wiggled out of his cheese omelet, a sight requiring a year"
                + " of therapy before Stephen could eat eggs again.";
        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);
        // TODO Break down
    }

    @Test
    public void doubleObjectTest()
    {
        String sentence = "He gave the dog a bone.";
        Node rootNode = parse(sentence);

        assertTrue(rootNode instanceof RootNode);
        assertTrue(rootNode.getNumberOfChildren() == 1);

        Node sentencesNode = rootNode.getChild(0);
        assertTrue(sentencesNode instanceof SentencesNode);
        assertTrue(sentencesNode.getNumberOfChildren() == 1);
        // TODO Break down
    }

    private Node parse(String sentence)
    {
        List<Token> tokens = scanner.tokenize(sentence);
        Node rootNode = parser.parse(tokens);
        return rootNode;
    }

    private boolean checkTerminalData(Node node, String data)
    {
        return (((TerminalNode) node).data.equals(data));
    }
}

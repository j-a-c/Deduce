package com.joshuac.deduce;

import java.util.List;

import util.ParseTreePrinter;

import com.joshuac.deduce.classifier.HardcodedClassifier;
import com.joshuac.deduce.parser.Parser;
import com.joshuac.deduce.parser.SimpleParser;
import com.joshuac.deduce.parser.node.Node;
import com.joshuac.deduce.scanner.Scanner;
import com.joshuac.deduce.scanner.SimpleScanner;
import com.joshuac.deduce.scanner.Token;

public class CompleteIntegration
{

    public static void main(String[] args)
    {
        String sentences = "The ball is red. Red is a color. The ball will never change color.";

        Scanner scanner = new SimpleScanner();
        Parser parser = new SimpleParser(new HardcodedClassifier());

        List<Token> tokens = scanner.tokenize(sentences);
        Node rootNode = parser.parse(tokens);

        ParseTreePrinter.printParseTree(rootNode);

        System.out.println("Done.");
    }
}

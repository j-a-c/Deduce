package com.joshuac.deduce.parser;

import java.util.List;

import com.joshuac.deduce.parser.node.Node;
import com.joshuac.deduce.scanner.Token;

public interface Parser
{
    public Node parse(List<Token> tokens);
}

package com.joshuac.deduce.parser.node;

import com.joshuac.deduce.scanner.Token;

public class TerminalNode extends Node
{
    String data;

    public TerminalNode(final Token token)
    {
        this.data = token.data;
    }

    @Override
    public String toString()
    {
        return super.toString() + " : " + this.data;
    }
}

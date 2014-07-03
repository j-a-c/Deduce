package com.joshuac.deduce.scanner;

public class Token
{
    public String data;

    public Token(String data)
    {
        this.data = data;
    }

    public Token(char data)
    {
        this(String.valueOf(data));
    }
}

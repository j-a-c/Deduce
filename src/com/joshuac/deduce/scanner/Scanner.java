package com.joshuac.deduce.scanner;

import java.util.List;

public interface Scanner
{
    public List<Token> tokenize(String data);
}

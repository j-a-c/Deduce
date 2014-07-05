package util;

import com.joshuac.deduce.parser.node.Node;

public class ParseTreePrinter
{
    public static void printParseTree(Node root)
    {
        print(root, 0);
    }

    private static void print(Node node, int tabs)
    {
        for (int i = 0; i < tabs; i++)
        {
            System.out.print('\t');
        }
        System.out.println(node);
        for (Node child : node.getChildren())
        {
            print(child, tabs + 1);
        }
    }
}

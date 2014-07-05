package com.joshuac.deduce.parser.node;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public abstract class Node
{
    List<Node> children = new ArrayList<Node>();

    public List<Node> getChildren()
    {
        return children;
    }

    public int getNumberOfChildren()
    {
        return children.size();
    }

    public void insertChild(int index, Node child)
    {
        children.add(index, child);
    }

    public Node getChild(int index)
    {
        Preconditions.checkArgument(index >= 0, "Child index must be > 0.");
        Preconditions.checkArgument(index < children.size(), "Child index is too big.");

        return children.get(index);
    }

    public Node getLastChild()
    {
        return children.get(getNumberOfChildren() - 1);
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName();
    }
}

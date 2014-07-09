package com.joshuac.deduce.injector;

import java.util.List;

import com.joshuac.deduce.Node;
import com.joshuac.deduce.injector.concepts.ConceptNode;
import com.joshuac.deduce.injector.concepts.ConceptsNode;
import com.joshuac.deduce.injector.concepts.time.ConceptTime;

public class EntireInjector implements Injector
{
    private static final int FIRST_INDEX = 0;

    @Override
    public Node injectConceptsTo(Node rootNode)
    {
        ConceptsNode conceptsNode = new ConceptsNode();

        addTimeConceptsTo(conceptsNode);

        rootNode.insertChild(0, conceptsNode);

        return rootNode;
    }

    private void addTimeConceptsTo(Node conceptsNode)
    {
        ConceptTime conceptNow = new ConceptTime();
        List<ConceptNode> nowConcepts = conceptNow.getNodesToAdd();
        conceptsNode = addAllConceptsTo(conceptsNode, nowConcepts);

    }

    private Node addAllConceptsTo(Node conceptsNode, List<ConceptNode> concepts)
    {
        for (Node concept : concepts)
        {
            conceptsNode.insertChild(FIRST_INDEX, concept);
        }
        return conceptsNode;
    }
}

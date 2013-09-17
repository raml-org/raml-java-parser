package org.raml.parser.builder;


import java.util.ArrayList;
import java.util.List;

import org.raml.parser.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class TemplateBuilder extends SequenceTupleBuilder
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public TemplateBuilder(String fieldName)
    {
        super(fieldName, String.class);
    }

    @Override
    public Object buildValue(Object parent, SequenceNode sequenceNode)
    {
        List<?> list = new ArrayList();
        ReflectionUtils.setProperty(parent, getFieldName(), list);
        int initialSize = sequenceNode.getValue().size();
        for (int i = 0; i < initialSize; i++)
        {
            MappingNode mapping = (MappingNode) sequenceNode.getValue().remove(0);
            for (NodeTuple tuple : mapping.getValue())
            {
                sequenceNode.getValue().add(tuple.getKeyNode());
            }
        }
        return list;
    }

    @Override
    public NodeBuilder getItemBuilder()
    {
        return super.getItemBuilder();
    }

}

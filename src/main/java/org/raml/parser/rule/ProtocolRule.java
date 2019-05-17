package org.raml.parser.rule;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class ProtocolRule extends DefaultTupleRule<Node, Node> implements SequenceRule {

    protected Map<String, TupleRule<?, ?>> rules = new HashMap<String, TupleRule<?, ?>>();

    @Override
    public TupleHandler getHandler() {
        return new ProtocolTupleHandler();
    }

    protected boolean isValidValueNodeType(Class valueNodeClass)
    {
        for (Class<?> clazz : getValueNodeType())
        {
            if (clazz.isAssignableFrom(valueNodeClass))
            {
                return true;
            }
        }
        return false;
    }

    public Class<?>[] getValueNodeType()
    {
        return new Class[] {ScalarNode.class, SequenceNode.class};
    }

    @Override
    public NodeRule<?> getItemRule() {
        return this;
    }
}

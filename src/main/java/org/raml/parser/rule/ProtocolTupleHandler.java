package org.raml.parser.rule;

import org.raml.model.Protocol;
import org.raml.parser.completion.DefaultSuggestion;
import org.raml.parser.completion.Suggestion;
import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ProtocolTupleHandler implements TupleHandler {
    @Override
    public boolean handles(NodeTuple tuple) {
        return tuple.getKeyNode() instanceof ScalarNode && ((ScalarNode) tuple.getKeyNode()).getValue().equals("protocols");
    }

    @Override
    public List<Suggestion> getSuggestions() {

        ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
        for (Protocol value : Protocol.values()) {
            suggestions.add(new DefaultSuggestion(value.name().toLowerCase()));
        }
        return suggestions;
    }
}

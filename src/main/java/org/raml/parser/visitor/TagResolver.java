package org.raml.parser.visitor;

import org.raml.parser.loader.ResourceLoader;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public interface TagResolver
{

    boolean handles(Tag tag);

    Node resolve(Node valueNode, ResourceLoader resourceLoader, NodeHandler nodeHandler);

}

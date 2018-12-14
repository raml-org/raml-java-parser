package org.raml.v2.internal.impl.v10.nodes.factory;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNodeFragment;
import org.raml.v2.internal.impl.commons.phase.RamlTypedFragment;
import org.raml.yagi.framework.grammar.rule.ClassNodeFactory;
import org.raml.yagi.framework.grammar.rule.NodeFactory;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nonnull;

public class TypeDeclarationNodeFactory implements NodeFactory {
    @Override
    public Node create(@Nonnull Node currentNode, Object... args) {
        if (currentNode instanceof RamlTypedFragment) return new ClassNodeFactory(TypeDeclarationNodeFragment.class).create(currentNode, args);
        return new ClassNodeFactory(TypeDeclarationNode.class).create(currentNode, args);
    }
}

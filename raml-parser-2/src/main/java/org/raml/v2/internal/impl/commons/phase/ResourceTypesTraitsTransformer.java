/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
/*
 *
 */
package org.raml.v2.internal.impl.commons.phase;

import static org.raml.v2.internal.impl.commons.phase.ResourceTypesTraitsMerger.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.v2.internal.impl.commons.grammar.BaseRamlGrammar;
import org.raml.v2.internal.impl.commons.nodes.BaseResourceTypeRefNode;
import org.raml.v2.internal.impl.commons.nodes.BaseTraitRefNode;
import org.raml.v2.internal.impl.commons.nodes.MethodNode;
import org.raml.v2.internal.impl.commons.nodes.ParametrizedReferenceNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceTypeNode;
import org.raml.v2.internal.impl.commons.nodes.StringTemplateNode;
import org.raml.v2.internal.impl.commons.nodes.TraitNode;
import org.raml.v2.internal.impl.v08.grammar.Raml08Grammar;
import org.raml.v2.internal.impl.v08.phase.ReferenceResolverTransformerV08;
import org.raml.v2.internal.impl.v10.phase.ReferenceResolverTransformer;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.ExecutionContext;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.ReferenceNode;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.yagi.framework.nodes.snakeyaml.SYBaseRamlNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYNullNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYObjectNode;
import org.raml.yagi.framework.phase.GrammarPhase;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.phase.TransformationPhase;
import org.raml.yagi.framework.phase.Transformer;
import org.raml.yagi.framework.util.NodeSelector;
import org.raml.yagi.framework.util.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypesTraitsTransformer implements Transformer
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Set<ResourceNode> mergedResources = new HashSet<>();
    private BaseRamlGrammar ramlGrammar;

    public ResourceTypesTraitsTransformer(BaseRamlGrammar ramlGrammar)
    {
        this.ramlGrammar = ramlGrammar;
    }

    @Override
    public boolean matches(Node node)
    {
        return (node instanceof BaseTraitRefNode ||
               node instanceof BaseResourceTypeRefNode) &&
               node.findAncestorWith(ResourceNode.class) != null;
    }

    @Override
    public Node transform(Node node)
    {
        ResourceNode resourceNode = node.findAncestorWith(ResourceNode.class);
        if (mergedResources.contains(resourceNode))
        {
            return node;
        }

        // apply method and resource traits if defined
        checkTraits(resourceNode, resourceNode);

        // apply resource type if defined
        ReferenceNode resourceTypeReference = findResourceTypeReference(resourceNode);
        if (resourceTypeReference != null)
        {
            applyResourceType(resourceNode, resourceTypeReference, resourceNode);
        }

        mergedResources.add(resourceNode);
        return node;
    }

    private void checkTraits(KeyValueNode resourceNode, ResourceNode baseResourceNode)
    {
        final List<MethodNode> methodNodes = findMethodNodes(resourceNode);
        final List<ReferenceNode> resourceTraitRefs = findTraitReferences(resourceNode);

        for (MethodNode methodNode : methodNodes)
        {
            final List<ReferenceNode> traitRefs = findTraitReferences(methodNode);
            traitRefs.addAll(resourceTraitRefs);
            for (final ReferenceNode traitRef : traitRefs)
            {
                final String traitLevel = resourceTraitRefs.contains(traitRef) ? "resource" : "method";
                logger.debug("applying {} level trait '{}' to '{}.{}'", traitLevel, traitRef.getRefName(), resourceNode.getKey(), methodNode.getName());
                applyTrait(methodNode, traitRef, baseResourceNode);
            }
        }
    }

    private void applyResourceType(KeyValueNode targetNode, ReferenceNode resourceTypeReference, ResourceNode baseResourceNode)
    {
        ResourceTypeNode refNode = (ResourceTypeNode) resourceTypeReference.getRefNode();
        if (refNode.getValue() instanceof NullNode)
        {
            // empty resource type
            return;
        }
        ResourceTypeNode templateNode = refNode.copy();
        templateNode.setParent(refNode.getParent());

        // generateDefinition parameters
        Map<String, Node> parameters = getBuiltinResourceTypeParameters(baseResourceNode);
        if (resourceTypeReference instanceof ParametrizedReferenceNode)
        {
            parameters.putAll(((ParametrizedReferenceNode) resourceTypeReference).getParameters());
        }
        resolveParameters(templateNode, parameters, NodeUtils.getContextNode(baseResourceNode));

        // apply grammar phase to generate method nodes
        GrammarPhase grammarPhase = new GrammarPhase(ramlGrammar.resourceTypeParamsResolved());
        // generateDefinition references
        TransformationPhase referenceResolution;
        if (ramlGrammar instanceof Raml08Grammar)
        {
            referenceResolution = new TransformationPhase(new ReferenceResolverTransformerV08());
        }
        else
        {
            referenceResolution = new TransformationPhase(new ReferenceResolverTransformer());
        }
        // resolves types
        boolean success = applyPhases(templateNode, grammarPhase);

        removeUnimplementedOptionalMethods(templateNode, baseResourceNode);

        success = success && applyPhases(templateNode, referenceResolution);

        if (success)
        {
            // apply traits
            checkTraits(templateNode, baseResourceNode);

            // generateDefinition inheritance
            ReferenceNode parentTypeReference = findResourceTypeReference(templateNode);
            if (parentTypeReference != null)
            {
                applyResourceType(templateNode, parentTypeReference, baseResourceNode);
            }
        }

        merge(targetNode.getValue(), templateNode.getValue());
    }

    private void removeUnimplementedOptionalMethods(ResourceTypeNode templateNode, ResourceNode baseResourceNode)
    {
        final List<MethodNode> unimplementedMethods = new ArrayList<>();
        for (MethodNode node : findMethodNodes(templateNode))
        {
            String key = node.getName();
            if (!key.endsWith("?"))
                continue;

            key = key.substring(0, key.length() - 1);
            Node methodInTemplateNode = NodeSelector.selectFrom(NodeSelector.encodePath(key), baseResourceNode.getValue());
            if (methodInTemplateNode == null)
                unimplementedMethods.add(node);
        }

        for (MethodNode unimplementedMethod : unimplementedMethods)
        {
            templateNode.getValue().removeChild(unimplementedMethod);
        }
    }

    private boolean applyPhases(KeyValueNode templateNode, Phase... phases)
    {
        List<ErrorNode> errorNodes = templateNode.findDescendantsWith(ErrorNode.class);
        if (errorNodes.isEmpty())
        {
            for (Phase phase : phases)
            {
                phase.apply(templateNode.getValue());
                errorNodes = templateNode.findDescendantsWith(ErrorNode.class);
                if (!errorNodes.isEmpty())
                {
                    return false;
                }
            }
        }
        return true;

    }

    private Map<String, Node> getBuiltinResourceTypeParameters(ResourceNode resourceNode)
    {
        Map<String, Node> parameters = new HashMap<>();
        parameters.put("resourcePathName", new StringNodeImpl(resourceNode.getResourcePathName()));
        parameters.put("resourcePath", new StringNodeImpl(resourceNode.getResourcePath()));
        return parameters;
    }

    private Map<String, Node> getBuiltinTraitParameters(MethodNode methodNode, ResourceNode resourceNode)
    {
        Map<String, Node> parameters = getBuiltinResourceTypeParameters(resourceNode);
        parameters.put("methodName", new StringNodeImpl(methodNode.getName()));
        return parameters;
    }

    private void applyTrait(MethodNode methodNode, ReferenceNode traitReference, ResourceNode baseResourceNode)
    {
        TraitNode refNode = (TraitNode) traitReference.getRefNode();
        if (refNode.getValue() instanceof NullNode)
        {
            // empty trait
            return;
        }

        TraitNode copy = refNode.copy();
        copy.setParent(refNode.getParent());

        replaceNullValueWithObject(copy);

        // generateDefinition parameters
        Map<String, Node> parameters = getBuiltinTraitParameters(methodNode, baseResourceNode);
        if (traitReference instanceof ParametrizedReferenceNode)
        {
            parameters.putAll(((ParametrizedReferenceNode) traitReference).getParameters());
        }
        resolveParameters(copy, parameters, NodeUtils.getContextNode(methodNode));

        // apply grammar phase to generate method nodes
        GrammarPhase validatePhase = new GrammarPhase(ramlGrammar.traitParamsResolved());

        // generateDefinition references
        TransformationPhase referenceResolution;
        if (ramlGrammar instanceof Raml08Grammar)
        {
            referenceResolution = new TransformationPhase(new ReferenceResolverTransformerV08());
        }
        else
        {
            referenceResolution = new TransformationPhase(new ReferenceResolverTransformer());
        }
        // resolves types
        applyPhases(copy, validatePhase, referenceResolution);

        replaceNullValueWithObject(methodNode);
        merge(methodNode.getValue(), copy.getValue());
    }

    private void resolveParameters(Node parameterizedNode, Map<String, Node> parameters, Node referenceContext)
    {
        ExecutionContext context = new ExecutionContext(parameters, referenceContext);
        List<StringTemplateNode> templateNodes = parameterizedNode.findDescendantsWith(StringTemplateNode.class);
        for (StringTemplateNode templateNode : templateNodes)
        {
            Node resolvedNode = templateNode.execute(context);
            templateNode.replaceTree(resolvedNode);

        }
    }

    private void replaceNullValueWithObject(KeyValueNode keyValueNode)
    {
        Node valueNode = keyValueNode.getValue();
        if (valueNode instanceof SYNullNode)
        {
            final SYBaseRamlNode ramlNode = (SYBaseRamlNode) valueNode;
            valueNode = new SYObjectNode(ramlNode);
            keyValueNode.setValue(valueNode);
        }
    }

    private List<ReferenceNode> findTraitReferences(KeyValueNode keyValueNode)
    {
        List<ReferenceNode> result = new ArrayList<>();
        Node isNode = NodeSelector.selectFrom("is", keyValueNode.getValue());
        if (isNode != null)
        {
            List<Node> children = isNode.getChildren();
            for (Node child : children)
            {
                result.add((ReferenceNode) child);
            }
        }
        return result;
    }

    private ReferenceNode findResourceTypeReference(KeyValueNode resourceNode)
    {
        return (ReferenceNode) NodeSelector.selectFrom("type", resourceNode.getValue());
    }

    private List<MethodNode> findMethodNodes(KeyValueNode resourceNode)
    {
        List<MethodNode> methodNodes = new ArrayList<>();
        for (Node node : resourceNode.getValue().getChildren())
        {
            if (node instanceof MethodNode)
            {
                methodNodes.add((MethodNode) node);
            }
        }
        return methodNodes;
    }

}

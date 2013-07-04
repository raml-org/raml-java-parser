package org.raml.parser.visitor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.rule.DefaultTupleRule;
import org.raml.parser.rule.NodeRule;
import org.raml.parser.rule.SequenceRule;
import org.raml.parser.rule.TupleRule;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class YamlDocumentValidator implements NodeHandler
{

    private Class<?> documentClass;

    private Stack<NodeRule<?>> ruleContext = new Stack<NodeRule<?>>();

    private List<ValidationResult> errorMessage = new ArrayList<ValidationResult>();

    public YamlDocumentValidator(Class<?> documentClass)
    {
        this.documentClass = documentClass;

    }

    public List<ValidationResult> validate(String content)
    {
        Yaml yamlParser = new Yaml();

        try
        {
            NodeVisitor nodeVisitor = new NodeVisitor(this);
            for (Node data : yamlParser.composeAll(new StringReader(content)))
            {
                if (data instanceof MappingNode)
                {
                    nodeVisitor.visitDocument((MappingNode) data);
                }
                else
                {
                    //   errorMessage.add(ValidationResult.createErrorResult(EMPTY_DOCUMENT_MESSAGE));
                }
            }

        }
        catch (YAMLException ex)
        {
            // errorMessage.add(ValidationResult.createErrorResult(ex.getMessage()));
        }
        return errorMessage;
    }


    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {

    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode)
    {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        SequenceRule peek = (SequenceRule) ruleContext.peek();

        switch (tupleType)
        {
            case VALUE:
                result = ((NodeRule<SequenceNode>) peek).validateValue(node);
                ruleContext.push(peek.getItemRule());
                break;
        }
        addErrorMessageIfRequired(node, result);
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {
        switch (tupleType)
        {
            case VALUE:
                ruleContext.pop();
                break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {
        List<ValidationResult> result;
        NodeRule<?> peek = ruleContext.peek();

        switch (tupleType)
        {
            case VALUE:
                result = ((NodeRule<ScalarNode>) peek).validateValue(node);
                break;

            default:
                result = ((TupleRule<ScalarNode, ?>) peek).validateKey(node);
                break;
        }
        addErrorMessageIfRequired(node, result);
    }

    private void addErrorMessageIfRequired(Node node, List<ValidationResult> result)
    {
        for (ValidationResult validationResult : result)
        {
            if (!validationResult.isValid())
            {
                errorMessage.add(validationResult);
            }
        }
    }

    @Override
    public void onDocumentStart(MappingNode node)
    {
        ruleContext.push(buildDocumentRule());
    }

    @Override
    public void onDocumentEnd(MappingNode node)
    {
        NodeRule<?> pop = ruleContext.pop();

        List<ValidationResult> onRuleEnd = pop.onRuleEnd();
        addErrorMessageIfRequired(node, onRuleEnd);

    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        NodeRule<?> rule = ruleContext.pop();
        if (rule != null)
        {
            List<ValidationResult> onRuleEnd = rule.onRuleEnd();
            addErrorMessageIfRequired(nodeTuple.getKeyNode(), onRuleEnd);
        }
        else
        {
            throw new IllegalStateException("Unexpected ruleContext state");
        }
    }

    @Override
    public void onTupleStart(NodeTuple nodeTuple)
    {

        TupleRule<?, ?> tupleRule = (TupleRule<?, ?>) ruleContext.peek();
        if (tupleRule != null)
        {
            TupleRule<?, ?> rule = tupleRule.getRuleForTuple(nodeTuple);
            ruleContext.push(rule);
        }
        else
        {
            throw new IllegalStateException("Unexpected ruleContext state");
        }

    }

    private DefaultTupleRule<Node, MappingNode> buildDocumentRule()
    {
        DefaultTupleRule<Node, MappingNode> documentRule = new DefaultTupleRule<Node, MappingNode>(null, new DefaultTupleHandler());
        documentRule.addRulesFor(documentClass);
        return documentRule;
    }

}

package org.raml.parser.rule;

import static org.raml.parser.visitor.IncludeResolver.IncludeScalarNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SchemaRule extends SimpleRule
{

    public SchemaRule()
    {
        super("schema", String.class);
    }

    @Override
    public List<ValidationResult> validateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = super.validateValue(node);

        String mimeType = ((ScalarNode) getParentTupleRule().getKey()).getValue();
        if (mimeType.contains("json"))
        {
            try
            {
                value = getGlobalSchemaIfDefined(value);
                JsonLoader.fromString(value);
            }
            catch (IOException e)
            {
                String prefix = "invalid JSON schema" + getSourceErrorDetail(node);
                validationResults.add(ValidationResult.createErrorResult(prefix + e.getMessage(), node.getStartMark(), node.getEndMark()));
            }
        }
        else if (mimeType.contains("xml"))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try
            {
                value = getGlobalSchemaIfDefined(value);
                factory.newSchema(new StreamSource(new StringReader(value)));
            }
            catch (SAXException e)
            {
                String prefix = "invalid XML schema" + getSourceErrorDetail(node);
                validationResults.add(ValidationResult.createErrorResult(prefix + e.getMessage(), node.getStartMark(), node.getEndMark()));
            }
        }
        return validationResults;
    }

    private String getSourceErrorDetail(ScalarNode node)
    {
        String msg = "";
        if (node instanceof IncludeScalarNode)
        {
            msg = " (" + ((IncludeScalarNode) node).getIncludeName() + ")";
        }
        else if (node.getValue().matches("\\w.*"))
        {
            msg = " (" + node.getValue() + ")";
        }
        return msg + ": ";
    }

    private String getGlobalSchemaIfDefined(String key)
    {
        String globalSchema = getGlobalSchemas().get(key);
        return globalSchema != null ? globalSchema : key;
    }

    private Map<String, String> getGlobalSchemas()
    {
        GlobalSchemasRule schemasRule = (GlobalSchemasRule) getRootTupleRule().getRuleByFieldName("schemas");
        return schemasRule.getSchemas();
    }
}

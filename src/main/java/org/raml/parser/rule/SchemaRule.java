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
package org.raml.parser.rule;

import static org.raml.parser.rule.ValidationResult.UNKNOWN;
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.raml.parser.tagresolver.IncludeResolver.INCLUDE_APPLIED_TAG;
import static org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;
import static org.yaml.snakeyaml.nodes.Tag.STR;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.visitor.IncludeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SchemaRule extends SimpleRule implements IRuleWithContext<ScalarNode>
{

    private static final class LSInputWrapper implements LSInput {
		InputSource source;
		private String stringData;

		public LSInputWrapper(InputSource inputSource) {
			this.source=inputSource;
		}

		public int hashCode() {
			return source.hashCode();
		}

		public boolean equals(Object obj) {
			return source.equals(obj);
		}

		public void setPublicId(String publicId) {
			source.setPublicId(publicId);
		}

		public String getPublicId() {
			return source.getPublicId();
		}

		public void setSystemId(String systemId) {
			source.setSystemId(systemId);
		}

		public String getSystemId() {
			return source.getSystemId();
		}

		public void setByteStream(InputStream byteStream) {
			source.setByteStream(byteStream);
		}

		public InputStream getByteStream() {
			return source.getByteStream();
		}

		public void setEncoding(String encoding) {
			source.setEncoding(encoding);
		}

		public String getEncoding() {
			return source.getEncoding();
		}

		public void setCharacterStream(Reader characterStream) {
			source.setCharacterStream(characterStream);
		}

		public String toString() {
			return source.toString();
		}

		public Reader getCharacterStream() {
			return source.getCharacterStream();
		}

		@Override
		public String getStringData() {
			return stringData;
		}

		@Override
		public void setStringData(String stringData) {
			this.stringData=stringData;
		}
		String baseUri;
		private boolean certifiedText;

		@Override
		public String getBaseURI() {
			return baseUri;
		}

		@Override
		public void setBaseURI(String baseURI) {
			this.baseUri=baseURI;
		}

		@Override
		public boolean getCertifiedText() {
			return certifiedText;
		}

		@Override
		public void setCertifiedText(boolean certifiedText) {
			this.certifiedText=certifiedText;
		}

		
	}

	private static final SyntaxValidator VALIDATOR = new SyntaxValidator(ValidationConfiguration.newBuilder().setDefaultVersion(SchemaVersion.DRAFTV3).freeze());

    public SchemaRule()
    {
        super("schema", String.class);
    }

    @Override
    public List<ValidationResult> doValidateValue(ScalarNode node) {
    	return doValidateValue(node,null);
    }
    
    public List<ValidationResult> doValidateValue(ScalarNode node,IParserContext context)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = super.doValidateValue(node);
        final ResourceLoader resourceLoader = context.getResourceLoader();
        IncludeInfo globaSchemaIncludeInfo = null;
        final ContextPath contextPath = context.getContextPath();
        ScalarNode schemaNode = getGlobalSchemaNode(value);
        if (schemaNode == null)
        {
            schemaNode = node;
        }
        else
        {
            value = schemaNode.getValue();
            if (schemaNode.getTag().startsWith(INCLUDE_APPLIED_TAG))
            {
                globaSchemaIncludeInfo = new IncludeInfo(schemaNode.getTag());
            }
        }
        if (value == null || isCustomTag(schemaNode.getTag()))
        {
            return validationResults;
        }

        String mimeType = ((ScalarNode) getParentTupleRule().getKey()).getValue();
        if (mimeType.contains("json"))
        {
            try
            {
                JsonNode jsonNode = JsonLoader.fromString(value);
                ProcessingReport report = VALIDATOR.validateSchema(jsonNode);
                if (!report.isSuccess())
                {
                    StringBuilder msg = new StringBuilder("invalid JSON schema");
                    msg.append(getSourceErrorDetail(node));
                    for (ProcessingMessage processingMessage : report)
                    {
                        msg.append("\n").append(processingMessage.toString());
                    }
                    validationResults.add(getErrorResult(msg.toString(), getLineOffset(schemaNode), globaSchemaIncludeInfo));
                }
            }
            catch (JsonParseException jpe)
            {
                String msg = "invalid JSON schema" + getSourceErrorDetail(node) + jpe.getOriginalMessage();
                JsonLocation loc = jpe.getLocation();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode) + loc.getLineNr(), globaSchemaIncludeInfo));
            }
            catch (IOException e)
            {
                String prefix = "invalid JSON schema" + getSourceErrorDetail(node);
                validationResults.add(getErrorResult(prefix + e.getMessage(), UNKNOWN, globaSchemaIncludeInfo));
            }
        }
        else if (mimeType.contains("xml"))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final IncludeInfo gI=globaSchemaIncludeInfo;
            factory.setResourceResolver(new LSResourceResolver() {
				
				@Override
				public LSInput resolveResource(String type, String namespaceURI,
						String publicId, String systemId, String baseURI) {
					String parent = null;
					if (gI != null) {
						String includeName = gI.getIncludeName();
						parent = removeLastSegment(includeName);
					}
					else{
						IncludeInfo peek = contextPath.peek();
						parent=removeLastSegment(peek.getIncludeName());
					}
					if (parent != null) {
						InputStream fetchResource = resourceLoader
								.fetchResource(ContextPath
										.resolveRelatives(parent + systemId));

						if (fetchResource != null) {
							return new LSInputWrapper(new InputSource(
									fetchResource));
						}
					}
					return null;
				}

				private String removeLastSegment(String includeName) {
					int lastIndexOf = includeName.lastIndexOf('/');
					String parent=null;
					if (lastIndexOf != -1) {
						parent = includeName.substring(0, lastIndexOf + 1);
					}
					else{
						parent="";
					}
					return parent;
				}
			});
            try
            {
                factory.newSchema(new StreamSource(new StringReader(value)));
            }
            catch (SAXParseException e)
            {
                String msg = "invalid XML schema" + getSourceErrorDetail(node) + e.getMessage();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode) + e.getLineNumber(), globaSchemaIncludeInfo));
            }
            catch (SAXException e)
            {
                String msg = "invalid XML schema" + getSourceErrorDetail(node);
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode), globaSchemaIncludeInfo));
            }
        }
        return validationResults;
    }

    private ValidationResult getErrorResult(String msg, int line, IncludeInfo globaSchemaIncludeInfo)
    {
        ValidationResult errorResult = createErrorResult(msg, line, UNKNOWN, UNKNOWN);
        if (globaSchemaIncludeInfo != null)
        {
            errorResult.setExtraIncludeInfo(globaSchemaIncludeInfo);
        }
        return errorResult;
    }

    private int getLineOffset(ScalarNode schemaNode)
    {
        boolean isInclude = schemaNode.getTag().startsWith(INCLUDE_APPLIED_TAG);
        return isInclude ? -1 : schemaNode.getStartMark().getLine();
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

    private ScalarNode getGlobalSchemaNode(String key)
    {
        GlobalSchemasRule schemasRule = (GlobalSchemasRule) getRootTupleRule().getRuleByFieldName("schemas");
        return schemasRule.getSchema(key);
    }

    private boolean isCustomTag(Tag tag)
    {
        return tag != null && !STR.equals(tag) && !tag.startsWith(INCLUDE_APPLIED_TAG);
    }

    @Override
    public TupleRule<?, ?> deepCopy()
    {
        checkClassToCopy(SchemaRule.class);
        SchemaRule copy = new SchemaRule();
        copy.setNodeRuleFactory(getNodeRuleFactory());
        copy.setHandler(getHandler());
        return copy;
    }

	
}

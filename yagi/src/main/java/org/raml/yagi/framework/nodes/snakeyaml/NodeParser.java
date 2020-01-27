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
package org.raml.yagi.framework.nodes.snakeyaml;

import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_UNIX;
import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_WINDOWS;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

import java.io.Reader;
import java.io.StringReader;

import javax.annotation.Nullable;

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.resolver.Resolver;

public class NodeParser
{

    public static final String DISABLE_ANCHORS_PROPERTY = "yagi.disable_anchors";

    // This non final to enable setting the value on tests
    private static boolean ANCHORS_DISABLED;

    /**
     * Configures the {@link #ANCHORS_DISABLED} field by reading the {@value #DISABLE_ANCHORS_PROPERTY} system property.
     * <p>
     *     This method was defined to enable testing by configuring the system property using reflection
     * </p>
     */
    private static void setAnchorsDisabled()
    {
        ANCHORS_DISABLED = Boolean.valueOf(System.getProperty(DISABLE_ANCHORS_PROPERTY, "false"));
    }

    static
    {
        setAnchorsDisabled();
    }

    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, Reader reader)
    {

        SmartReader smartReader = new SmartReader(reader);

        try
        {
            Yaml yamlParser = ANCHORS_DISABLED ? new SecureYaml() : new Yaml();
            org.yaml.snakeyaml.nodes.Node composedNode = yamlParser.compose(smartReader);
            if (composedNode == null)
            {
                return null;
            }
            else
            {
                return new SYModelWrapper(resourceLoader, resourcePath).wrap(composedNode);
            }
        }
        catch (final MarkedYAMLException e)
        {
            return buildYamlErrorNode(e, resourcePath);
        }
        catch (ReaderException e)
        {

            return buildYamlErrorNode(e, smartReader);
        }
    }

    /**
     * Uses a {@link SecureComposer} to disable parsing of anchors
     */
    private static class SecureYaml extends Yaml
    {
        @Override
        public org.yaml.snakeyaml.nodes.Node compose(Reader yaml)
        {
            Composer composer = new SecureComposer(new ParserImpl(new StreamReader(yaml)), resolver);
            return composer.getSingleNode();
        }
    }

    /**
     * Defined only to enable exception creation as {@link ComposerException} constructor has protected access
     */
    private static class InvalidComposerException extends ComposerException
    {
        protected InvalidComposerException(String context, Mark contextMark, String problem, Mark problemMark)
        {
            super(context, contextMark, problem, problemMark);
        }
    }

    /**
     * Disables the parsing of anchors
     */
    private static class SecureComposer extends Composer
    {
        public SecureComposer(Parser parser, Resolver resolver)
        {
            super(parser, resolver);
        }

        @Override
        protected org.yaml.snakeyaml.nodes.Node composeScalarNode(String anchor)
        {
            checkAnchorUsage(anchor);
            return super.composeScalarNode(anchor);
        }

        @Override
        protected org.yaml.snakeyaml.nodes.Node composeSequenceNode(String anchor)
        {
            checkAnchorUsage(anchor);
            return super.composeSequenceNode(anchor);
        }

        @Override
        protected org.yaml.snakeyaml.nodes.Node composeMappingNode(String anchor)
        {
            checkAnchorUsage(anchor);
            return super.composeMappingNode(anchor);
        }

        private void checkAnchorUsage(String anchor)
        {
            if (anchor != null)
            {
                throw new InvalidComposerException(null, null,
                        "Attempt to define anchor '" + anchor + "' but anchors are disabled.",
                        parser.getEvent().getStartMark());
            }
        }
    }

    private static Node buildYamlErrorNode(MarkedYAMLException e, String resourcePath)
    {
        final ErrorNode errorNode = new ErrorNode("Underlying error while parsing YAML syntax: '" + e.getMessage() + "'");
        final Mark problemMark = e.getProblemMark();
        errorNode.setStartPosition(new DefaultPosition(problemMark.getIndex(), problemMark.getLine(), 0, resourcePath, new DefaultResourceLoader()));
        errorNode.setEndPosition(new DefaultPosition(problemMark.getIndex() + 1, problemMark.getLine(), problemMark.getColumn(), resourcePath, new DefaultResourceLoader()));
        return errorNode;
    }

    private static Node buildYamlErrorNode(ReaderException e, SmartReader reader)
    {

        final ErrorNode errorNode = new ErrorNode("Underlying error while parsing YAML syntax: '" + e + " around: " + reader.getLastValidString() + "'");
        // errorNode.setStartPosition(new DefaultPosition(e.getPosition(), 0, 0, resourcePath, new DefaultResourceLoader()));
        // errorNode.setEndPosition(new DefaultPosition(e.getPosition() + 1, 0,0, resourcePath, new DefaultResourceLoader()));
        return errorNode;
    }

    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, String content)
    {
        if (IS_OS_WINDOWS)
        {
            content = content.replace(LINE_SEPARATOR_WINDOWS, LINE_SEPARATOR_UNIX);
        }

        return parse(resourceLoader, resourcePath, new StringReader(content));
    }


}

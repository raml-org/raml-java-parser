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
package org.raml.v2.internal.impl;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.EmptyErrorNode;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.DefaultSuggestion;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.ParsingContextType;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.yagi.framework.suggester.Suggestions;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.utils.Inflector;

public class RamlSuggester
{

    private ResourceLoader resourceLoader;

    public RamlSuggester(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public RamlSuggester()
    {
        this(new DefaultResourceLoader());
    }

    /**
     * Returns the suggestions for the specified document at the given position.
     * In most common cases the offset will be the cursor position.
     * @param document The raml document
     * @param offset The offset from the begging of the document
     * @return The suggestions
     */
    public Suggestions suggestions(String document, int offset)
    {
        final List<Suggestion> result = new ArrayList<>();
        final ParsingContext parsingContext = getContext(document, offset);
        final int location = parsingContext.getLocation();
        final String content = parsingContext.getContent();
        final List<Suggestion> suggestions = getSuggestions(parsingContext, document, offset, location);
        if (content.isEmpty())
        {
            result.addAll(suggestions);
        }
        else
        {
            for (Suggestion suggestion : suggestions)
            {
                if (suggestion.getValue().startsWith(content))
                {
                    result.add(suggestion);
                }
            }
        }
        Collections.sort(result);
        return new Suggestions(result, content, location);

    }

    private List<Suggestion> getSuggestions(ParsingContext context, String document, int offset, int location)
    {
        switch (context.getContextType())
        {
        case HEADER:
            return getHeaderSuggestions();
        case FUNCTION_CALL:
            return getFunctionCallSuggestions();
        case STRING_TEMPLATE:
            return getTemplateParameterSuggestions(document, offset, location);
        case LIBRARY_CALL:
        case ITEM:
        case VALUE:
            return getSuggestionsAt(context, document, offset, location);
        default:
            return getSuggestionByColumn(context, document, offset, location);

        }
    }


    @Nonnull
    private List<Suggestion> getTemplateParameterSuggestions(String document, int offset, int location)
    {
        final Node rootNode = getRootNode(document, offset, location);
        Node node = org.raml.yagi.framework.util.NodeUtils.searchNodeAt(rootNode, location);
        boolean inTrait = false;
        while (node != null)
        {

            if (node instanceof KeyValueNode)
            {
                if (((KeyValueNode) node).getKey() instanceof StringNode)
                {
                    final String value = ((StringNode) ((KeyValueNode) node).getKey()).getValue();
                    if (value.equals("traits") || value.equals("resourceTypes"))
                    {
                        inTrait = value.equals("traits");
                        break;
                    }
                }
            }
            node = node.getParent();
        }
        return inTrait ? defaultTraitParameters() : defaultResourceTypeParameters();
    }

    @Nonnull
    private List<Suggestion> defaultTraitParameters()
    {
        final List<Suggestion> suggestions = defaultResourceTypeParameters();
        suggestions.add(new DefaultSuggestion("methodName", "The name of the method", ""));
        return suggestions;
    }

    @Nonnull
    private List<Suggestion> defaultResourceTypeParameters()
    {
        final List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new DefaultSuggestion("resourcePath", "The resource's full URI relative to the baseUri (if any)", ""));
        suggestions.add(new DefaultSuggestion("resourcePathName", "The rightmost path fragment of the resource's relative URI, " +
                                                                  "omitting any parametrize brackets (\"{\" and \"}\")", ""));
        return suggestions;
    }

    @Nonnull
    private List<Suggestion> getFunctionCallSuggestions()
    {
        List<Suggestion> suggestions = new ArrayList<>();
        final Method[] declaredMethods = Inflector.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods)
        {
            if (Modifier.isStatic(declaredMethod.getModifiers()) && Modifier.isPublic(declaredMethod.getModifiers()))
            {
                suggestions.add(new DefaultSuggestion("!" + declaredMethod.getName(), "", declaredMethod.getName()));
            }
        }
        return suggestions;
    }

    private List<Suggestion> getSuggestionsAt(ParsingContext context, String document, int offset, int location)
    {
        final Node root = getRootNode(document, offset, location);
        Node node = org.raml.yagi.framework.util.NodeUtils.searchNodeAt(root, location);
        if (node != null)
        {
            // If it is the key of a key value pair
            if (node.getParent() instanceof KeyValueNode && node.getParent().getChildren().indexOf(node) == 0)
            {
                node = node.getParent().getParent();
            }
            // Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);
            // Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = getRuleFor(document);
            return rootRule != null ? rootRule.getSuggestions(pathToRoot, context) : Collections.<Suggestion> emptyList();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private Node getRootNode(String document, int offset, int location)
    {
        // We only run the first phase
        final RamlBuilder ramlBuilder = new RamlBuilder(RamlBuilder.FIRST_PHASE);
        try
        {
            // We try the with the original document
            final Node rootNode = ramlBuilder.build(document, resourceLoader, "");
            if (rootNode instanceof StringNode)
            {
                // File still doesn't have any mapping and will not generate any suggestions so we'll force the parsing to make it a mapping
                final Node rootNode2 = ramlBuilder.build(stripLastChanges(document, offset, location) + "\n\nstub: stub", resourceLoader, ""); // we add an invalid key so as to enforce the creation of
                // the root node
                if (rootNode2 instanceof ErrorNode)
                {
                    // Otherwise let's just try to remove the whole line starting from where we are located
                    return ramlBuilder.build(removeChangedLine(document, offset, location) + "\n\nstub: stub", resourceLoader, "");
                }
                else
                {
                    return rootNode2;
                }
            }
            else if (!(rootNode instanceof ErrorNode))
            {
                return rootNode;
            }
            else if (rootNode instanceof EmptyErrorNode)
            {
                // File is not corrupted but just empty, we should suggest initial keys for the current file
                return ramlBuilder.build(document + "\n\nstub: stub", resourceLoader, ""); // we add an invalid key so as to force the creation of the root node
            }
            else
            { // let's just try to remove the whole line starting from where we are located
                return ramlBuilder.build(removeChangedLine(document, offset, location) + "\n\nstub: stub", resourceLoader, "");
            }
        }
        catch (final Exception e)
        {
            // We remove some current keywords to see if it parses
            return ramlBuilder.build(stripLastChanges(document, offset, location), resourceLoader, "");
        }
    }

    private String removeChangedLine(String document, int offset, int location)
    {
        final String header = document.substring(0, location + 1);
        final String footer = getFooter1(document, offset);
        return header + footer;
    }

    private String stripLastChanges(String document, int offset, int location)
    {
        final String header = document.substring(0, location + 1);
        final String footer = getFooter(document, offset);
        return header + footer;
    }

    private List<Suggestion> getSuggestionByColumn(ParsingContext context, String document, int offset, int location)
    {
        // // I don't care column number unless is an empty new line
        int columnNumber = getColumnNumber(document, offset);
        final Node root = getRootNode(document, offset, location);
        Node node = org.raml.yagi.framework.util.NodeUtils.searchNodeAt(root, location);

        if (node != null)
        {
            node = getValueNodeAtColumn(columnNumber, node);
            // Recreate path with the node at the correct indentation
            final List<Node> pathToRoot = createPathToRoot(node);

            // Follow the path from the root to the node and apply the rules for auto-completion.
            final Rule rootRule = getRuleFor(document);
            return rootRule != null ? rootRule.getSuggestions(pathToRoot, context) : Collections.<Suggestion> emptyList();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private int getLineNumber(final String document, final int offset)
    {
        int lineNumber = 0;
        for (int currentIndex = 0; currentIndex <= offset; ++currentIndex)
        {
            if (currentIndex - 1 == offset || //
                currentIndex >= document.length()) // should never be possible if offset is legal
            {
                return lineNumber;
            }

            if (document.charAt(currentIndex) == '\n')
            {
                ++lineNumber;
            }
        }

        // should never reach this
        return lineNumber;
    }

    private List<Suggestion> getHeaderSuggestions()
    {
        return Arrays.asList(
                (Suggestion) new DefaultSuggestion("#%RAML 1.0", "RAML 1.0 root file header", DefaultSuggestion.RAML_1_0_HEADER),
                new DefaultSuggestion("#%RAML 1.0 DocumentationItem", "An item in the collection of items that is the value of the root-level documentation property",
                        "RAML 1.0 Documentation Item fragment"),
                new DefaultSuggestion("#%RAML 1.0 DataType", "A data type declaration where the type property may be used", "RAML 1.0 Data Type fragment"),
                new DefaultSuggestion("#%RAML 1.0 NamedExample", "A property of the examples property, whose key is a name of an example and whose value describes the example",
                        "RAML 1.0 Named Example fragment"),
                new DefaultSuggestion("#%RAML 1.0 ResourceType", "A single resource type declaration", "RAML 1.0 Resource Type fragment"),
                new DefaultSuggestion("#%RAML 1.0 Trait", "A single trait declaration", "RAML 1.0 Trait fragment"),
                new DefaultSuggestion("#%RAML 1.0 AnnotationTypeDeclaration", "A single annotation type declaration", "RAML 1.0 Annotation Type Declaration fragment"),
                new DefaultSuggestion("#%RAML 1.0 Library", "A RAML library", "RAML 1.0 Library fragment"),
                new DefaultSuggestion("#%RAML 1.0 Overlay", "An overlay file", "RAML 1.0 Overlay fragment"),
                new DefaultSuggestion("#%RAML 1.0 Extension", "An extension file", "RAML 1.0 Extension fragment"),
                new DefaultSuggestion("#%RAML 1.0 SecurityScheme", "A definition of a security scheme", "RAML 1.0 Security Scheme fragment"));
    }

    @Nonnull
    private ParsingContext getContext(String document, int offset)
    {
        if (offset == -1 || getLineNumber(document, offset) == 0)
        {
            final String content = offset < 0 || document.isEmpty() ? "" : document.substring(0, offset + 1);
            return new ParsingContext(ParsingContextType.HEADER, content, offset);
        }

        ParsingContext context = null;
        int location = offset;
        final StringBuilder content = new StringBuilder();
        while (location >= 0 && context == null)
        {
            char character = document.charAt(location);
            switch (character)
            {
            case ':':
                context = new ParsingContext(ParsingContextType.VALUE, revertAndTrim(content), location + 1);
                break;
            case ',':
            case '[':
            case '{':
            case '-':
                context = new ParsingContext(ParsingContextType.ITEM, revertAndTrim(content), location);
                break;
            case '<':
                if (location > 0)
                {
                    if (document.charAt(location - 1) == '<')
                    {
                        location--;
                        final String contextContent = revertAndTrim(content);
                        final String[] split = contextContent.split("\\|");
                        if (split.length > 1)
                        {
                            context = new ParsingContext(ParsingContextType.FUNCTION_CALL, split[split.length - 1].trim(), location);
                        }
                        else if (contextContent.endsWith("|"))
                        {
                            context = new ParsingContext(ParsingContextType.FUNCTION_CALL, "", location);
                        }
                        else
                        {
                            context = new ParsingContext(ParsingContextType.STRING_TEMPLATE, contextContent, location);
                        }
                        break;
                    }
                }
                content.append(character);
                break;
            case '.':
                context = new ParsingContext(ParsingContextType.LIBRARY_CALL, revertAndTrim(content), location);
                break;
            case '\n':
                context = new ParsingContext(ParsingContextType.ANY, revertAndTrim(content), location);
                break;
            default:
                content.append(character);
            }
            location--;
        }

        if (context == null)
        {
            context = new ParsingContext(ParsingContextType.ANY, revertAndTrim(content), location);
        }

        return context;
    }

    @Nonnull
    private String revertAndTrim(StringBuilder content)
    {
        return content.reverse().toString().trim();
    }

    private Node getValueNodeAtColumn(int columnNumber, Node node)
    {
        if (columnNumber == 0)
        {
            return node.getRootNode();
        }
        else
        {
            // Create the path from the selected node to the root.
            final List<Node> path = createPathToRoot(node);
            // Find the node with the indentation in the path
            for (Node element : path)
            {
                if (element instanceof KeyValueNode)
                {
                    if (element.getStartPosition().getColumn() < columnNumber)
                    {
                        // In an object we need that it should be minor for arrays
                        node = ((KeyValueNode) element).getValue();
                    }
                }
                else if (element instanceof ObjectNode)
                {
                    // In an object we need that it should be equals or minor
                    if (element.getStartPosition().getColumn() <= columnNumber)
                    {
                        node = element;
                    }
                }
            }
            return node;
        }
    }

    @Nonnull
    private List<Node> createPathToRoot(Node node)
    {
        final List<Node> path = new ArrayList<>();
        Node parent = node;
        while (parent != null)
        {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
    }

    @Nonnull
    private String getFooter(String document, int offset)
    {
        int loc = offset;
        char current = document.charAt(loc);
        while (loc < document.length() && current != '\n' && current != '}' && current != ']' && current != ',')
        {
            loc++;
            if (loc == document.length())
            {
                break;
            }
            current = document.charAt(loc);
        }

        return loc < document.length() ? document.substring(loc) : "";
    }

    @Nonnull
    private String getFooter1(String document, int offset)
    {
        return getFooter(document, offset + 1);
    }

    private int getColumnNumber(String document, int offset)
    {
        final StringBuilder contextLine = getContextLine(document, offset);
        int columnNumber = 0;
        for (int i = 0; i < contextLine.length(); i++)
        {
            if (Character.isWhitespace(contextLine.charAt(i)))
            {
                columnNumber++;
            }
            else
            {
                break;
            }
        }
        return columnNumber;
    }

    @Nonnull
    private StringBuilder getContextLine(String document, int offset)
    {
        final StringBuilder contextLine = new StringBuilder();

        if (offset < 0) // start of file detected!
        {
            return contextLine;
        }

        int location = offset;
        char character = document.charAt(location);
        while (location > 0 && character != '\n')
        {
            location--;
            contextLine.append(character);
            character = document.charAt(location);

        }
        return contextLine.reverse();
    }

    @Nullable
    public Rule getRuleFor(String stringContent)
    {
        try
        {
            RamlHeader ramlHeader = RamlHeader.parse(stringContent);
            return ramlHeader.getRule();
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            // ignore, just return null
        }
        return null;
    }

}

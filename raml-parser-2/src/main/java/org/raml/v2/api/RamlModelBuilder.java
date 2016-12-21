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
package org.raml.v2.api;

import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_08;
import static org.raml.v2.internal.utils.RamlNodeUtils.getVersion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.raml.v2.api.loader.CompositeResourceLoader;
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.FileResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.api.model.v10.api.DocumentationItem;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Trait;
import org.raml.v2.api.model.v10.resources.ResourceType;
import org.raml.v2.api.model.v10.security.SecurityScheme;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.model.Api;
import org.raml.v2.internal.impl.commons.model.DefaultModelElement;
import org.raml.v2.internal.impl.commons.model.RamlValidationResult;
import org.raml.v2.internal.impl.commons.model.StringType;
import org.raml.v2.internal.impl.commons.model.factory.TypeDeclarationModelFactory;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.utils.RamlNodeUtils;
import org.raml.v2.internal.utils.StreamUtils;
import org.raml.yagi.framework.model.DefaultModelBindingConfiguration;
import org.raml.yagi.framework.model.ModelBindingConfiguration;
import org.raml.yagi.framework.model.ModelProxyBuilder;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Entry point class to parse top level RAML descriptors.
 * Supports versions 0.8 and 1.0
 */
public class RamlModelBuilder
{

    public static final String MODEL_PACKAGE = "org.raml.v2.internal.impl.commons.model";
    private ResourceLoader resourceLoader;
    private RamlBuilder builder = new RamlBuilder();

    public RamlModelBuilder()
    {
        this(new DefaultResourceLoader());
    }

    public RamlModelBuilder(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Nonnull
    public RamlModelResult buildApi(String ramlLocation)
    {
        String content = getRamlContent(ramlLocation);
        if (content == null)
        {
            return generateRamlApiResult("Raml does not exist at: " + ramlLocation);
        }
        return buildApi(content, ramlLocation);
    }

    @Nonnull
    public RamlModelResult buildApi(File ramlFile)
    {
        String content = getRamlContent(ramlFile);
        if (content == null)
        {
            return generateRamlApiResult("Files does not exist or is not a regular file: " + ramlFile.getPath());
        }
        return buildApi(content, ramlFile.getPath());
    }

    @Nonnull
    public RamlModelResult buildApi(Reader reader, String ramlLocation)
    {
        String content = getRamlContent(reader);
        if (content == null)
        {
            return generateRamlApiResult("Invalid reader provided with location: " + ramlLocation);
        }
        return buildApi(content, ramlLocation);
    }

    @Nonnull
    public RamlModelResult buildApi(String content, String ramlLocation)
    {
        if (content == null)
        {
            return buildApi(ramlLocation);
        }
        Node ramlNode = builder.build(content, resourceLoader, ramlLocation);
        return generateRamlApiResult(ramlNode, getFragment(content));
    }

    private RamlFragment getFragment(String content)
    {
        try
        {
            RamlHeader ramlHeader = RamlHeader.parse(content);
            return ramlHeader.getFragment();
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            // ignore, already handled by builder
        }
        return null;
    }

    private RamlModelResult generateRamlApiResult(Node ramlNode, RamlFragment fragment)
    {
        List<ValidationResult> validationResults = new ArrayList<>();
        List<ErrorNode> errors = RamlNodeUtils.getErrors(ramlNode);
        for (ErrorNode errorNode : errors)
        {
            validationResults.add(new RamlValidationResult(errorNode));
        }
        if (validationResults.isEmpty())
        {
            return wrapTree(ramlNode, fragment);
        }
        return new RamlModelResult(validationResults);
    }

    private RamlModelResult generateRamlApiResult(String errorMessage)
    {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(new RamlValidationResult(errorMessage));
        return new RamlModelResult(validationResults);
    }

    private RamlModelResult wrapTree(Node ramlNode, RamlFragment fragment)
    {
        if (ramlNode instanceof RamlDocumentNode && getVersion(ramlNode) == RAML_08)
        {
            org.raml.v2.api.model.v08.api.Api apiV08 = ModelProxyBuilder.createModel(org.raml.v2.api.model.v08.api.Api.class, new Api((RamlDocumentNode) ramlNode), createV08Binding());
            return new RamlModelResult(apiV08);
        }
        if (ramlNode instanceof RamlDocumentNode)
        {
            org.raml.v2.api.model.v10.api.Api apiV10 = ModelProxyBuilder.createModel(org.raml.v2.api.model.v10.api.Api.class, new Api((RamlDocumentNode) ramlNode), createV10Binding());
            return new RamlModelResult(apiV10);
        }
        if (fragment == RamlFragment.Library)
        {
            Library library = ModelProxyBuilder.createModel(Library.class, new DefaultModelElement(ramlNode), createV10Binding());
            return new RamlModelResult(library);
        }
        if (fragment == RamlFragment.DataType || fragment == RamlFragment.AnnotationTypeDeclaration)
        {
            final org.raml.v2.internal.impl.commons.model.type.TypeDeclaration delegateNode = new TypeDeclarationModelFactory().create(ramlNode);
            final ModelBindingConfiguration v10Binding = createV10Binding();
            TypeDeclaration typeDeclaration = ModelProxyBuilder.createModel((Class<TypeDeclaration>) v10Binding.reverseBindingOf(delegateNode), delegateNode, v10Binding);
            return new RamlModelResult(typeDeclaration);
        }
        if (fragment == RamlFragment.DocumentationItem)
        {
            DocumentationItem documentationItem = ModelProxyBuilder.createModel(DocumentationItem.class, new DefaultModelElement(ramlNode), createV10Binding());
            return new RamlModelResult(documentationItem);
        }
        if (fragment == RamlFragment.SecurityScheme)
        {
            SecurityScheme securityScheme = ModelProxyBuilder.createModel(SecurityScheme.class, new DefaultModelElement(ramlNode), createV10Binding());
            return new RamlModelResult(securityScheme);
        }
        if (fragment == RamlFragment.Trait)
        {
            Trait trait = ModelProxyBuilder.createModel(Trait.class, new DefaultModelElement(ramlNode), createV10Binding());
            return new RamlModelResult(trait);
        }
        if (fragment == RamlFragment.ResourceType)
        {
            ResourceType resourceType = ModelProxyBuilder.createModel(ResourceType.class, new DefaultModelElement(ramlNode), createV10Binding());
            return new RamlModelResult(resourceType);
        }
        if (fragment == RamlFragment.NamedExample)
        {
            if (!(ramlNode instanceof KeyValueNode))
            {
                ramlNode = new KeyValueNodeImpl(new StringNodeImpl("__NamedExample_Fragment__"), ramlNode);
            }
            ExampleSpec exampleSpec = ModelProxyBuilder.createModel(ExampleSpec.class, new org.raml.v2.internal.impl.commons.model.ExampleSpec((KeyValueNode) ramlNode), createV10Binding());
            return new RamlModelResult(exampleSpec);
        }
        throw new IllegalStateException("Invalid ramlNode type (" + ramlNode.getClass().getSimpleName() + ") or fragment (" + fragment + ") combination");
    }

    private ModelBindingConfiguration createV10Binding()
    {
        final DefaultModelBindingConfiguration bindingConfiguration = new DefaultModelBindingConfiguration();
        bindingConfiguration.bindPackage(MODEL_PACKAGE);
        // Bind all StringTypes to the StringType implementation they are only marker interfaces
        bindingConfiguration.bind(org.raml.v2.api.model.v10.system.types.StringType.class, StringType.class);
        bindingConfiguration.bind(org.raml.v2.api.model.v10.system.types.ValueType.class, StringType.class);
        bindingConfiguration.defaultTo(DefaultModelElement.class);
        bindingConfiguration.bind(TypeDeclaration.class, new TypeDeclarationModelFactory());
        bindingConfiguration.reverseBindPackage("org.raml.v2.api.model.v10.datamodel");
        return bindingConfiguration;
    }

    private ModelBindingConfiguration createV08Binding()
    {
        final DefaultModelBindingConfiguration bindingConfiguration = new DefaultModelBindingConfiguration();
        bindingConfiguration.bindPackage(MODEL_PACKAGE);
        bindingConfiguration.bind(org.raml.v2.api.model.v08.system.types.StringType.class, StringType.class);
        bindingConfiguration.bind(org.raml.v2.api.model.v08.system.types.ValueType.class, StringType.class);
        bindingConfiguration.bind(Parameter.class, new TypeDeclarationModelFactory());
        bindingConfiguration.reverseBindPackage("org.raml.v2.api.model.v08.parameters");
        bindingConfiguration.defaultTo(DefaultModelElement.class);
        return bindingConfiguration;
    }

    private String getRamlContent(File ramlFile)
    {
        if (ramlFile == null || !ramlFile.isFile())
        {
            return null;
        }
        ResourceLoader fileLoader = new CompositeResourceLoader(resourceLoader, new FileResourceLoader(ramlFile.getParent()));
        return getRamlContent(ramlFile.getName(), fileLoader);
    }

    private String getRamlContent(Reader ramlReader)
    {
        if (ramlReader == null)
        {
            return null;
        }
        try
        {
            return IOUtils.toString(ramlReader);
        }
        catch (IOException e)
        {
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(ramlReader);
        }
    }

    private String getRamlContent(String ramlLocation)
    {
        return getRamlContent(ramlLocation, resourceLoader);
    }

    private String getRamlContent(String ramlLocation, ResourceLoader loader)
    {
        if (ramlLocation == null)
        {
            return null;
        }
        InputStream ramlStream = loader.fetchResource(ramlLocation);
        if (ramlStream != null)
        {
            return StreamUtils.toString(ramlStream);
        }
        return null;
    }

}

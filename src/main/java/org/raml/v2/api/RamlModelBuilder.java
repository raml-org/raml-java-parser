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

import org.apache.commons.io.IOUtils;
import org.raml.v2.api.loader.CompositeResourceLoader;
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.FileResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.internal.framework.model.DefaultModelBindingConfiguration;
import org.raml.v2.internal.framework.model.ModelBindingConfiguration;
import org.raml.v2.internal.framework.model.ModelProxyBuilder;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.RamlVersion;
import org.raml.v2.internal.impl.commons.model.Api;
import org.raml.v2.internal.impl.commons.model.DefaultModelElement;
import org.raml.v2.internal.impl.commons.model.RamlValidationResult;
import org.raml.v2.internal.impl.commons.model.StringType;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.impl.v10.RamlFragment;
import org.raml.v2.internal.utils.StreamUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
        if (!(ramlNode instanceof RamlDocumentNode))
        {
            try
            {
                RamlHeader ramlHeader = RamlHeader.parse(content);
                if (ramlHeader.getVersion() == RamlVersion.RAML_10 && ramlHeader.getFragment() != RamlFragment.Default)
                {
                    return generateRamlApiResult("Raml file is not a root document.");
                }
            }
            catch (RamlHeader.InvalidHeaderException e)
            {
                // ignore, already handled by builder
            }
        }
        return generateRamlApiResult(ramlNode);
    }

    private RamlModelResult generateRamlApiResult(Node ramlNode)
    {
        List<ValidationResult> validationResults = new ArrayList<>();
        if (ramlNode instanceof ErrorNode)
        {
            validationResults.add(new RamlValidationResult((ErrorNode) ramlNode));
        }
        else
        {
            List<ErrorNode> errors = ramlNode.findDescendantsWith(ErrorNode.class);
            for (ErrorNode errorNode : errors)
            {
                validationResults.add(new RamlValidationResult(errorNode));
            }
            if (validationResults.isEmpty())
            {
                return wrapTree((RamlDocumentNode) ramlNode);
            }
        }
        return new RamlModelResult(validationResults);
    }

    private RamlModelResult generateRamlApiResult(String errorMessage)
    {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(new RamlValidationResult(errorMessage));
        return new RamlModelResult(validationResults);
    }

    private RamlModelResult wrapTree(RamlDocumentNode ramlNode)
    {
        if (ramlNode.getVersion() == RamlVersion.RAML_10)
        {
            org.raml.v2.api.model.v10.api.Api apiV10 = ModelProxyBuilder.createModel(org.raml.v2.api.model.v10.api.Api.class, new Api(ramlNode), createV10Binding());
            return new RamlModelResult(apiV10);
        }
        else
        {
            org.raml.v2.api.model.v08.api.Api apiV08 = ModelProxyBuilder.createModel(org.raml.v2.api.model.v08.api.Api.class, new Api(ramlNode), createV08Binding());
            return new RamlModelResult(apiV08);
        }
    }

    private ModelBindingConfiguration createV10Binding()
    {
        final DefaultModelBindingConfiguration bindingConfiguration = new DefaultModelBindingConfiguration();
        bindingConfiguration.bindPackage(MODEL_PACKAGE);
        // Bind all StringTypes to the StringType implementation they are only marker interfaces
        bindingConfiguration.bindInterfaceTo(org.raml.v2.api.model.v10.system.types.StringType.class, StringType.class);
        bindingConfiguration.bindInterfaceTo(org.raml.v2.api.model.v10.system.types.ValueType.class, StringType.class);
        bindingConfiguration.defaultTo(DefaultModelElement.class);
        return bindingConfiguration;
    }

    private ModelBindingConfiguration createV08Binding()
    {
        final DefaultModelBindingConfiguration bindingConfiguration = new DefaultModelBindingConfiguration();
        bindingConfiguration.bindPackage(MODEL_PACKAGE);
        bindingConfiguration.bindInterfaceTo(org.raml.v2.api.model.v08.system.types.StringType.class, StringType.class);
        bindingConfiguration.bindInterfaceTo(org.raml.v2.api.model.v08.system.types.ValueType.class, StringType.class);
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

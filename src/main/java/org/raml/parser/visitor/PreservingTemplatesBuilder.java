package org.raml.parser.visitor;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.Raml2;
import org.raml.model.Resource;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.MappingNode;

public final class PreservingTemplatesBuilder extends RamlDocumentBuilder {
	private final class IncludedResourceOrTraitBuilder<T> extends
			YamlDocumentBuilder<T> {
		private IncludedResourceOrTraitBuilder(Class<T> documentClass,
				ResourceLoader resourceLoader, TagResolver[] tagResolvers) {
			super(documentClass, resourceLoader, tagResolvers);
		}
		@Override
	    public void onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
	    {
	        super.onMappingNodeStart(mappingNode, tupleType);
	        if (getDocumentContext().peek() instanceof Resource)
	        {
	            Resource resource = (Resource) getDocumentContext().peek();
	            getTemplateResolver().resolve(mappingNode, resource.getRelativeUri(), resource.getUri());
	        }
	        else if (isBodyBuilder(getBuilderContext().peek()))
	        {
	            getMediaTypeResolver().resolve(mappingNode);
	        }
	    }
		private boolean isBodyBuilder(NodeBuilder builder)
	    {
	        try
	        {
	            Field valueType = builder.getClass().getDeclaredField("valueClass");
	            valueType.setAccessible(true);
	            return valueType.get(builder) != null && ((Class) valueType.get(builder)).getName().equals("org.raml.model.MimeType");
	        }
	        catch (NoSuchFieldException e)
	        {
	            return false;
	        }
	        catch (IllegalAccessException e)
	        {
	            return false;
	        }
	    }

		@Override
		protected void preBuildProcess() {
			getTemplateResolver().init(getRootNode());
			getMediaTypeResolver().beforeDocumentStart(getRootNode());
		}
	}

	protected TagResolver[] rs;

	public PreservingTemplatesBuilder(ResourceLoader resourceLoader,
			TagResolver[] tagResolvers) {
		super(Raml2.class, resourceLoader, tagResolvers);
		this.rs = tagResolvers;
	}

	public LinkedHashMap<String, Resource> resourceTypes = new LinkedHashMap<String, Resource>();
	public LinkedHashMap<String, Action> traits = new LinkedHashMap<String, Action>();

	@Override
	public Raml2 build(Reader content) {
		Raml2 build = (Raml2) super.build(content);
		Map<String, MappingNode> resourceTypesMap = getTemplateResolver()
				.getResourceTypesMap();
		
		for (String s : resourceTypesMap.keySet()) {
			try{
			MappingNode z = resourceTypesMap.get(s);
			IncludedResourceOrTraitBuilder<Resource> includedResourceOrTraitBuilder = new IncludedResourceOrTraitBuilder<Resource>(
					Resource.class, getResourceLoader(),
					new TagResolver[] { new IncludeResolver() });
			Resource partialType = includedResourceOrTraitBuilder.build(z);
			partialType.setRelativeUri(s);
			resourceTypes.put(s, partialType);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		resourceTypesMap = getTemplateResolver().getTraitsMap();
		for (String s : resourceTypesMap.keySet()) {
			MappingNode z = resourceTypesMap.get(s);
			IncludedResourceOrTraitBuilder<Action> includedResourceOrTraitBuilder = new IncludedResourceOrTraitBuilder<Action>(
					Action.class, getResourceLoader(),
					new TagResolver[] { new IncludeResolver() });
			
			Action partialType = includedResourceOrTraitBuilder.build(z);

			traits.put(s, partialType);
		}
		build.setResourceTypesModel(resourceTypes);
		build.setTraitsModel(traits);
		return build;
	}
}
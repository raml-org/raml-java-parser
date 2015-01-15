package org.raml.parser.rule;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.ContextPath;

/**
 * bridging interface for document builder and validator
 * @author pavel
 *
 */
public interface IParserContext {

	ResourceLoader getResourceLoader();
	
	ContextPath getContextPath();
}

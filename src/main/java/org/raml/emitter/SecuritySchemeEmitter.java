package org.raml.emitter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.Raml2;
import org.raml.model.SecurityScheme;
import org.raml.model.TraitModel;

public class SecuritySchemeEmitter implements IRAMLFieldDumper{

	@Override
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		Raml2 v=(Raml2) pojo;
		List<Map<String, SecurityScheme>> resourceTypeMap = v.getSecuritySchemes();
		if (resourceTypeMap.isEmpty()){
			return;
		}
		dump.append("securitySchemes:\n");
		if (emitter.isSeparated) {
			for (Map<String,SecurityScheme> q : resourceTypeMap) {
				dump.append(emitter.indent(depth + 1));
				dump.append("- ");
				String name = q.keySet().iterator().next();
				dump.append(name);
				dump.append(": ");
				dump.append("!include");
				dump.append(' ');
				dump.append("securitySchemes/");
				dump.append(name);
				dump.append(".raml");
				dump.append("\n");
				StringBuilder content = new StringBuilder();
				emitter.dumpPojo(content, 0, q.values().iterator().next());
				if (emitter.writer != null) {
					emitter.writer.write("securitySchemas/"+name+".raml",content.toString());
				}
			}
		} else {
			dump.append(emitter.indent(depth+1));		
			emitter.dumpSequenceField(dump, depth+1, declaredField, pojo);
		}		
	}

}

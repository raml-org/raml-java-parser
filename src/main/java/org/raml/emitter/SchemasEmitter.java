package org.raml.emitter;

import java.lang.reflect.Field;

import org.raml.model.Raml;
import org.raml.model.Raml2;

public class SchemasEmitter implements IRAMLFieldDumper {

	@Override
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		Raml2 rp=(Raml2) pojo;
		
		if (emitter.isSeparated){
			dump.append("schemas:\n");
			for (String q : rp.getSchemaMap().keySet()) {
				dump.append(emitter.indent(depth + 1));
				dump.append("- ");
				dump.append(q);
				dump.append(": ");
				dump.append("!include");
				dump.append(' ');
				String str = rp.getSchemaMap().get(q);
				dump.append(str);				
				dump.append("\n");
				if (emitter.writer != null) {
					String schemaContent = rp.getSchemaContent(q);
					emitter.writer.write(str,schemaContent);
				}
			}			
		}
		else{
			emitter.dumpSequenceField(dump, depth, declaredField,pojo);
		}
	}

}

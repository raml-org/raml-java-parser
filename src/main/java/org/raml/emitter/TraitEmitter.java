package org.raml.emitter;

import java.lang.reflect.Field;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.Raml2;

public class TraitEmitter implements IRAMLFieldDumper{

	@Override
	public void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo, RamlEmitterV2 emitter) {
		Raml2 v=(Raml2) pojo;
		dump.append("traits:\n");
		Map<String, Action> resourceTypeMap = v.getTraitsModel();
		if (emitter.isSeparated) {
			for (String q : resourceTypeMap.keySet()) {
				dump.append(emitter.indent(depth + 1));
				dump.append("- ");
				dump.append(q);
				dump.append(": ");
				dump.append("!include");
				dump.append(' ');
				dump.append("traits/");
				dump.append(q);
				dump.append(".raml");
				dump.append("\n");
				StringBuilder content = new StringBuilder();
				emitter.dumpPojo(content, 0, resourceTypeMap.get(q));
				if (emitter.writer != null) {
					emitter.writer.write("traits/"+q+".raml",content.toString());
				}
			}
		} else {
		dump.append(emitter.indent(depth+1));		
		emitter.dumpMapInSeq(dump, depth+1, Action.class, resourceTypeMap, false, true);
		}
	}
}

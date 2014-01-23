package org.raml.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

/**
 * Extension to RAML model which keeps information about traits and resource types  
 * @author pavel
 *
 */
public class Raml2 extends Raml {

	private Map<String, ResourceType> resourceTypesModel = new LinkedHashMap<String, ResourceType>();
	private Map<String, TraitModel> traitsModel = new LinkedHashMap<String, TraitModel>();
	private Map<String, String> schemaMap=new LinkedHashMap<String, String>();

    public Map<String, String> getSchemaMap() {
		return schemaMap;
	}

    public String getSchemaContent(String schemaName){
    	for (Map<String,String>q:schemas){
    		if (q.containsKey(schemaName)){
    			return q.get(schemaName);
    		}
    	}
    	return null;
    }
	
	public Map<String, ResourceType> getResourceTypesModel() {
		return resourceTypesModel;
	}

	public void setResourceTypesModel(Map<String, ResourceType> resourceTypesModel) {
		this.resourceTypesModel = resourceTypesModel;
	}

	public Map<String, TraitModel> getTraitsModel() {
		return traitsModel;
	}

	public void setTraitsModel(Map<String, TraitModel> traitsModel) {
		this.traitsModel = traitsModel;
	}

	public void visit(IRamlFileVisitor v) {
		visitResources(this.getResources(), v);
		Map<String, ResourceType> resourceTypesModel2 = getResourceTypesModel();
		for (ResourceType z:resourceTypesModel2.values()){
			v.visitResourceType(z);
			visitResource(v, z);
		}
		for (TraitModel m:getTraitsModel().values()){
			visitAction(v, m);
		}
	}
	
	public void addGlobalSchema(String name,String content,boolean json){
		HashMap<String, String> e = new HashMap<String, String>();
		String path=name;
		if(json){
			path+="-schema.json";
		}
		else{
			path+="-schema.xsd";
		}
		e.put(name, content);
		getSchemaMap().put(name, "/schemas/"+path);
		for (Map<String,String>s:schemas){
			if (s.keySet().contains(name)){
				return;
			}
		}
		schemas.add(e);		
	}
	

	private void visitResources(Map<String, Resource> resourceTypesModel2,
			IRamlFileVisitor v) {
		for (Resource q : resourceTypesModel2.values()) {
			visitResource(v, q);
		}
	}

	private void visitResource(IRamlFileVisitor v, Resource q) {
		boolean startVisit = v.startVisit(q);
		if (startVisit) {
			Map<String, UriParameter> uriParameters = q.getUriParameters();
			for (String u:uriParameters.keySet()){
				v.visit(u,uriParameters.get(u));
			}
			Map<ActionType, Action> actions = q.getActions();
			for (Action a:actions.values()){
				visitAction(v, a);
			}
			visitResources(q.getResources(), v);
		}
		v.endVisit(q);
	}

	private void visitAction(IRamlFileVisitor v, Action a) {
		boolean startVisit2 = v.startVisit(a);
		if (startVisit2){
			Map<String, Header> headers = a.getHeaders();
			for (String h:headers.keySet()){
				v.visit(h,headers.get(h));
			}
			Map<String, QueryParameter> qp = a.getQueryParameters();
			for (String h:qp.keySet()){
				v.visit(h,qp.get(h));
			}
			v.startVisitBody();
			Map<String, MimeType> body = a.getBody();
			for (MimeType m:body.values()){
				v.visit(m);
			}
			v.endVisitBody();
			Map<String, Response> responses = a.getResponses();
			for (String c:responses.keySet()){
				Response response = responses.get(c);
				v.startVisit(c,response);
				for (MimeType m:response.getBody().values()){
					v.visit(m);
				}
				Map<String, Header> headers2 = response.getHeaders();
				for (String h:headers2.keySet()){
					v.visit(h,headers2.get(h));
				}							
				v.endVisit(responses.get(c));
			}
		}
		v.endVisit(a);
	}

}
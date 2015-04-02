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
package org.raml.parser.visitor;

import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class MediaTypeResolver
{

    private static Set<String> MEDIA_TYPE_KEYS;
    private String mediaType;

    static
    {
        String[] keys = {"schema", "example", "formParameters"};
        MEDIA_TYPE_KEYS = new HashSet<String>(Arrays.asList(keys));
    }

    /**
     * checks if there is a default media type declared
     *
     * @param rootNode raml root node
     * @return a list of validation results
     */
    public List<ValidationResult> beforeDocumentStart(MappingNode rootNode)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

        if (rootNode == null)
        {
            validationResults.add(createErrorResult("Invalid Root Node"));
            return validationResults;
        }

        for (NodeTuple tuple : rootNode.getValue())
        {
            if (tuple.getKeyNode().getNodeId() != scalar)
            {
                continue;
            }
            String key = ((ScalarNode) tuple.getKeyNode()).getValue();
            if (key.equals("mediaType"))
            {
                Node valueNode = tuple.getValueNode();
                if (valueNode.getNodeId() != scalar)
                {
                    validationResults.add(createErrorResult("Invalid mediaType", valueNode.getStartMark(), valueNode.getEndMark()));
                    break;
                }
                String value = ((ScalarNode) valueNode).getValue();
                if (!isValidMediaType(value))
                {
                    validationResults.add(createErrorResult("Invalid mediaType", valueNode.getStartMark(), valueNode.getEndMark()));
                    break;
                }
                mediaType = value;
                break;
            }
        }
        return validationResults;
    }

    private boolean isValidMediaType(String value)
    {
        return value.matches(".+/.+");
    }

    /**
     * inject the default media type if an explicit media type is not
     *  declared and the body contains media type child elements
     *  (e.g.: schema)
     *
     * @param bodyNode mapping node with media types or media type child elements
     * @return a list of validation results
     */
    public List<ValidationResult> resolve(MappingNode bodyNode)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        
        if (mediaType!=null){
        	
        	NodeTuple mediaTypeNodeTuple = findNodeByKey(bodyNode, mediaType);
        	
        	//if mediaTypeNode is null create it, but do not add it to the parent node yet
        	if (mediaTypeNodeTuple == null){
     	        Node keyNode = new ScalarNode(Tag.STR, mediaType, null, null, null);
     	        Node valueNode = new MappingNode(Tag.MAP, new ArrayList<NodeTuple>(), false);     	        
     	        NodeTuple newTuple = new NodeTuple(keyNode, valueNode);
     	        mediaTypeNodeTuple = newTuple;
        	}
        	
         	moveOrphanedNodes(mediaTypeNodeTuple, bodyNode);
        	
        	// in case the mediaTypeNodeTuple was not yet appended to the parent node, 
         	// append it but only if it is not empty 
        	if (!bodyNode.getValue().contains(mediaTypeNodeTuple) && 
        			!((MappingNode)mediaTypeNodeTuple.getValueNode()).getValue().isEmpty()){
        		bodyNode.getValue().add(mediaTypeNodeTuple);
        	}
        }
        
        return validationResults;
    }

    /**
     * It is moving all orphaned schema/example/formParameters nodes from the source node under the
     * targetTouple node.
     * 
     * @param targetTouple the target node which should become new parent of orphaned nodes
     * @param sourceNode the current parent node of the possibly orphaned nodes
     */
	private void moveOrphanedNodes(NodeTuple targetTouple, MappingNode sourceNode) {
    	
    	List<NodeTuple> tuplesToRemove = new ArrayList<NodeTuple>();
    	
    	for (NodeTuple sourceTuple : sourceNode.getValue())
        {
    		String mediaTypeKey = ((ScalarNode) sourceTuple.getKeyNode()).getValue();
    		
    		// if it is an orphaned schema/example/formParameters
            if (MEDIA_TYPE_KEYS.contains(mediaTypeKey))
            {
            	// move it to the targetTouple
        		tuplesToRemove.add(sourceTuple);
        		MappingNode targetToupleNode = (MappingNode)targetTouple.getValueNode();
        		NodeTuple existingTuple = findNodeByKey(targetToupleNode, mediaTypeKey);
        		if (existingTuple!=null){
        			targetToupleNode.getValue().remove(existingTuple);
        		}
        		targetToupleNode.getValue().add(sourceTuple);
	        }
    	}
    	
    	// remove the orphaned schema/example/formParameters that were marked for removal earlier
    	for (NodeTuple toRemove: tuplesToRemove){
    		sourceNode.getValue().remove(toRemove);
    	}
	}

	private NodeTuple findNodeByKey(MappingNode parentNode, String key) {
		for (NodeTuple tuple : parentNode.getValue())
		{
			 if (key.equals(((ScalarNode) tuple.getKeyNode()).getValue())){
				return  tuple;
			 }
		}
		return null;
	}

	/**
     * if no explicit media type is defined in either the request or
     * response body, the default one is applied
     *
     * @param resourceMap the resources to be recursively visited
     */
    public void setBodyDefaultMediaType(Map<String, Resource> resourceMap)
    {
        if (mediaType == null)
        {
            //no default media type set
            return;
        }
        for (Resource resource : resourceMap.values())
        {
            Map<ActionType,Action> actionMap = resource.getActions();
            for (Action action : actionMap.values())
            {
                if (action.getBody() != null && action.getBody().isEmpty())
                {
                    action.getBody().put(mediaType, new MimeType(mediaType));
                }
                for (Response response : action.getResponses().values())
                {
                    if (response.getBody() != null && response.getBody().isEmpty())
                    {
                        response.getBody().put(mediaType, new MimeType(mediaType));
                    }
                }
            }
            setBodyDefaultMediaType(resource.getResources());
        }
    }
}

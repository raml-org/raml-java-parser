package org.raml.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.model.parameter.UriParameter;

public class ActionTest {
	
	@Test
	public void hasBody_with_null_body_test() {
		Action action = new Action();
		assertFalse(action.hasBody());
	}
	
	@Test
	public void hasBody_with_empty_body_test() {
		Action action = new Action();
		action.setBody(new HashMap<String, MimeType>());
		assertFalse(action.hasBody());
	}
	
	@Test
	public void hasBody_with_non_null_non_empty_body_test() {
		Map<String, MimeType> body = new HashMap<String, MimeType>();
		body.put("css", new MimeType("text/css"));
		Action action = new Action();
		action.setBody(body);
		assertTrue(action.hasBody());
	}
	
	@Test
	public void getResource_test() {
		Action action = new Action();
		assertNull(action.getResource());
		Resource resource = new Resource();
		resource.setDisplayName("test-resource");
		action.setResource(resource);
		assertEquals("test-resource", action.getResource().getDisplayName());		
	}
	
	@Test
	public void getSecuredBy_test() {
		Action action = new Action();
		assertNotNull(action.getSecuredBy());
		assertTrue(action.getSecuredBy().size() == 0);
		List<SecurityReference> securityReferences = Arrays.asList(new SecurityReference ("OAuth 1.0"), new SecurityReference ("MSN"), new SecurityReference ("Kerberos"));
		action.setSecuredBy(securityReferences);
		assertEquals(3, action.getSecuredBy().size());
		assertEquals("MSN", action.getSecuredBy().get(1).getName());	
	}
	
	@Test
	public void getBaseUriParameters_test() {
		Action action = new Action();
		assertNotNull(action.getBaseUriParameters());
		assertEquals(0, action.getBaseUriParameters().size());
		List<UriParameter> metricParamList = Arrays.asList(new UriParameter("metric"), new UriParameter("measureValue"));
		Map<String, List<UriParameter>> uriParameters = new HashMap<String, List<UriParameter>>();
		uriParameters.put("metric params", metricParamList);
		action.setBaseUriParameters(uriParameters);
		assertNotNull(action.getBaseUriParameters());
		assertEquals(2, action.getBaseUriParameters().get("metric params").size());		
	}
	
	@Test
	public void toString_with_non_null_resource_test() {
		Action action = new Action();
		action.setType(ActionType.POST);
		Resource resource = new Resource();
		resource.setParentUri("/metrics");
		resource.setRelativeUri("/{metricId}");
		action.setResource(resource);
		assertTrue(action.toString().contains("POST"));
		assertTrue(action.toString().contains("/metrics/{metricId}"));
	}
	
	@Test
	public void toString_with_null_resource_test() {
		Action action = new Action();
		action.setType(ActionType.POST);
		assertTrue(action.toString().contains("POST"));
		assertTrue(action.toString().contains("-"));
	}
}

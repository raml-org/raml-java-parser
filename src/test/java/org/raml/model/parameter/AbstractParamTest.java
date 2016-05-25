package org.raml.model.parameter;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractParamTest {

	@Test
	public void message_with_null_type_test() {
		AbstractParam abstractParam = new AbstractParam("test param", null, true);
		abstractParam.setMinLength(100);
		String expected = "Value length is shorter than 100";
		assertEquals(expected, abstractParam.message("test value"));		
	}
}

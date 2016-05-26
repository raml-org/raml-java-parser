package org.raml.parser.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

public class BooleanConverterTest {
	@Test
	public void testConvertWithBooleanValue()  {
		Boolean trueValue = Boolean.TRUE;
		Boolean falseValue = Boolean.FALSE;
		BooleanConverter booleanConverter = new BooleanConverter();
		Object trueValueConverted = booleanConverter.convert(Boolean.class, trueValue);
		Object falseValueConverted = booleanConverter.convert(Boolean.class, falseValue);
		assertTrue(trueValueConverted instanceof Boolean);
		assertTrue(falseValueConverted instanceof Boolean);
		assertEquals(trueValueConverted, trueValue);
		assertEquals(falseValueConverted, falseValue);
	}
	
	@Test
	public void testConvertWithCompatibleNonBooleanValue()  {
		DummyBooleanWrapper trueValue = new DummyBooleanWrapper(Boolean.TRUE);
		DummyBooleanWrapper falseValue =new DummyBooleanWrapper(Boolean.FALSE);
		BooleanConverter booleanConverter = new BooleanConverter();
		Object trueValueConverted = booleanConverter.convert(Boolean.class, trueValue);
		Object falseValueConverted = booleanConverter.convert(Boolean.class, falseValue);
		assertTrue(trueValueConverted instanceof Boolean);
		assertTrue(falseValueConverted instanceof Boolean);
		assertEquals(trueValueConverted, Boolean.TRUE);
		assertEquals(falseValueConverted, Boolean.FALSE);
	}
	
	@Test(expected = ConversionException.class)
	public void testConvertWithIncompatibleNonBooleanValue()  {
		DummyClass trueValue = new DummyClass();		
		BooleanConverter booleanConverter = new BooleanConverter();
		Object trueValueConverted = booleanConverter.convert(Boolean.class, trueValue);
		assertTrue(trueValueConverted instanceof Boolean);		
		assertEquals(trueValueConverted, trueValue);
	}
	
	@Test
	public void testConvertWithToStringYesOrYTrueOrt()  {
		StringValueEmitter trueValue1 = new StringValueEmitter("yes");
		StringValueEmitter trueValue2 = new StringValueEmitter("y");
		StringValueEmitter trueValue3 = new StringValueEmitter("true");
		StringValueEmitter trueValue4 = new StringValueEmitter("t");
		BooleanConverter booleanConverter = new BooleanConverter();
		Object trueValueConverted1 = booleanConverter.convert(Boolean.class, trueValue1);
		Object trueValueConverted2 = booleanConverter.convert(Boolean.class, trueValue2);
		Object trueValueConverted3 = booleanConverter.convert(Boolean.class, trueValue3);
		Object trueValueConverted4 = booleanConverter.convert(Boolean.class, trueValue4);
		assertTrue(trueValueConverted1 instanceof Boolean);		
		assertTrue(trueValueConverted2 instanceof Boolean);
		assertTrue(trueValueConverted3 instanceof Boolean);		
		assertTrue(trueValueConverted4 instanceof Boolean);
		assertEquals(trueValueConverted1, Boolean.TRUE);
		assertEquals(trueValueConverted2, Boolean.TRUE);
		assertEquals(trueValueConverted3, Boolean.TRUE);
		assertEquals(trueValueConverted4, Boolean.TRUE);
	}
	
	@Test
	public void testConvertWithToStringNoOn()  {
		StringValueEmitter falseValue1 = new StringValueEmitter("no");
		StringValueEmitter falseValue2 = new StringValueEmitter("n");
		StringValueEmitter falseValue3 = new StringValueEmitter("false");
		StringValueEmitter falseValue4 = new StringValueEmitter("f");
		BooleanConverter booleanConverter = new BooleanConverter();
		Object falseValueConverte1 = booleanConverter.convert(Boolean.class, falseValue1);
		Object falseValueConverte2 = booleanConverter.convert(Boolean.class, falseValue2);
		Object falseValueConverte3 = booleanConverter.convert(Boolean.class, falseValue3);
		Object falseValueConverte4 = booleanConverter.convert(Boolean.class, falseValue4);
		assertTrue(falseValueConverte1 instanceof Boolean);		
		assertTrue(falseValueConverte2 instanceof Boolean);
		assertTrue(falseValueConverte3 instanceof Boolean);		
		assertTrue(falseValueConverte4 instanceof Boolean);
		assertEquals(falseValueConverte1, Boolean.FALSE);
		assertEquals(falseValueConverte2, Boolean.FALSE);
		assertEquals(falseValueConverte3, Boolean.FALSE);
		assertEquals(falseValueConverte4, Boolean.FALSE);
	}

	
	class DummyBooleanWrapper {
		private Boolean booleanValue;
		public DummyBooleanWrapper(Boolean booleanValue) {
			this.booleanValue = booleanValue;
		}
		public String toString() {
			return booleanValue.toString();
		}
	}
	
	class DummyClass {
		public String toString() {
			return "dummyvalue";
		}
	}
	
	class StringValueEmitter {
		private String emittableValue;
		public StringValueEmitter(String emittableValue) {
			this.emittableValue = emittableValue;
		}
		public String toString() {
			return this.emittableValue;
		}
	}
}

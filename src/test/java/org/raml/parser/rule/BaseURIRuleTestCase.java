/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.raml.parser.rule.ValidationMessage.getRuleEmptyMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.BaseUriRule;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class BaseURIRuleTestCase extends AbstractRamlTestCase
{

    @Test
    public void testBaseURINotEmpty()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                      + "baseUri:";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(getRuleEmptyMessage("baseUri")));
        assertThat(errors.get(1).getMessage(), is("The baseUri element is not a valid URI"));
    }

    @Test
    public void testBaseURIOptional()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "version: v28.0\n" + "title: apiTitle";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testBaseURIisNotValid()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                      + "baseUri: notavaliduri.com";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.URI_NOT_VALID_MESSAGE));
    }

    @Test
    public void testVersionAfterBaseUri()
    {
        String raml = "#%RAML 0.8\n" +
                      "title: x\n" +
                      "baseUri: http://localhost/{version}\n" +
                      "version: v1";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testBaseUriNoVersion()
    {
        String raml = "#%RAML 0.8\n" +
                      "title: x\n" +
                      "baseUri: http://localhost/{version}";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.VERSION_NOT_PRESENT_MESSAGE));
    }
    
    @Test
    public void testGetBaseUriWithFailedValidation() {
    	BaseUriRule baseUriRule = new BaseUriRule();
    	assertNull(baseUriRule.getBaseUri());
    	Tag tag = new Tag("metrics");
    	Mark startMark = new Mark("startMark", 0, 1, 3, "prints a nice error message from here",0 );
    	Mark endMark = new Mark("endMark", 1, 1, 15, "prints a nice error message from here",5 );
    	ScalarNode node = new ScalarNode(tag, "..\\metrics\\datas", startMark, endMark, null);
    	assertEquals("..\\metrics\\datas", node.getValue());
    	baseUriRule.setKeyNode(node);
    	baseUriRule.doValidateValue(node);
    	assertNull(baseUriRule.getBaseUri());
    }
    
    @Test
    public void testGetBaseUriWithSuccessValidation() {
    	BaseUriRule baseUriRule = new BaseUriRule();
    	assertNull(baseUriRule.getBaseUri());
    	Tag tag = new Tag("metrics");
    	Mark startMark = new Mark("startMark", 0, 1, 3, "prints a nice error message from here",0 );
    	Mark endMark = new Mark("endMark", 1, 1, 15, "prints a nice error message from here",5 );
    	ScalarNode node = new ScalarNode(tag, "http://solidmetrics.com/metrics/categories", startMark, endMark, null);
    	assertEquals("http://solidmetrics.com/metrics/categories", node.getValue());
    	baseUriRule.setKeyNode(node);
    	baseUriRule.doValidateValue(node);
    	assertEquals("http://solidmetrics.com/metrics/categories", baseUriRule.getBaseUri());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testGetVersionRule() {
    	BaseUriRule baseUriRule = new BaseUriRule();
    	Tag tag = new Tag("metrics");
    	Mark startMark = new Mark("startMark", 0, 1, 3, "prints a nice error message from here",0 );
    	Mark endMark = new Mark("endMark", 1, 1, 15, "prints a nice error message from here",5 );
    	ScalarNode node = new ScalarNode(tag, "http://solidmetrics.com/metrics/categories", startMark, endMark, null);
    	assertEquals("http://solidmetrics.com/metrics/categories", node.getValue());
    	baseUriRule.setKeyNode(node);
    	Map<String,TupleRule<?, ?> > rules = new HashMap<String, TupleRule<?, ?>>();
    	TupleRule rule = new SimpleRule("version", String.class);
    	rule.setName("versionRule");
    	rules.put("version", rule);
    	TupleRule parentRule = new SimpleRule("version", String.class);
    	baseUriRule.setParentTupleRule(parentRule);
    	parentRule.setNestedRules(rules);
    	assertNotNull(baseUriRule.getVersionRule());
    	assertEquals("versionRule", baseUriRule.getVersionRule().getName());
    }
}

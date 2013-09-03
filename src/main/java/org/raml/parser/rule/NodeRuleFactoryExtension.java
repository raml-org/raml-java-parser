package org.raml.parser.rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface NodeRuleFactoryExtension
{

    boolean handles(Field field, Annotation annotation);

    TupleRule<?,?> createRule(Field field,Annotation annotation);

}

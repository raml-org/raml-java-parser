/**
 * 
 */

package org.raml.parser.utils;

import java.lang.reflect.Field;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.rule.ITupleRule;
import org.raml.parser.rule.SimpleRule;

public enum RuleFactory
{
    INSTANCE;

    public ITupleRule<?, ?> createRuleFor(Field declaredField)
    {
        if (declaredField.isAnnotationPresent(Scalar.class))
        {
            Scalar annotation = declaredField.getAnnotation(Scalar.class);
            return createRule(annotation, declaredField.getName());
        }
        else if (declaredField.isAnnotationPresent(Mapping.class))
        {
            Mapping annotation = declaredField.getAnnotation(Mapping.class);
            return createRule(annotation, declaredField.getName());
        }
        else if (declaredField.isAnnotationPresent(Sequence.class))
        {
            Sequence annotation = declaredField.getAnnotation(Sequence.class);
            return createRule(annotation, declaredField.getName());
        }
        return null;
    }

    private ITupleRule<?, ?> createRule(Sequence annotation, String fieldName)
    {
        ITupleRule<?, ?> iTupleRule;
        if (annotation.rule() != ITupleRule.class)
        {
            try
            {
                iTupleRule = annotation.rule().newInstance();

            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            iTupleRule = createSimpleRule(fieldName, annotation.required());
        }
        return iTupleRule;
    }

    private ITupleRule<?, ?> createRule(Mapping annotation, String fieldName)
    {
        ITupleRule<?, ?> iTupleRule;
        if (annotation.rule() != ITupleRule.class)
        {
            try
            {
                iTupleRule = annotation.rule().newInstance();

            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            iTupleRule = createSimpleRule(fieldName, annotation.required());
        }
        return iTupleRule;
    }

    private ITupleRule<?, ?> createRule(Scalar annotation, String fieldName)
    {
        ITupleRule<?, ?> iTupleRule;
        if (annotation.rule() != ITupleRule.class)
        {
            try
            {
                iTupleRule = annotation.rule().newInstance();

            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            iTupleRule = createSimpleRule(fieldName, annotation.required());
        }
        return iTupleRule;
    }

    public SimpleRule createSimpleRule(String fieldName, boolean required)
    {
        return new SimpleRule(fieldName, required);
    }
}

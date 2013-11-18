/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.emitter;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.utils.ReflectionUtils;

public class YamlEmitter
{

    private static final String INDENTATION = "    ";
    private static final String YAML_SEQ = "- ";
    private static final String YAML_SEQ_START = "[";
    private static final String YAML_SEQ_END = "]";
    private static final String YAML_SEQ_SEP = ", ";
    private static final String YAML_MAP_SEP = ": ";

    public String dump(Object pojo)
    {
        StringBuilder dump = new StringBuilder();
        int depth = 0;
        dumpPojo(dump, depth, pojo);
        return dump.toString();
    }

    private void dumpPojo(StringBuilder dump, int depth, Object pojo)
    {
        final List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojo.getClass());
        pojo.getClass();
        for (Field declaredField : declaredFields)
        {
            declaredField.setAccessible(true);
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);

            if (scalar != null)
            {
                dumpScalar(dump, depth, declaredField, pojo);
            }
            else if (mapping != null)
            {
                dumpMapping(dump, depth, declaredField, mapping.implicit(), pojo);
            }
            else if (sequence != null)
            {
                dumpSequence(dump, depth, declaredField, pojo);
            }
        }
    }

    private Object getFieldValue(Field field, Object pojo)
    {
        try
        {
            return field.get(pojo);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void dumpSequence(StringBuilder dump, int depth, Field field, Object pojo)
    {
        if (!List.class.isAssignableFrom(field.getType()))
        {
            throw new RuntimeException("Only List can be sequence.");
        }

        List seq = (List) getFieldValue(field, pojo);
        if (seq == null || seq.size() == 0)
        {
            return;
        }

        Type type = field.getGenericType();
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Type itemType = pType.getActualTypeArguments()[0];
            dump.append(indent(depth)).append(alias(field)).append(YAML_MAP_SEP);
            dumpSequenceItems(dump, depth, seq, itemType);
        }
    }

    private void dumpSequenceItems(StringBuilder dump, int depth, List seq, Type itemType)
    {
        if (!(itemType instanceof Class<?>))
        {
            //TODO
            throw new RuntimeException("who uses this?");
        }
        else if (ReflectionUtils.isPojo((Class<?>) itemType))
        {
            dump.append("\n");
            for (Object item : seq)
            {
                dump.append(indent(depth + 1)).append(YAML_SEQ).append("\n");
                dumpPojo(dump, depth + 2, item);
            }
        }
        else
        {
            dump.append(YAML_SEQ_START);
            for (int i = 0; i < seq.size(); i++)
            {
                Object item = seq.get(i);
                dump.append(sanitizeScalarValue(depth, item));
                if (i < seq.size() - 1)
                {
                    dump.append(YAML_SEQ_SEP);
                }
            }
            dump.append(YAML_SEQ_END).append("\n");
        }
    }

    private void dumpMapping(StringBuilder dump, int depth, Field field, boolean implicit, Object pojo)
    {
        if (!Map.class.isAssignableFrom(field.getType()))
        {
            throw new RuntimeException("invalid type");
        }

        Map value = (Map) getFieldValue(field, pojo);
        if (value == null || value.isEmpty())
        {
            return;
        }

        Type type = field.getGenericType();
        ParameterizedType pType = (ParameterizedType) type;
        Type valueType = pType.getActualTypeArguments()[1];

        if (valueType instanceof Class<?>)
        {
            if (implicit)
            {
                for (Map.Entry entry : (Set<Map.Entry>) value.entrySet())
                {
                    dump.append(indent(depth)).append(sanitizeScalarValue(depth, entry.getKey()));
                    dump.append(YAML_MAP_SEP).append("\n");
                    dumpPojo(dump, depth + 1, entry.getValue());
                }
            }
            else
            {
                dump.append(indent(depth)).append(alias(field)).append(YAML_MAP_SEP).append("\n");
                for (Map.Entry entry : (Set<Map.Entry>) value.entrySet())
                {
                    dump.append(indent(depth + 1)).append(entry.getKey()).append(YAML_MAP_SEP).append("\n");
                    dumpPojo(dump, depth + 2, entry.getValue());
                }
            }
        }
        else if (valueType instanceof ParameterizedType)
        {
            Type rawType = ((ParameterizedType) valueType).getRawType();
            if (rawType instanceof Class && List.class.isAssignableFrom((Class<?>) rawType))
            {
                Type listType = ((ParameterizedType) valueType).getActualTypeArguments()[0];
                if (listType instanceof Class)
                {
                    dump.append(indent(depth)).append(alias(field)).append(YAML_MAP_SEP).append("\n");
                    for (Map.Entry entry : (Set<Map.Entry>) value.entrySet())
                    {
                        dump.append(indent(depth + 1)).append(entry.getKey()).append(YAML_MAP_SEP);
                        dumpSequenceItems(dump, depth + 2, (List) entry.getValue(), listType);
                    }
                }
            }
        }
        else
        {
            throw new RuntimeException("unexpected value type: " + valueType);
        }

    }

    private void dumpScalar(StringBuilder dump, int depth, Field field, Object pojo)
    {
        try
        {
            Object value = field.get(pojo);
            if (value == null)
            {
                return;
            }
            dump.append(indent(depth)).append(alias(field)).append(YAML_MAP_SEP);
            dump.append(sanitizeScalarValue(depth, value)).append("\n");
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String alias(Field field)
    {
        Scalar scalar = field.getAnnotation(Scalar.class);
        Mapping mapping = field.getAnnotation(Mapping.class);
        Sequence sequence = field.getAnnotation(Sequence.class);

        if (scalar != null && isNotEmpty(scalar.alias()))
        {
            return scalar.alias();
        }
        else if (mapping != null && isNotEmpty(mapping.alias()))
        {
            return mapping.alias();
        }
        else if (sequence != null && isNotEmpty(sequence.alias()))
        {
            return sequence.alias();
        }
        return field.getName();
    }

    private String sanitizeScalarValue(int depth, Object value)
    {
        Class<?> type = value.getClass();
        String result;

        if (type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum()))
        {
            result = String.valueOf(value).toLowerCase();
        }
        else if (String.class.isAssignableFrom(type))
        {
            String text = (String) value;
            if (text.contains("\n"))
            {
                result = blockFormat(depth, text);
            }
            else
            {
                result = inlineFormat(depth, text);
            }
        }
        else
        {
            result = String.valueOf(value);
        }
        return result;
    }

    private String inlineFormat(int depth, String text)
    {
        if (!text.contains("\""))
        {
            return "\"" + text + "\"";
        }
        if (!text.contains("'"))
        {
            return "'" + text + "'";
        }
        return blockFormat(depth, text);
    }

    private String blockFormat(int depth, String text)
    {
        StringBuilder block = new StringBuilder("|\n");
        String[] lines = text.split("\n");
        for (String line : lines)
        {
            block.append(indent(depth + 1)).append(line).append("\n");
        }
        return block.substring(0, block.length() - 1);
    }

    private String indent(int depth)
    {
        return StringUtils.repeat(INDENTATION, depth);
    }
}

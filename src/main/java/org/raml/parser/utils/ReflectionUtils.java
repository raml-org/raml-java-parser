package org.raml.parser.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class ReflectionUtils
{

    private static final Set<Class<?>> WRAPPERS_PLUS_STRING = new HashSet<Class<?>>();

    static
    {
        WRAPPERS_PLUS_STRING.add(Boolean.class);
        WRAPPERS_PLUS_STRING.add(Character.class);
        WRAPPERS_PLUS_STRING.add(Byte.class);
        WRAPPERS_PLUS_STRING.add(Short.class);
        WRAPPERS_PLUS_STRING.add(Integer.class);
        WRAPPERS_PLUS_STRING.add(Long.class);
        WRAPPERS_PLUS_STRING.add(Float.class);
        WRAPPERS_PLUS_STRING.add(Double.class);
        WRAPPERS_PLUS_STRING.add(String.class);
    }

    public static boolean isWrapperOrString(Class<?> type)
    {
        return WRAPPERS_PLUS_STRING.contains(type);
    }

    public static List<Field> getInheritedFields(Class<?> type)
    {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass())
        {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static void setProperty(Object parent, String fieldName, Object value)
    {
        if (parent instanceof List)
        {
            ((List) parent).add(value);
        }
        else
        {
            try
            {
                new PropertyUtilsBean().setProperty(parent, fieldName, value);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
            catch (NoSuchMethodException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isPojo(Class<?> type)
    {
        return !(isWrapperOrString(type) || type.isEnum() || type.isPrimitive());
    }
}

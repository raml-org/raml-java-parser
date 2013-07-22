package org.raml.parser.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.ClassUtils;

public class ConvertUtils
{
    private static BooleanConverter booleanConverter = new BooleanConverter();
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T convertTo(String value, Class<T> type)
    {
        if (type.isEnum())
        {
            return type.cast(Enum.valueOf((Class) type, value.toUpperCase()));
        }
        else
        {
            Class<T> wrapperClass = type;
            if (type.isPrimitive()) {
                wrapperClass = ClassUtils.primitiveToWrapper(type);
            }
            if (wrapperClass.getName().equals(Boolean.class.getName())) {
                return wrapperClass.cast(booleanConverter.convert(Boolean.class, value));
            }
            return wrapperClass.cast(org.apache.commons.beanutils.ConvertUtils.convert(value, type));
        }
    }

    public static boolean canBeConverted(String value, Class<?> type)
    {
        if (type.isEnum())
        {
            Object[] enumConstants = type.getEnumConstants();
            for (Object enumConstant : enumConstants)
            {
                if (enumConstant.toString().equals(value.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
        else if (type.isInstance(value)) {
            return true;
        } else {
            try {
                Class<?> wrapperClass = ClassUtils.primitiveToWrapper(type);
                convertTo(value, wrapperClass);
                return true;
            } catch (ClassCastException e) {
                return false;
            } catch (ConversionException e) {
                return false;
            }
        }
    }
}

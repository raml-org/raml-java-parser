package org.raml.parser.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class ConvertUtils
{
    private static List<Converter> converters = new ArrayList<Converter>();
    
    static {
        initializeConverters();
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T convertTo(String value, Class<T> type)
    {
        if (type.isEnum())
        {
            return type.cast(Enum.valueOf((Class) type, value.toUpperCase()));
        }
        else
        {
            return type.cast(org.apache.commons.beanutils.ConvertUtils.convert(value, type));
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
        } else if (type.equals(Boolean.class)) {
            try {
                convertTo(value, type);
                return true;
            } catch (ClassCastException e) {
                return false;
            } catch (ConversionException e) {
                return false;
            }
        }
        return false;
    }
    
    private static void initializeConverters()
    {
        BooleanConverter booleanConverter = new BooleanConverter();
        converters.add(booleanConverter);
        org.apache.commons.beanutils.ConvertUtils.register(booleanConverter, Boolean.class);
        org.apache.commons.beanutils.ConvertUtils.register(booleanConverter, Boolean.TYPE);
    }
}

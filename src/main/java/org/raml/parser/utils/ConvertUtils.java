package org.raml.parser.utils;

public class ConvertUtils
{

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

}

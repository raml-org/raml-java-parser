/**
 * 
 */

package org.raml.parser.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class BooleanConverter implements Converter
{

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class,
     * java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object convert(Class type, Object value)
    {
        if (value instanceof Boolean)
        {
            return (value);
        }
        try
        {
            String stringValue = value.toString();
            if (stringValue.equalsIgnoreCase("yes") || stringValue.equals("y")
                || stringValue.equalsIgnoreCase("true") || stringValue.equals("t"))
            {
                return (Boolean.TRUE);
            }
            else if (stringValue.equalsIgnoreCase("no") || stringValue.equals("n")
                     || stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("f"))
            {
                return (Boolean.FALSE);
            }
            else
            {
                throw new ConversionException(stringValue);
            }
        }
        catch (ClassCastException e)
        {
            throw new ConversionException(e);
        }
    }
}

package org.raml.model;

import org.raml.model.parameter.AbstractParam;

public enum ParamType
{
    STRING
            {
                @Override
                public boolean validate(AbstractParam param, String value)
                {
                    if (param.getPattern() != null && !value.matches(param.getPattern()))
                    {
                        return false;
                    }
                    if (param.getMinLength() != null && value.length() < param.getMinLength())
                    {
                        return false;
                    }
                    if (param.getMaxLength() != null && value.length() > param.getMaxLength())
                    {
                        return false;
                    }
                    if (param.getEnumeration() != null && !param.getEnumeration().contains(value))
                    {
                        return false;
                    }
                    return true;
                }
            },
    NUMBER
            {
                @Override
                public boolean validate(AbstractParam param, String value)
                {
                    Double number;
                    try
                    {
                        number = Double.parseDouble(value);
                    }
                    catch (NumberFormatException nfe)
                    {
                        return false;
                    }
                    if (param.getMinimum() != null && number < param.getMinimum())
                    {
                        return false;
                    }
                    if (param.getMaximum() != null && number > param.getMaximum())
                    {
                        return false;
                    }
                    return true;
                }
            },
    INTEGER
            {
                @Override
                public boolean validate(AbstractParam param, String value)
                {
                    Integer number;
                    try
                    {
                        number = Integer.parseInt(value);
                    }
                    catch (NumberFormatException nfe)
                    {
                        return false;
                    }
                    if (param.getMinimum() != null && number < param.getMinimum())
                    {
                        return false;
                    }
                    if (param.getMaximum() != null && number > param.getMaximum())
                    {
                        return false;
                    }
                    return true;                }
            },
    DATE, //TODO add date validation
    FILE,
    BOOLEAN
            {
                @Override
                public boolean validate(AbstractParam param, String value)
                {
                    return "true".equals(value) || "false".equals(value);
                }
            };

    public boolean validate(AbstractParam param, String value)
    {
        return true;
    }
}

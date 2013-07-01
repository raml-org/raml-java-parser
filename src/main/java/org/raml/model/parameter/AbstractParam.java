package org.raml.model.parameter;

import java.util.List;

import org.raml.model.ParamType;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractParam
{

    @Scalar
    private String name;

    @Scalar
    private String description;

    @Scalar
    private ParamType type;

    @Scalar
    private boolean required;

    @Sequence(alias = "enum")
    private List<String> enumeration;
    @Scalar
    private String pattern;
    @Scalar
    private Integer minLength;
    @Scalar
    private Integer maxLength;
    @Scalar
    private Double minimum;
    @Scalar
    private Double maximum;

    @Scalar(alias = "default")
    private String defaultValue;

    @Scalar
    private String example;

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setType(ParamType type)
    {
        this.type = type;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public ParamType getType()
    {
        return type;
    }

    public boolean isRequired()
    {
        return required;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getExample()
    {
        return example;
    }

    public List<String> getEnumeration()
    {
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration)
    {
        this.enumeration = enumeration;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    public Integer getMinLength()
    {
        return minLength;
    }

    public void setMinLength(Integer minLength)
    {
        this.minLength = minLength;
    }

    public Integer getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength)
    {
        this.maxLength = maxLength;
    }

    public Double getMinimum()
    {
        return minimum;
    }

    public void setMinimum(Double minimum)
    {
        this.minimum = minimum;
    }

    public Double getMaximum()
    {
        return maximum;
    }

    public void setMaximum(Double maximum)
    {
        this.maximum = maximum;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public void setExample(String example)
    {
        this.example = example;
    }

    public boolean validate(String value)
    {
        //TODO refactor validations to enforce typing
        switch (type)
        {
            case STRING:
                if (pattern != null && !value.matches(pattern)) return false;
                if (minLength != null && value.length() < minLength) return false;
                if (maxLength != null && value.length() > maxLength) return false;
                if (enumeration != null && !enumeration.contains(value)) return false;
                break;
            case INTEGER:
            case NUMBER:
                Double number = Double.valueOf(value);
                if (minimum != null && number < minimum) return false;
                if (maximum != null && number > maximum) return false;
                break;
        }
        return true;
    }
}

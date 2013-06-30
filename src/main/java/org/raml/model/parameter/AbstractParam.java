package org.raml.model.parameter;

import java.util.List;
import java.util.Map;

import org.raml.model.ParamType;
import org.raml.model.validation.Validation;
import org.raml.model.validation.ValidationType;
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

    //@Mapping(implicit = true)
    //private Map<ValidationType, Validation> validations = new HashMap<ValidationType, Validation>();
    //TODO hack till Mappings with interface types are supported

    @Sequence(alias = "enum")
    private List<String> enumeration;
    @Scalar
    private String pattern;
    @Scalar
    private Integer minLength;
    @Scalar
    private Integer maxLength;
    @Scalar
    private Integer minimum;
    @Scalar
    private Integer maximum;

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

    public Integer getMinimum()
    {
        return minimum;
    }

    public void setMinimum(Integer minimum)
    {
        this.minimum = minimum;
    }

    public Integer getMaximum()
    {
        return maximum;
    }

    public void setMaximum(Integer maximum)
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

    public Map<ValidationType, Validation> getValidations()
    {
        //TODO
        return null;
    }

    public boolean validate(String value)
    {
        //for (Validation validation : validations.values())
        //{
        //    if (!validation.check(value))
        //    {
        //        if (logger.isInfoEnabled())
        //        {
        //            logger.info(String.format("Validation %s failed for value %s", validation, value));
        //        }
        //        return false;
        //    }
        //}
        return true;
    }
}

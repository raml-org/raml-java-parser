package org.raml.parser.completion;

/**
 *
 */
public class DefaultSuggestion implements Suggestion
{

    private String text;

    public DefaultSuggestion(String text)
    {
        this.text = text;
    }

    @Override
    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return "DefaultSuggestion{" +
               "text='" + text + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        DefaultSuggestion that = (DefaultSuggestion) o;

        if (text != null ? !text.equals(that.text) : that.text != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return text != null ? text.hashCode() : 0;
    }
}

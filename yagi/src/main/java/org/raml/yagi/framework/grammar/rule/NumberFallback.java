package org.raml.yagi.framework.grammar.rule;

/**
 * Created. There, you have it.
 */
public class NumberFallback {
    private static final String CAST_STRINGS_AS_NUMBERS_PROP = "org.raml.cast_strings_as_number";
    public static boolean CAST_STRINGS_AS_NUMBERS = Boolean.parseBoolean(System.getProperty(CAST_STRINGS_AS_NUMBERS_PROP, "false"));
}

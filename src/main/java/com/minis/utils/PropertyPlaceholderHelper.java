package com.minis.utils;

import java.util.Properties;

public class PropertyPlaceholderHelper {

    private final PlaceholderParser parser;


    /**
     * Create a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
     * Unresolvable placeholders are ignored.
     * @param placeholderPrefix the prefix that denotes the start of a placeholder
     * @param placeholderSuffix the suffix that denotes the end of a placeholder
     */
    public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
        this(placeholderPrefix, placeholderSuffix, null, null, true);
    }

    /**
     * Create a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
     * @param placeholderPrefix the prefix that denotes the start of a placeholder
     * @param placeholderSuffix the suffix that denotes the end of a placeholder
     * @param valueSeparator the separating character between the placeholder variable
     * and the associated default value, if any
     * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should
     * be ignored ({@code true}) or cause an exception ({@code false})
     * @deprecated as of 6.2, in favor of
     * {@link PropertyPlaceholderHelper#PropertyPlaceholderHelper(String, String, String, Character, boolean)}
     */
    @Deprecated(since = "6.2", forRemoval = true)
    public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix,
                                     String valueSeparator, boolean ignoreUnresolvablePlaceholders) {

        this(placeholderPrefix, placeholderSuffix, valueSeparator, null, ignoreUnresolvablePlaceholders);
    }

    /**
     * Create a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
     * @param placeholderPrefix the prefix that denotes the start of a placeholder
     * @param placeholderSuffix the suffix that denotes the end of a placeholder
     * @param valueSeparator the separating character between the placeholder variable
     * and the associated default value, if any
     * @param escapeCharacter the escape character to use to ignore placeholder prefix
     * or value separator, if any
     * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should
     * be ignored ({@code true}) or cause an exception ({@code false})
     * @since 6.2
     */
    public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix,
                                     String valueSeparator, Character escapeCharacter,
                                     boolean ignoreUnresolvablePlaceholders) {

        this.parser = new PlaceholderParser(placeholderPrefix, placeholderSuffix,
                valueSeparator, escapeCharacter, ignoreUnresolvablePlaceholders);
    }


    /**
     * Replace all placeholders of format {@code ${name}} with the corresponding
     * property from the supplied {@link Properties}.
     * @param value the value containing the placeholders to be replaced
     * @param properties the {@code Properties} to use for replacement
     * @return the supplied value with placeholders replaced inline
     */
    public String replacePlaceholders(String value, final Properties properties) {
        return replacePlaceholders(value, properties::getProperty);
    }

    /**
     * Replace all placeholders of format {@code ${name}} with the value returned
     * from the supplied {@link PlaceholderResolver}.
     * @param value the value containing the placeholders to be replaced
     * @param placeholderResolver the {@code PlaceholderResolver} to use for replacement
     * @return the supplied value with placeholders replaced inline
     */
    public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
        return parseStringValue(value, placeholderResolver);
    }

    protected String parseStringValue(String value, PlaceholderResolver placeholderResolver) {
        return this.parser.replacePlaceholders(value, placeholderResolver);
    }


    /**
     * Strategy interface used to resolve replacement values for placeholders contained in Strings.
     */
    @FunctionalInterface
    public interface PlaceholderResolver {

        /**
         * Resolve the supplied placeholder name to the replacement value.
         * @param placeholderName the name of the placeholder to resolve
         * @return the replacement value, or {@code null} if no replacement is to be made
         */
        String resolvePlaceholder(String placeholderName);
    }
}

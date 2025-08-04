package com.minis.utils;

public abstract class SystemPropertyUtils {

    public static final String PLACEHOLDER_PREFIX = "${";

    /** Suffix for property placeholders: {@value}. */
    public static final String PLACEHOLDER_SUFFIX = "}";

    /** Value separator for property placeholders: {@value}. */
    public static final String VALUE_SEPARATOR = ":";

    /**
     * Escape character for property placeholders: {@code '\'}.
     * @since 6.2
     */
    public static final Character ESCAPE_CHARACTER = '\\';


    private static final PropertyPlaceholderHelper strictHelper =
            new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR,
                    ESCAPE_CHARACTER, false);

    private static final PropertyPlaceholderHelper nonStrictHelper =
            new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR,
                    ESCAPE_CHARACTER, true);


    /**
     * Resolve {@code ${...}} placeholders in the given text, replacing them with
     * corresponding system property values.
     * @param text the String to resolve
     * @return the resolved String
     * @throws IllegalArgumentException if there is an unresolvable placeholder
     * @see #PLACEHOLDER_PREFIX
     * @see #PLACEHOLDER_SUFFIX
     */
    public static String resolvePlaceholders(String text) {
        return resolvePlaceholders(text, false);
    }

    /**
     * Resolve {@code ${...}} placeholders in the given text, replacing them with
     * corresponding system property values. Unresolvable placeholders with no default
     * value are ignored and passed through unchanged if the flag is set to {@code true}.
     * @param text the String to resolve
     * @param ignoreUnresolvablePlaceholders whether unresolved placeholders are to be ignored
     * @return the resolved String
     * @throws IllegalArgumentException if there is an unresolvable placeholder
     * @see #PLACEHOLDER_PREFIX
     * @see #PLACEHOLDER_SUFFIX
     * and the "ignoreUnresolvablePlaceholders" flag is {@code false}
     */
    public static String resolvePlaceholders(String text, boolean ignoreUnresolvablePlaceholders) {
        if (text.isEmpty()) {
            return text;
        }
        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
        return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
    }


    /**
     * PlaceholderResolver implementation that resolves against system properties
     * and system environment variables.
     */
    private static class SystemPropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final String text;

        public SystemPropertyPlaceholderResolver(String text) {
            this.text = text;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            try {
                String propVal = System.getProperty(placeholderName);
                if (propVal == null) {
                    // Fall back to searching the system environment.
                    propVal = System.getenv(placeholderName);
                }
                return propVal;
            }
            catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" +
                        this.text + "] as system property: " + ex);
                return null;
            }
        }
    }
}

package com.rendertemplate.renderer;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for resolving {@code {{placeholder}}} expressions in template text.
 */
public final class PlaceholderResolver {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    private PlaceholderResolver() {}

    /**
     * Replaces all {@code {{key}}} placeholders in the text with values from the data map.
     * Missing keys resolve to an empty string.
     *
     * @param text      the text containing placeholders
     * @param data      the data map for substitution
     * @param escaper   a function applied to each resolved value (e.g. HTML escaping)
     * @return the text with placeholders replaced
     */
    public static String resolve(String text, Map<String, Object> data, UnaryOperator<String> escaper) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = data.getOrDefault(key, "");
            String replacement = escaper.apply(String.valueOf(value));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Replaces placeholders without any escaping.
     */
    public static String resolve(String text, Map<String, Object> data) {
        return resolve(text, data, UnaryOperator.identity());
    }
}

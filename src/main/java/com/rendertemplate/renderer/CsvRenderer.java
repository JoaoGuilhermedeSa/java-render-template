package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renderer that converts templates to CSV format.
 *
 * <p>The template content uses {@code {{placeholder}}} syntax for variable substitution.
 * The first line of the content is treated as the header row. Subsequent lines are treated
 * as a row template.</p>
 *
 * <p>If the template data contains a {@code "rows"} key with a {@code List<Map<String, Object>>},
 * the row template is repeated for each entry. Otherwise, the template data itself is used
 * for a single-row substitution.</p>
 *
 * <p>Values containing commas, double quotes, or newlines are escaped per RFC 4180.</p>
 */
public class CsvRenderer implements TemplateRenderer {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    @Override
    public RenderResult render(Template template) {
        String content = template.getContent();
        if (content == null || content.isBlank()) {
            throw new RenderException("Template content cannot be empty for CSV rendering");
        }

        String[] lines = content.split("\\r?\\n", 2);
        String headerLine = lines[0];

        StringBuilder result = new StringBuilder();
        result.append(replacePlaceholders(headerLine, template.getData()));

        if (lines.length > 1) {
            String rowTemplate = lines[1];
            Object rowsData = template.getData("rows");

            if (rowsData instanceof List<?> rows) {
                for (Object row : rows) {
                    if (row instanceof Map<?, ?> rowMap) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> typedRow = (Map<String, Object>) rowMap;
                        result.append("\n").append(replacePlaceholders(rowTemplate, typedRow));
                    }
                }
            } else {
                result.append("\n").append(replacePlaceholders(rowTemplate, template.getData()));
            }
        }

        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        return new RenderResult(bytes, RenderFormat.CSV, template.getName());
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.CSV;
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = data.getOrDefault(key, "");
            String replacement = escapeCsvValue(String.valueOf(value));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

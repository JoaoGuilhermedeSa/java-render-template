package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import com.rendertemplate.model.TemplateSection;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Renderer that converts templates to CSV format.
 *
 * <p>Only {@code Table} sections are rendered. {@code Heading} and {@code Paragraph} sections
 * are ignored. If no {@code Table} section exists, a {@link RenderException} is thrown.</p>
 *
 * <p>Values containing commas, double quotes, or newlines are escaped per RFC 4180.</p>
 */
public class CsvRenderer implements TemplateRenderer {

    @Override
    public RenderResult render(Template template) {
        TemplateSection.Table table = template.getSections().stream()
                .filter(s -> s instanceof TemplateSection.Table)
                .map(s -> (TemplateSection.Table) s)
                .findFirst()
                .orElseThrow(() -> new RenderException("CSV rendering requires at least one Table section"));

        StringBuilder result = new StringBuilder();

        // Header row
        for (int i = 0; i < table.headers().size(); i++) {
            if (i > 0) result.append(",");
            result.append(escapeCsvValue(table.headers().get(i)));
        }

        // Data rows
        List<Map<String, Object>> rows = extractRows(template);
        for (Map<String, Object> row : rows) {
            result.append("\n");
            for (int i = 0; i < table.columns().size(); i++) {
                if (i > 0) result.append(",");
                String resolved = PlaceholderResolver.resolve(table.columns().get(i), row);
                result.append(escapeCsvValue(resolved));
            }
        }

        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        return new RenderResult(bytes, RenderFormat.CSV, template.getName());
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.CSV;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRows(Template template) {
        Object rowsData = template.getData("rows");
        if (rowsData instanceof List<?> rows) {
            return rows.stream()
                    .filter(r -> r instanceof Map)
                    .map(r -> (Map<String, Object>) r)
                    .toList();
        }
        return List.of(template.getData());
    }

    String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import com.rendertemplate.model.TemplateSection;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Renderer that converts templates to HTML format.
 *
 * <p>Sections are rendered as follows:
 * <ul>
 *   <li>{@code Heading} &rarr; {@code <h1>}</li>
 *   <li>{@code Paragraph} &rarr; {@code <p>}</li>
 *   <li>{@code Table} &rarr; {@code <table>} with thead/tbody</li>
 * </ul>
 * Output is wrapped in {@code <html><body>...</body></html>}.
 * All substituted values are HTML-escaped to prevent XSS.</p>
 */
public class HtmlRenderer implements TemplateRenderer {

    @Override
    public RenderResult render(Template template) {
        if (template.getSections().isEmpty()) {
            throw new RenderException("Template must have at least one section for HTML rendering");
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");

        for (TemplateSection section : template.getSections()) {
            switch (section) {
                case TemplateSection.Heading h -> {
                    String resolved = PlaceholderResolver.resolve(h.text(), template.getData(), this::escapeHtml);
                    html.append("<h1>").append(resolved).append("</h1>");
                }
                case TemplateSection.Paragraph p -> {
                    String resolved = PlaceholderResolver.resolve(p.text(), template.getData(), this::escapeHtml);
                    html.append("<p>").append(resolved).append("</p>");
                }
                case TemplateSection.Table t -> {
                    html.append("<table>");
                    html.append("<thead><tr>");
                    for (String header : t.headers()) {
                        html.append("<th>").append(escapeHtml(header)).append("</th>");
                    }
                    html.append("</tr></thead>");
                    html.append("<tbody>");
                    List<Map<String, Object>> rows = extractRows(template);
                    for (Map<String, Object> row : rows) {
                        html.append("<tr>");
                        for (String col : t.columns()) {
                            String resolved = PlaceholderResolver.resolve(col, row, this::escapeHtml);
                            html.append("<td>").append(resolved).append("</td>");
                        }
                        html.append("</tr>");
                    }
                    html.append("</tbody>");
                    html.append("</table>");
                }
            }
        }

        html.append("</body></html>");

        byte[] bytes = html.toString().getBytes(StandardCharsets.UTF_8);
        return new RenderResult(bytes, RenderFormat.HTML, template.getName());
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.HTML;
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

    protected String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

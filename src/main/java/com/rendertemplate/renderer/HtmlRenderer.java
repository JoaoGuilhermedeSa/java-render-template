package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renderer that converts templates to HTML format.
 *
 * <p>The template content uses {@code {{placeholder}}} syntax for variable substitution.
 * All substituted values are HTML-escaped to prevent XSS.</p>
 */
public class HtmlRenderer implements TemplateRenderer {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    @Override
    public RenderResult render(Template template) {
        String content = template.getContent();
        if (content == null || content.isBlank()) {
            throw new RenderException("Template content cannot be empty for HTML rendering");
        }

        String rendered = replacePlaceholders(content, template.getData());
        byte[] bytes = rendered.getBytes(StandardCharsets.UTF_8);
        return new RenderResult(bytes, RenderFormat.HTML, template.getName());
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.HTML;
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = data.getOrDefault(key, "");
            String replacement = escapeHtml(String.valueOf(value));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
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

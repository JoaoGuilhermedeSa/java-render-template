package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlRendererTest {

    private HtmlRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new HtmlRenderer();
    }

    @Test
    void shouldReturnHtmlFormat() {
        assertEquals(RenderFormat.HTML, renderer.getFormat());
    }

    @Test
    void shouldSupportHtmlFormat() {
        assertTrue(renderer.supports(RenderFormat.HTML));
        assertFalse(renderer.supports(RenderFormat.CSV));
        assertFalse(renderer.supports(RenderFormat.PDF));
    }

    @Test
    void shouldRenderHeadingWithPlaceholders() {
        Template template = Template.builder()
                .name("page")
                .heading("{{title}}")
                .data("title", "Hello")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("<html><body><h1>Hello</h1></body></html>", result.contentAsString());
        assertEquals(RenderFormat.HTML, result.format());
        assertEquals("page", result.templateName());
    }

    @Test
    void shouldRenderParagraphWithPlaceholders() {
        Template template = Template.builder()
                .name("page")
                .paragraph("{{greeting}}, {{name}}!")
                .data("greeting", "Hello")
                .data("name", "World")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("<html><body><p>Hello, World!</p></body></html>", result.contentAsString());
    }

    @Test
    void shouldRenderTableWithRows() {
        Template template = Template.builder()
                .name("report")
                .table(List.of("Name", "Age"), List.of("{{name}}", "{{age}}"))
                .data("rows", List.of(
                        Map.of("name", "Alice", "age", 30),
                        Map.of("name", "Bob", "age", 25)
                ))
                .build();

        RenderResult result = renderer.render(template);
        String html = result.contentAsString();

        assertTrue(html.contains("<thead><tr><th>Name</th><th>Age</th></tr></thead>"));
        assertTrue(html.contains("<td>Alice</td><td>30</td>"));
        assertTrue(html.contains("<td>Bob</td><td>25</td>"));
        assertTrue(html.startsWith("<html><body>"));
        assertTrue(html.endsWith("</body></html>"));
    }

    @Test
    void shouldRenderTableWithSingleRowFromData() {
        Template template = Template.builder()
                .name("report")
                .table(List.of("Name"), List.of("{{name}}"))
                .data("name", "Alice")
                .build();

        RenderResult result = renderer.render(template);
        String html = result.contentAsString();

        assertTrue(html.contains("<td>Alice</td>"));
    }

    @Test
    void shouldRenderMultipleSections() {
        Template template = Template.builder()
                .name("page")
                .heading("{{title}}")
                .paragraph("Generated for {{company}}")
                .table(List.of("Name"), List.of("{{name}}"))
                .data("title", "Report")
                .data("company", "Acme")
                .data("rows", List.of(Map.of("name", "Alice")))
                .build();

        RenderResult result = renderer.render(template);
        String html = result.contentAsString();

        assertTrue(html.contains("<h1>Report</h1>"));
        assertTrue(html.contains("<p>Generated for Acme</p>"));
        assertTrue(html.contains("<td>Alice</td>"));
    }

    @Test
    void shouldEscapeHtmlInValues() {
        Template template = Template.builder()
                .name("page")
                .paragraph("{{content}}")
                .data("content", "<script>alert('xss')</script>")
                .build();

        RenderResult result = renderer.render(template);

        assertTrue(result.contentAsString().contains("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;"));
    }

    @Test
    void shouldEscapeAmpersands() {
        Template template = Template.builder()
                .name("page")
                .paragraph("{{text}}")
                .data("text", "Tom & Jerry")
                .build();

        RenderResult result = renderer.render(template);

        assertTrue(result.contentAsString().contains("Tom &amp; Jerry"));
    }

    @Test
    void shouldEscapeDoubleQuotes() {
        Template template = Template.builder()
                .name("page")
                .paragraph("{{val}}")
                .data("val", "say \"hi\"")
                .build();

        RenderResult result = renderer.render(template);

        assertTrue(result.contentAsString().contains("say &quot;hi&quot;"));
    }

    @Test
    void shouldLeaveMissingPlaceholdersEmpty() {
        Template template = Template.builder()
                .name("page")
                .paragraph("{{missing}}")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("<html><body><p></p></body></html>", result.contentAsString());
    }

    @Test
    void shouldRenderStaticHeading() {
        Template template = Template.builder()
                .name("static")
                .heading("No placeholders")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("<html><body><h1>No placeholders</h1></body></html>", result.contentAsString());
    }

    @Test
    void shouldProduceSuggestedHtmlFilename() {
        Template template = Template.builder()
                .name("report")
                .heading("hello")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("report.html", result.suggestedFilename());
    }

    @Test
    void shouldEscapeHtmlSpecialChars() {
        assertEquals("&amp;", renderer.escapeHtml("&"));
        assertEquals("&lt;", renderer.escapeHtml("<"));
        assertEquals("&gt;", renderer.escapeHtml(">"));
        assertEquals("&quot;", renderer.escapeHtml("\""));
        assertEquals("&#39;", renderer.escapeHtml("'"));
    }

    @Test
    void shouldNotEscapePlainText() {
        assertEquals("hello world", renderer.escapeHtml("hello world"));
    }

    @Test
    void shouldEscapeHtmlInTableValues() {
        Template template = Template.builder()
                .name("page")
                .table(List.of("Data"), List.of("{{val}}"))
                .data("val", "<b>bold</b>")
                .build();

        RenderResult result = renderer.render(template);

        assertTrue(result.contentAsString().contains("&lt;b&gt;bold&lt;/b&gt;"));
    }
}

package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PdfRendererTest {

    private PdfRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new PdfRenderer();
    }

    @Test
    void shouldReturnPdfFormat() {
        assertEquals(RenderFormat.PDF, renderer.getFormat());
    }

    @Test
    void shouldSupportPdfFormat() {
        assertTrue(renderer.supports(RenderFormat.PDF));
        assertFalse(renderer.supports(RenderFormat.HTML));
        assertFalse(renderer.supports(RenderFormat.CSV));
    }

    @Test
    void shouldRenderHeadingWithPlaceholders() throws IOException {
        Template template = Template.builder()
                .name("doc")
                .heading("Hello {{name}}")
                .data("name", "World")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals(RenderFormat.PDF, result.format());
        assertEquals("doc", result.templateName());

        String text = extractText(result.content());
        assertTrue(text.contains("Hello World"));
    }

    @Test
    void shouldRenderParagraphWithPlaceholders() throws IOException {
        Template template = Template.builder()
                .name("doc")
                .paragraph("{{greeting}}, {{name}}!")
                .data("greeting", "Hello")
                .data("name", "World")
                .build();

        RenderResult result = renderer.render(template);
        String text = extractText(result.content());

        assertTrue(text.contains("Hello, World!"));
    }

    @Test
    void shouldRenderMultipleSections() throws IOException {
        Template template = Template.builder()
                .name("doc")
                .heading("{{title}}")
                .paragraph("By {{author}}")
                .data("title", "Report")
                .data("author", "Alice")
                .build();

        RenderResult result = renderer.render(template);
        String text = extractText(result.content());

        assertTrue(text.contains("Report"));
        assertTrue(text.contains("By Alice"));
    }

    @Test
    void shouldRenderTableSection() throws IOException {
        Template template = Template.builder()
                .name("doc")
                .table(List.of("Name", "Age"), List.of("{{name}}", "{{age}}"))
                .data("rows", List.of(
                        Map.of("name", "Alice", "age", 30),
                        Map.of("name", "Bob", "age", 25)
                ))
                .build();

        RenderResult result = renderer.render(template);
        String text = extractText(result.content());

        assertTrue(text.contains("Name"));
        assertTrue(text.contains("Age"));
        assertTrue(text.contains("Alice"));
        assertTrue(text.contains("Bob"));
    }

    @Test
    void shouldLeaveMissingPlaceholdersEmpty() throws IOException {
        Template template = Template.builder()
                .name("doc")
                .paragraph("Value: {{missing}}")
                .build();

        RenderResult result = renderer.render(template);
        String text = extractText(result.content());

        assertTrue(text.contains("Value:"));
    }

    @Test
    void shouldRenderStaticContent() throws IOException {
        Template template = Template.builder()
                .name("static")
                .paragraph("No placeholders here")
                .build();

        RenderResult result = renderer.render(template);
        String text = extractText(result.content());

        assertTrue(text.contains("No placeholders here"));
    }

    @Test
    void shouldProduceSuggestedPdfFilename() {
        Template template = Template.builder()
                .name("report")
                .heading("Some content")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("report.pdf", result.suggestedFilename());
    }

    @Test
    void shouldProduceValidPdfBytes() {
        Template template = Template.builder()
                .name("doc")
                .heading("Test PDF")
                .build();

        RenderResult result = renderer.render(template);
        byte[] bytes = result.content();

        assertTrue(bytes.length > 4);
        assertEquals('%', (char) bytes[0]);
        assertEquals('P', (char) bytes[1]);
        assertEquals('D', (char) bytes[2]);
        assertEquals('F', (char) bytes[3]);
    }

    @Test
    void shouldRenderTableWithSingleRowFromData() throws IOException {
        Template template = Template.builder()
                .name("doc")
                .table(List.of("Name"), List.of("{{name}}"))
                .data("name", "Alice")
                .build();

        RenderResult result = renderer.render(template);
        String text = extractText(result.content());

        assertTrue(text.contains("Name"));
        assertTrue(text.contains("Alice"));
    }

    private String extractText(byte[] pdfBytes) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }
}

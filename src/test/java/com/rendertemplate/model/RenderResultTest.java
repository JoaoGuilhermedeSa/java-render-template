package com.rendertemplate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderResultTest {

    @Test
    void shouldCreateRenderResult() {
        byte[] content = "Hello World".getBytes();
        RenderResult result = new RenderResult(content, RenderFormat.HTML, "test-template");

        assertArrayEquals(content, result.content());
        assertEquals(RenderFormat.HTML, result.format());
        assertEquals("test-template", result.templateName());
    }

    @Test
    void shouldReturnContentAsString() {
        RenderResult result = new RenderResult(
                "Hello World".getBytes(),
                RenderFormat.HTML,
                "test"
        );

        assertEquals("Hello World", result.contentAsString());
    }

    @Test
    void shouldGenerateSuggestedFilename() {
        RenderResult htmlResult = new RenderResult("content".getBytes(), RenderFormat.HTML, "report");
        RenderResult pdfResult = new RenderResult("content".getBytes(), RenderFormat.PDF, "report");
        RenderResult csvResult = new RenderResult("content".getBytes(), RenderFormat.CSV, "report");

        assertEquals("report.html", htmlResult.suggestedFilename());
        assertEquals("report.pdf", pdfResult.suggestedFilename());
        assertEquals("report.csv", csvResult.suggestedFilename());
    }

    @Test
    void shouldThrowExceptionForNullContent() {
        assertThrows(IllegalArgumentException.class, () ->
                new RenderResult(null, RenderFormat.HTML, "test"));
    }

    @Test
    void shouldThrowExceptionForNullFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                new RenderResult("content".getBytes(), null, "test"));
    }

    @Test
    void shouldThrowExceptionForNullTemplateName() {
        assertThrows(IllegalArgumentException.class, () ->
                new RenderResult("content".getBytes(), RenderFormat.HTML, null));
    }

    @Test
    void shouldThrowExceptionForBlankTemplateName() {
        assertThrows(IllegalArgumentException.class, () ->
                new RenderResult("content".getBytes(), RenderFormat.HTML, "   "));
    }
}

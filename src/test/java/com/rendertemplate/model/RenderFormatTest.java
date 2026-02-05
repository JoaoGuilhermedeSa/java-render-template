package com.rendertemplate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderFormatTest {

    @Test
    void shouldHaveCorrectMimeTypes() {
        assertEquals("text/html", RenderFormat.HTML.getMimeType());
        assertEquals("application/pdf", RenderFormat.PDF.getMimeType());
        assertEquals("text/csv", RenderFormat.CSV.getMimeType());
    }

    @Test
    void shouldHaveCorrectFileExtensions() {
        assertEquals(".html", RenderFormat.HTML.getFileExtension());
        assertEquals(".pdf", RenderFormat.PDF.getFileExtension());
        assertEquals(".csv", RenderFormat.CSV.getFileExtension());
    }

    @Test
    void shouldHaveThreeFormats() {
        assertEquals(3, RenderFormat.values().length);
    }
}

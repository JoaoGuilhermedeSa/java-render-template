package com.rendertemplate.factory;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.renderer.HtmlRenderer;
import com.rendertemplate.renderer.PdfRenderer;
import com.rendertemplate.renderer.CsvRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RendererFactoryTest {

    private RendererFactory factory;

    @BeforeEach
    void setUp() {
        factory = new RendererFactory();
    }

    @Test
    void shouldReturnHtmlRenderer() {
        var renderer = factory.getRenderer(RenderFormat.HTML);

        assertNotNull(renderer);
        assertInstanceOf(HtmlRenderer.class, renderer);
        assertEquals(RenderFormat.HTML, renderer.getFormat());
    }

    @Test
    void shouldReturnPdfRenderer() {
        var renderer = factory.getRenderer(RenderFormat.PDF);

        assertNotNull(renderer);
        assertInstanceOf(PdfRenderer.class, renderer);
        assertEquals(RenderFormat.PDF, renderer.getFormat());
    }

    @Test
    void shouldReturnCsvRenderer() {
        var renderer = factory.getRenderer(RenderFormat.CSV);

        assertNotNull(renderer);
        assertInstanceOf(CsvRenderer.class, renderer);
        assertEquals(RenderFormat.CSV, renderer.getFormat());
    }

    @Test
    void shouldCheckRendererAvailability() {
        assertTrue(factory.hasRenderer(RenderFormat.HTML));
        assertTrue(factory.hasRenderer(RenderFormat.PDF));
        assertTrue(factory.hasRenderer(RenderFormat.CSV));
    }

    @Test
    void shouldAllowRendererReplacement() {
        HtmlRenderer customRenderer = new HtmlRenderer();
        factory.register(customRenderer);

        assertSame(customRenderer, factory.getRenderer(RenderFormat.HTML));
    }
}

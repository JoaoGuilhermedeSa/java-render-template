package com.rendertemplate;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateServiceTest {

    private TemplateService service;

    @BeforeEach
    void setUp() {
        service = new TemplateService();
    }

    @Test
    void shouldHaveRendererFactory() {
        assertNotNull(service.getRendererFactory());
    }

    @Test
    void shouldRenderHtmlSuccessfully() {
        Template template = Template.builder()
                .name("test-template")
                .heading("Hello {{name}}")
                .data("name", "World")
                .build();

        var result = service.renderAsHtml(template);

        assertNotNull(result);
        assertEquals(RenderFormat.HTML, result.format());
        assertTrue(result.contentAsString().contains("Hello World"));
    }

    @Test
    void shouldRenderPdfSuccessfully() {
        Template template = Template.builder()
                .name("test-template")
                .heading("Hello {{name}}")
                .data("name", "World")
                .build();

        var result = service.renderAsPdf(template);

        assertNotNull(result);
        assertEquals(RenderFormat.PDF, result.format());
        assertTrue(result.content().length > 0);
    }

    @Test
    void shouldRenderCsvSuccessfully() {
        Template csvTemplate = Template.builder()
                .name("test-csv")
                .table(List.of("greeting"), List.of("{{name}}"))
                .data("name", "World")
                .build();

        var result = service.renderAsCsv(csvTemplate);

        assertNotNull(result);
        assertEquals(RenderFormat.CSV, result.format());
        assertEquals("greeting\nWorld", result.contentAsString());
    }

    @Test
    void shouldRenderViaGenericMethod() {
        Template template = Template.builder()
                .name("test-template")
                .heading("Hello {{name}}")
                .data("name", "World")
                .build();

        var result = service.render(template, RenderFormat.HTML);

        assertNotNull(result);
        assertEquals(RenderFormat.HTML, result.format());
        assertTrue(result.contentAsString().contains("Hello World"));
    }
}

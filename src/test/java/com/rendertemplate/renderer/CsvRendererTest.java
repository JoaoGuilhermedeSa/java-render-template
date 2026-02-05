package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvRendererTest {

    private CsvRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new CsvRenderer();
    }

    @Test
    void shouldReturnCsvFormat() {
        assertEquals(RenderFormat.CSV, renderer.getFormat());
    }

    @Test
    void shouldSupportCsvFormat() {
        assertTrue(renderer.supports(RenderFormat.CSV));
        assertFalse(renderer.supports(RenderFormat.HTML));
        assertFalse(renderer.supports(RenderFormat.PDF));
    }

    @Test
    void shouldRenderSingleRowFromData() {
        Template template = Template.builder()
                .name("report")
                .table(List.of("name", "age"), List.of("{{name}}", "{{age}}"))
                .data("name", "Alice")
                .data("age", 30)
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,age\nAlice,30", result.contentAsString());
        assertEquals(RenderFormat.CSV, result.format());
        assertEquals("report", result.templateName());
    }

    @Test
    void shouldRenderMultipleRowsFromRowsData() {
        Template template = Template.builder()
                .name("users")
                .table(List.of("name", "city"), List.of("{{name}}", "{{city}}"))
                .data("rows", List.of(
                        Map.of("name", "Alice", "city", "Paris"),
                        Map.of("name", "Bob", "city", "London")
                ))
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,city\nAlice,Paris\nBob,London", result.contentAsString());
    }

    @Test
    void shouldRenderHeaderOnlyWhenEmptyRows() {
        Template template = Template.builder()
                .name("empty")
                .table(List.of("col1", "col2", "col3"), List.of("{{a}}", "{{b}}", "{{c}}"))
                .data("rows", List.of())
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("col1,col2,col3", result.contentAsString());
    }

    @Test
    void shouldEscapeValuesContainingCommas() {
        Template template = Template.builder()
                .name("escaped")
                .table(List.of("name", "address"), List.of("{{name}}", "{{address}}"))
                .data("name", "Alice")
                .data("address", "123 Main St, Apt 4")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,address\nAlice,\"123 Main St, Apt 4\"", result.contentAsString());
    }

    @Test
    void shouldEscapeValuesContainingDoubleQuotes() {
        Template template = Template.builder()
                .name("quotes")
                .table(List.of("name", "nickname"), List.of("{{name}}", "{{nickname}}"))
                .data("name", "Alice")
                .data("nickname", "The \"Great\"")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,nickname\nAlice,\"The \"\"Great\"\"\"", result.contentAsString());
    }

    @Test
    void shouldEscapeValuesContainingNewlines() {
        Template template = Template.builder()
                .name("newlines")
                .table(List.of("name", "bio"), List.of("{{name}}", "{{bio}}"))
                .data("name", "Alice")
                .data("bio", "Line1\nLine2")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,bio\nAlice,\"Line1\nLine2\"", result.contentAsString());
    }

    @Test
    void shouldLeaveMissingPlaceholdersEmpty() {
        Template template = Template.builder()
                .name("missing")
                .table(List.of("name", "age"), List.of("{{name}}", "{{age}}"))
                .data("name", "Alice")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,age\nAlice,", result.contentAsString());
    }

    @Test
    void shouldThrowWhenNoTableSection() {
        Template template = Template.builder()
                .name("no-table")
                .heading("Just a heading")
                .build();

        assertThrows(RenderException.class, () -> renderer.render(template));
    }

    @Test
    void shouldIgnoreNonTableSections() {
        Template template = Template.builder()
                .name("mixed")
                .heading("Ignored heading")
                .paragraph("Ignored paragraph")
                .table(List.of("name"), List.of("{{name}}"))
                .data("name", "Alice")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name\nAlice", result.contentAsString());
    }

    @Test
    void shouldProduceSuggestedCsvFilename() {
        Template template = Template.builder()
                .name("report")
                .table(List.of("a", "b"), List.of("{{a}}", "{{b}}"))
                .data("a", "1")
                .data("b", "2")
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("report.csv", result.suggestedFilename());
    }

    @Test
    void shouldEscapeCsvValueWithComma() {
        assertEquals("\"a,b\"", renderer.escapeCsvValue("a,b"));
    }

    @Test
    void shouldEscapeCsvValueWithQuotes() {
        assertEquals("\"say \"\"hello\"\"\"", renderer.escapeCsvValue("say \"hello\""));
    }

    @Test
    void shouldNotEscapePlainCsvValue() {
        assertEquals("hello", renderer.escapeCsvValue("hello"));
    }

    @Test
    void shouldHandleRowsWithMissingKeys() {
        Template template = Template.builder()
                .name("partial-rows")
                .table(List.of("name", "age"), List.of("{{name}}", "{{age}}"))
                .data("rows", List.of(
                        Map.of("name", "Alice"),
                        Map.of("age", 25)
                ))
                .build();

        RenderResult result = renderer.render(template);

        assertEquals("name,age\nAlice,\n,25", result.contentAsString());
    }
}

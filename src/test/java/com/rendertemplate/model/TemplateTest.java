package com.rendertemplate.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    void shouldBuildTemplateWithHeading() {
        Template template = Template.builder()
                .name("test-template")
                .heading("Hello {{name}}")
                .build();

        assertEquals("test-template", template.getName());
        assertEquals(1, template.getSections().size());
        assertInstanceOf(TemplateSection.Heading.class, template.getSections().get(0));
        assertEquals("Hello {{name}}", ((TemplateSection.Heading) template.getSections().get(0)).text());
    }

    @Test
    void shouldBuildTemplateWithParagraph() {
        Template template = Template.builder()
                .name("test-template")
                .paragraph("Some text")
                .build();

        assertEquals(1, template.getSections().size());
        assertInstanceOf(TemplateSection.Paragraph.class, template.getSections().get(0));
    }

    @Test
    void shouldBuildTemplateWithTable() {
        Template template = Template.builder()
                .name("test-template")
                .table(List.of("Name", "Age"), List.of("{{name}}", "{{age}}"))
                .build();

        assertEquals(1, template.getSections().size());
        assertInstanceOf(TemplateSection.Table.class, template.getSections().get(0));
        var table = (TemplateSection.Table) template.getSections().get(0);
        assertEquals(List.of("Name", "Age"), table.headers());
        assertEquals(List.of("{{name}}", "{{age}}"), table.columns());
    }

    @Test
    void shouldBuildTemplateWithMultipleSections() {
        Template template = Template.builder()
                .name("report")
                .heading("{{title}}")
                .paragraph("Generated for {{company}}")
                .table(List.of("Name", "Age"), List.of("{{name}}", "{{age}}"))
                .build();

        assertEquals(3, template.getSections().size());
        assertInstanceOf(TemplateSection.Heading.class, template.getSections().get(0));
        assertInstanceOf(TemplateSection.Paragraph.class, template.getSections().get(1));
        assertInstanceOf(TemplateSection.Table.class, template.getSections().get(2));
    }

    @Test
    void shouldBuildTemplateWithData() {
        Template template = Template.builder()
                .name("test-template")
                .heading("Hello")
                .data("name", "World")
                .data("count", 42)
                .build();

        assertEquals("World", template.getData("name"));
        assertEquals(42, template.getData("count"));
    }

    @Test
    void shouldBuildTemplateWithDataMap() {
        Map<String, Object> data = Map.of("key1", "value1", "key2", "value2");

        Template template = Template.builder()
                .name("test-template")
                .heading("Hello")
                .data(data)
                .build();

        assertEquals("value1", template.getData("key1"));
        assertEquals("value2", template.getData("key2"));
    }

    @Test
    void shouldBuildTemplateWithSectionMethod() {
        Template template = Template.builder()
                .name("test-template")
                .section(new TemplateSection.Heading("Custom"))
                .build();

        assertEquals(1, template.getSections().size());
        assertInstanceOf(TemplateSection.Heading.class, template.getSections().get(0));
    }

    @Test
    void shouldThrowExceptionWhenNameMissing() {
        var builder = Template.builder().heading("content");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void shouldThrowExceptionWhenNoSections() {
        var builder = Template.builder().name("test");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void shouldReturnImmutableDataMap() {
        Template template = Template.builder()
                .name("test")
                .heading("content")
                .data("key", "value")
                .build();

        assertThrows(UnsupportedOperationException.class, () ->
                template.getData().put("newKey", "newValue"));
    }

    @Test
    void shouldReturnImmutableSectionsList() {
        Template template = Template.builder()
                .name("test")
                .heading("content")
                .build();

        assertThrows(UnsupportedOperationException.class, () ->
                template.getSections().add(new TemplateSection.Heading("extra")));
    }
}

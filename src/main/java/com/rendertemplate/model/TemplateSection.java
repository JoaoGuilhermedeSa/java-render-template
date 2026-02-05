package com.rendertemplate.model;

import java.util.List;

/**
 * Represents a section within a template. Each renderer interprets sections
 * according to its output format.
 */
public sealed interface TemplateSection {

    record Heading(String text) implements TemplateSection {}

    record Paragraph(String text) implements TemplateSection {}

    record Table(List<String> headers, List<String> columns) implements TemplateSection {}
}

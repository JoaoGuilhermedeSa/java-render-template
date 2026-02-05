package com.rendertemplate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a template with structured sections and associated data for rendering.
 */
public class Template {

    private final String name;
    private final List<TemplateSection> sections;
    private final Map<String, Object> data;

    private Template(Builder builder) {
        this.name = builder.name;
        this.sections = List.copyOf(builder.sections);
        this.data = Collections.unmodifiableMap(new HashMap<>(builder.data));
    }

    public String getName() {
        return name;
    }

    public List<TemplateSection> getSections() {
        return sections;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private final List<TemplateSection> sections = new ArrayList<>();
        private final Map<String, Object> data = new HashMap<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder heading(String text) {
            this.sections.add(new TemplateSection.Heading(text));
            return this;
        }

        public Builder paragraph(String text) {
            this.sections.add(new TemplateSection.Paragraph(text));
            return this;
        }

        public Builder table(List<String> headers, List<String> columns) {
            this.sections.add(new TemplateSection.Table(headers, columns));
            return this;
        }

        public Builder section(TemplateSection section) {
            this.sections.add(section);
            return this;
        }

        public Builder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        public Builder data(Map<String, Object> data) {
            this.data.putAll(data);
            return this;
        }

        public Template build() {
            if (name == null || name.isBlank()) {
                throw new IllegalStateException("Template name is required");
            }
            if (sections.isEmpty()) {
                throw new IllegalStateException("Template must have at least one section");
            }
            return new Template(this);
        }
    }

    @Override
    public String toString() {
        return "Template{name='%s', sections=%d, dataKeys=%s}".formatted(name, sections.size(), data.keySet());
    }
}

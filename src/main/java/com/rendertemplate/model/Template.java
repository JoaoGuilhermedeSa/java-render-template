package com.rendertemplate.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a template with its content and associated data for rendering.
 */
public class Template {

    private final String name;
    private final String content;
    private final Map<String, Object> data;

    private Template(Builder builder) {
        this.name = builder.name;
        this.content = builder.content;
        this.data = Collections.unmodifiableMap(new HashMap<>(builder.data));
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
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
        private String content;
        private final Map<String, Object> data = new HashMap<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
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
            if (content == null) {
                throw new IllegalStateException("Template content is required");
            }
            return new Template(this);
        }
    }

    @Override
    public String toString() {
        return "Template{name='%s', dataKeys=%s}".formatted(name, data.keySet());
    }
}

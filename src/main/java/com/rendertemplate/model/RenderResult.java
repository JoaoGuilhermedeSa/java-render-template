package com.rendertemplate.model;

/**
 * Contains the result of a template rendering operation.
 */
public record RenderResult(
        byte[] content,
        RenderFormat format,
        String templateName
) {
    public RenderResult {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template name cannot be null or blank");
        }
    }

    /**
     * Returns the content as a string (useful for text-based formats like HTML and CSV).
     */
    public String contentAsString() {
        return new String(content);
    }

    /**
     * Returns the suggested filename for this render result.
     */
    public String suggestedFilename() {
        return templateName + format.getFileExtension();
    }
}

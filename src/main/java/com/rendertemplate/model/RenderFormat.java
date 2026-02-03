package com.rendertemplate.model;

/**
 * Supported output formats for template rendering.
 */
public enum RenderFormat {
    HTML("text/html", ".html"),
    PDF("application/pdf", ".pdf"),
    CSV("text/csv", ".csv");

    private final String mimeType;
    private final String fileExtension;

    RenderFormat(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}

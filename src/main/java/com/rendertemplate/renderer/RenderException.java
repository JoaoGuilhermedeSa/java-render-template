package com.rendertemplate.renderer;

/**
 * Exception thrown when template rendering fails.
 */
public class RenderException extends RuntimeException {

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }
}

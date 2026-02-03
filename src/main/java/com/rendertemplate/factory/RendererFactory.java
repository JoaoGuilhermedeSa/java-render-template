package com.rendertemplate.factory;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.renderer.CsvRenderer;
import com.rendertemplate.renderer.HtmlRenderer;
import com.rendertemplate.renderer.PdfRenderer;
import com.rendertemplate.renderer.TemplateRenderer;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for creating and retrieving template renderers based on the desired output format.
 */
public class RendererFactory {

    private final Map<RenderFormat, TemplateRenderer> renderers;

    public RendererFactory() {
        this.renderers = new EnumMap<>(RenderFormat.class);
        registerDefaultRenderers();
    }

    private void registerDefaultRenderers() {
        register(new HtmlRenderer());
        register(new PdfRenderer());
        register(new CsvRenderer());
    }

    /**
     * Registers a renderer for its supported format.
     *
     * @param renderer the renderer to register
     */
    public void register(TemplateRenderer renderer) {
        renderers.put(renderer.getFormat(), renderer);
    }

    /**
     * Gets the renderer for the specified format.
     *
     * @param format the desired output format
     * @return the renderer for the format
     * @throws IllegalArgumentException if no renderer is registered for the format
     */
    public TemplateRenderer getRenderer(RenderFormat format) {
        TemplateRenderer renderer = renderers.get(format);
        if (renderer == null) {
            throw new IllegalArgumentException("No renderer registered for format: " + format);
        }
        return renderer;
    }

    /**
     * Checks if a renderer is available for the specified format.
     *
     * @param format the format to check
     * @return true if a renderer is available
     */
    public boolean hasRenderer(RenderFormat format) {
        return renderers.containsKey(format);
    }
}

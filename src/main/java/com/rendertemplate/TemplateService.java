package com.rendertemplate;

import com.rendertemplate.factory.RendererFactory;
import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import com.rendertemplate.renderer.TemplateRenderer;

/**
 * Main service for rendering templates to various output formats.
 * Provides a high-level API for template rendering operations.
 */
public class TemplateService {

    private final RendererFactory rendererFactory;

    public TemplateService() {
        this.rendererFactory = new RendererFactory();
    }

    public TemplateService(RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;
    }

    /**
     * Renders a template to the specified format.
     *
     * @param template the template to render
     * @param format   the desired output format
     * @return the render result
     */
    public RenderResult render(Template template, RenderFormat format) {
        TemplateRenderer renderer = rendererFactory.getRenderer(format);
        return renderer.render(template);
    }

    /**
     * Renders a template to HTML format.
     *
     * @param template the template to render
     * @return the render result
     */
    public RenderResult renderAsHtml(Template template) {
        return render(template, RenderFormat.HTML);
    }

    /**
     * Renders a template to PDF format.
     *
     * @param template the template to render
     * @return the render result
     */
    public RenderResult renderAsPdf(Template template) {
        return render(template, RenderFormat.PDF);
    }

    /**
     * Renders a template to CSV format.
     *
     * @param template the template to render
     * @return the render result
     */
    public RenderResult renderAsCsv(Template template) {
        return render(template, RenderFormat.CSV);
    }

    /**
     * Gets the underlying renderer factory for advanced customization.
     *
     * @return the renderer factory
     */
    public RendererFactory getRendererFactory() {
        return rendererFactory;
    }
}

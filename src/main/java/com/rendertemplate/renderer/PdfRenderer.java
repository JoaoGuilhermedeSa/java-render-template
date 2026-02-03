package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;

/**
 * Renderer that converts templates to PDF format.
 */
public class PdfRenderer implements TemplateRenderer {

    @Override
    public RenderResult render(Template template) {
        // TODO: Implement PDF rendering logic
        throw new UnsupportedOperationException("PDF rendering not yet implemented");
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.PDF;
    }
}

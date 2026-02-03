package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;

/**
 * Renderer that converts templates to CSV format.
 */
public class CsvRenderer implements TemplateRenderer {

    @Override
    public RenderResult render(Template template) {
        // TODO: Implement CSV rendering logic
        throw new UnsupportedOperationException("CSV rendering not yet implemented");
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.CSV;
    }
}

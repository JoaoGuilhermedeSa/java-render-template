package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;

/**
 * Renderer that converts templates to HTML format.
 */
public class HtmlRenderer implements TemplateRenderer {

    @Override
    public RenderResult render(Template template) {
        // TODO: Implement HTML rendering logic
        throw new UnsupportedOperationException("HTML rendering not yet implemented");
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.HTML;
    }
}

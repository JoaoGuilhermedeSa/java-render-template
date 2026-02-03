package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;

/**
 * Interface for template renderers that convert a Template to a specific output format.
 */
public interface TemplateRenderer {

    /**
     * Renders the given template to the output format supported by this renderer.
     *
     * @param template the template to render
     * @return the render result containing the output content
     * @throws RenderException if rendering fails
     */
    RenderResult render(Template template);

    /**
     * Returns the output format supported by this renderer.
     *
     * @return the render format
     */
    RenderFormat getFormat();

    /**
     * Checks if this renderer supports the given format.
     *
     * @param format the format to check
     * @return true if this renderer supports the format
     */
    default boolean supports(RenderFormat format) {
        return getFormat() == format;
    }
}

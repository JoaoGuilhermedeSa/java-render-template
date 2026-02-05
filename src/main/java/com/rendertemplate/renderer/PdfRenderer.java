package com.rendertemplate.renderer;

import com.rendertemplate.model.RenderFormat;
import com.rendertemplate.model.RenderResult;
import com.rendertemplate.model.Template;
import com.rendertemplate.model.TemplateSection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Renderer that converts templates to PDF format.
 *
 * <p>Sections are rendered as follows:
 * <ul>
 *   <li>{@code Heading} &rarr; 18pt bold Helvetica</li>
 *   <li>{@code Paragraph} &rarr; 12pt regular Helvetica</li>
 *   <li>{@code Table} &rarr; Column-aligned text layout with 12pt font, bold headers</li>
 * </ul>
 */
public class PdfRenderer implements TemplateRenderer {

    private static final float HEADING_FONT_SIZE = 18f;
    private static final float BODY_FONT_SIZE = 12f;
    private static final float MARGIN = 50f;
    private static final float HEADING_LEADING = 24f;
    private static final float BODY_LEADING = 16f;

    @Override
    public RenderResult render(Template template) {
        if (template.getSections().isEmpty()) {
            throw new RenderException("Template must have at least one section for PDF rendering");
        }

        byte[] pdfBytes = generatePdf(template);
        return new RenderResult(pdfBytes, RenderFormat.PDF, template.getName());
    }

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.PDF;
    }

    private byte[] generatePdf(Template template) {
        try (PDDocument document = new PDDocument()) {
            PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            float usableWidth = pageWidth - 2 * MARGIN;
            float yStart = pageHeight - MARGIN;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);
            float currentY = yStart;

            try {
                for (TemplateSection section : template.getSections()) {
                    switch (section) {
                        case TemplateSection.Heading h -> {
                            String text = PlaceholderResolver.resolve(h.text(), template.getData());
                            if (currentY - HEADING_LEADING < MARGIN) {
                                cs.close();
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                cs = new PDPageContentStream(document, page);
                                currentY = yStart;
                            }
                            cs.beginText();
                            cs.setFont(boldFont, HEADING_FONT_SIZE);
                            cs.setLeading(HEADING_LEADING);
                            cs.newLineAtOffset(MARGIN, currentY);
                            cs.showText(text);
                            cs.endText();
                            currentY -= HEADING_LEADING;
                        }
                        case TemplateSection.Paragraph p -> {
                            String text = PlaceholderResolver.resolve(p.text(), template.getData());
                            if (currentY - BODY_LEADING < MARGIN) {
                                cs.close();
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                cs = new PDPageContentStream(document, page);
                                currentY = yStart;
                            }
                            cs.beginText();
                            cs.setFont(regularFont, BODY_FONT_SIZE);
                            cs.setLeading(BODY_LEADING);
                            cs.newLineAtOffset(MARGIN, currentY);
                            cs.showText(text);
                            cs.endText();
                            currentY -= BODY_LEADING;
                        }
                        case TemplateSection.Table t -> {
                            int colCount = t.headers().size();
                            float colWidth = usableWidth / colCount;

                            // Header row (bold)
                            if (currentY - BODY_LEADING < MARGIN) {
                                cs.close();
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                cs = new PDPageContentStream(document, page);
                                currentY = yStart;
                            }
                            for (int i = 0; i < colCount; i++) {
                                cs.beginText();
                                cs.setFont(boldFont, BODY_FONT_SIZE);
                                cs.newLineAtOffset(MARGIN + i * colWidth, currentY);
                                cs.showText(t.headers().get(i));
                                cs.endText();
                            }
                            currentY -= BODY_LEADING;

                            // Data rows
                            List<Map<String, Object>> rows = extractRows(template);
                            for (Map<String, Object> row : rows) {
                                if (currentY - BODY_LEADING < MARGIN) {
                                    cs.close();
                                    page = new PDPage(PDRectangle.A4);
                                    document.addPage(page);
                                    cs = new PDPageContentStream(document, page);
                                    currentY = yStart;
                                }
                                for (int i = 0; i < t.columns().size(); i++) {
                                    String resolved = PlaceholderResolver.resolve(t.columns().get(i), row);
                                    cs.beginText();
                                    cs.setFont(regularFont, BODY_FONT_SIZE);
                                    cs.newLineAtOffset(MARGIN + i * colWidth, currentY);
                                    cs.showText(resolved);
                                    cs.endText();
                                }
                                currentY -= BODY_LEADING;
                            }
                        }
                    }
                }
            } finally {
                cs.close();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RenderException("Failed to generate PDF", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRows(Template template) {
        Object rowsData = template.getData("rows");
        if (rowsData instanceof List<?> rows) {
            return rows.stream()
                    .filter(r -> r instanceof Map)
                    .map(r -> (Map<String, Object>) r)
                    .toList();
        }
        return List.of(template.getData());
    }
}

package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.macbean.tech.shopify.AbstractShopifyReportGenerator;
import com.macbean.tech.shopify.ShopifyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;

public abstract class AbstractShopifyReportPdfGenerator extends AbstractShopifyReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShopifyReportPdfGenerator.class);

    private static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font PAGE_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE, BaseColor.BLACK);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
    private static final Font TAG_FONT = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
    private static final BaseColor TABLE_HEADER_BG = new BaseColor(10,49,82, 1);

    protected abstract Rectangle getPageSize();

    protected abstract String getTitle();

    protected abstract String getReferencePrefix();

    protected abstract void addHeader() throws DocumentException, IOException;

    protected abstract void addContent() throws DocumentException, IOException;

    protected abstract void addFooter() throws DocumentException, IOException;

    @Override
    protected String getFileSuffix() {
        return ".pdf";
    }

    protected Image getEyeLogo(float width, float height, int alignment) throws DocumentException, IOException {
        final Image eyeLogo = Image.getInstance(new URL(ShopifyConstants.EYE_LOGO_URL));
        eyeLogo.scaleToFit(width,height);
        eyeLogo.setAlignment(alignment);
        return eyeLogo;
    }

    protected byte[] getByteData() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            document = new Document(getPageSize());
            final PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
            pdfWriter.setPageEvent(new ReportPageEventHandler(getTitle(), this.dateFrom, this.dateTo));
            document.open();
            document.add(createPageTitle(getTitle()));
            addHeader();
            addContent();
            addFooter();
            document.close();
        } catch (DocumentException | IOException e) {
            LOGGER.error("Problem generating PDF file", e);
        } finally {
            document.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    //** iText convenience methods **//

    protected Paragraph createPageTitle(String text) {
        final Paragraph pageTitle = new Paragraph(text, PAGE_TITLE_FONT);
        pageTitle.setAlignment(ALIGN_CENTER);
         return pageTitle;
    }

    protected PdfPTable createFullWidthTable(int numberOfColumns) {
        final PdfPTable table = new PdfPTable(numberOfColumns);
        return formatTable(table);
    }

    protected PdfPTable createFullWidthTable(float... relativeWidths) {
        final PdfPTable table = new PdfPTable(relativeWidths);
        return formatTable(table);
    }

    protected PdfPTable formatTable(PdfPTable table) {
        table.setWidthPercentage(ONE_HUNDRED_PERCENT);
        table.setSpacingAfter(SPACING);
        return table;
    }

    protected PdfPCell createTableCell(String text) {
        return createTableCell(text, Element.ALIGN_LEFT);
    }

    protected PdfPCell createTableTagCell(String text) {
        return createTableCell(text, Element.ALIGN_LEFT, true, TAG_FONT);
    }

    protected PdfPCell createTableCell(BigDecimal value, int alignment) {
        return createTableCell(formatAmount(value), alignment, true, DEFAULT_FONT);
    }

    protected PdfPCell createTableCell(String text, int alignment) {
        return createTableCell(text, alignment, true, DEFAULT_FONT);
    }

    protected PdfPCell createTableCell(String text, int alignment, boolean hasBorders) {
        return createTableCell(text, alignment, hasBorders, DEFAULT_FONT);
    }

    protected PdfPCell createTableCell(String text, int alignment, boolean hasBorders, Font font) {
        final PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(alignment);
        if (!hasBorders) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    protected PdfPCell createTableCell(PdfPTable table, boolean hasBorders) {
        final PdfPCell cell = new PdfPCell(table);
        if (!hasBorders) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    protected PdfPCell createTableHeaderCell(String text) {
        final PdfPCell cell = createTableCell(text, ALIGN_LEFT, true, TABLE_HEADER_FONT);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        return cell;
    }

    protected PdfPCell createTableHeaderCell(BigDecimal value, int alignment) {
        return createTableHeaderCell(formatAmount(value), alignment);
    }

    protected PdfPCell createTableHeaderCell(String text, int alignment) {
        final PdfPCell cell = createTableCell(text, alignment, true, TABLE_HEADER_FONT);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        return cell;
    }

    protected void addCellsToTable(PdfPTable table, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            table.addCell(cell);
        }
    }

    protected String formatAmount(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.UK).format(value);
    }
}

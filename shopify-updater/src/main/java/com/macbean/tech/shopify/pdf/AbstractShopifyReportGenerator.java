package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.macbean.tech.shopify.ShopifyClient;
import com.macbean.tech.shopify.ShopifyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;

public abstract class AbstractShopifyReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShopifyReportGenerator.class);

    public static final String DATE_FORMAT = "dd-MMM-yyyy";
    public static final String ORDER_DATE_FORMAT = "dd-MM-yy hh:mm";
    public static final String FILENAME_DATE_FORMAT = "yyyyMM";

    private static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font PAGE_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE, BaseColor.BLACK);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
    private static final float ONE_HUNDRED_PERCENT = 100f;
    private static final float SPACING = 25f;
    private static final BaseColor TABLE_HEADER_BG = new BaseColor(10,49,82, 1);

    ShopifyClient shopifyClient;

    /** REPORT PARAMS **/
    ZonedDateTime from;
    ZonedDateTime to;
    Document document;
    String dateFrom;
    String dateTo;
    String reference;

    abstract Rectangle getPageSize();

    abstract String getTitle();

    abstract String getReferencePrefix();

    abstract void addHeader() throws DocumentException, IOException;

    abstract void addContent() throws DocumentException, IOException;

    abstract void addFooter() throws DocumentException, IOException;

    protected byte[] generateReport(ZonedDateTime from, ZonedDateTime to) throws IOException {
        this.from = from;
        this.to = to;
        this.dateFrom = DateTimeFormatter.ofPattern(DATE_FORMAT).format(from);
        this.dateTo = DateTimeFormatter.ofPattern(DATE_FORMAT).format(to);
        this.reference = getReferencePrefix() + DateTimeFormatter.ofPattern(FILENAME_DATE_FORMAT).format(to);
        this.shopifyClient = new ShopifyClient();
        byte[] pdfData = getPdfByteData();
        writePdfToFile(reference, pdfData);
        return pdfData;
    }

    protected Image getEyeLogo(float width, float height, int alignment) throws DocumentException, IOException {
        final Image eyeLogo = Image.getInstance(new URL(ShopifyConstants.EYE_LOGO_URL));
        eyeLogo.scaleToFit(width,height);
        eyeLogo.setAlignment(alignment);
        return eyeLogo;
    }

    private byte[] getPdfByteData() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            document = new Document(getPageSize());
            final PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
            pdfWriter.setPageEvent(new ReportPageEventHandler(getTitle()));
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

    private void writePdfToFile(String name, byte[] pdfBytes) {
        final String filename = ShopifyConstants.OUTPUT_DIRECTORY + File.separatorChar + name + ".pdf";
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(pdfBytes);
            LOGGER.debug("File written to {}", filename);
        } catch (IOException ioe) {
            LOGGER.error("Unable to write PDF file to {} ", filename, ioe);
        }
    }

    //** iText convenience methods **//

    Paragraph createPageTitle(String text) {
        final Paragraph pageTitle = new Paragraph(text, PAGE_TITLE_FONT);
        pageTitle.setAlignment(ALIGN_CENTER);
         return pageTitle;
    }

    PdfPTable createFullWidthTable(int numberOfColumns) {
        final PdfPTable table = new PdfPTable(numberOfColumns);
        return formatTable(table);
    }

    PdfPTable createFullWidthTable(float... relativeWidths) {
        final PdfPTable table = new PdfPTable(relativeWidths);
        return formatTable(table);
    }

    PdfPTable formatTable(PdfPTable table) {
        table.setWidthPercentage(ONE_HUNDRED_PERCENT);
        table.setSpacingAfter(SPACING);
        return table;
    }

    PdfPCell createTableCell(String text) {
        return createTableCell(text, Element.ALIGN_LEFT);
    }

    PdfPCell createTableCell(BigDecimal value, int alignment) {
        return createTableCell(formatAmount(value), alignment, true, DEFAULT_FONT);
    }

    PdfPCell createTableCell(String text, int alignment) {
        return createTableCell(text, alignment, true, DEFAULT_FONT);
    }

    PdfPCell createTableCell(String text, int alignment, boolean hasBorders) {
        return createTableCell(text, alignment, hasBorders, DEFAULT_FONT);
    }

    PdfPCell createTableCell(String text, int alignment, boolean hasBorders, Font font) {
        final PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(alignment);
        if (!hasBorders) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    PdfPCell createTableCell(PdfPTable table, boolean hasBorders) {
        final PdfPCell cell = new PdfPCell(table);
        if (!hasBorders) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    PdfPCell createTableHeaderCell(String text) {
        final PdfPCell cell = createTableCell(text, ALIGN_LEFT, true, TABLE_HEADER_FONT);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        return cell;
    }

    PdfPCell createTableHeaderCell(BigDecimal value, int alignment) {
        return createTableHeaderCell(formatAmount(value), alignment);
    }

    PdfPCell createTableHeaderCell(String text, int alignment) {
        final PdfPCell cell = createTableCell(text, alignment, true, TABLE_HEADER_FONT);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        return cell;
    }

    void addCellsToTable(PdfPTable table, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            table.addCell(cell);
        }
    }

    String formatAmount(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.UK).format(value);
    }
}

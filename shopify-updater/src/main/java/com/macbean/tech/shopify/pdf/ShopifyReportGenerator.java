package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.macbean.tech.shopify.ShopifyClient;
import com.macbean.tech.shopify.model.Order;
import com.macbean.tech.shopify.model.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;

public class ShopifyReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyReportGenerator.class);

    private static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font PAGE_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE, BaseColor.BLACK);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);

    private static final float ONE_HUNDRED_PERCENT = 100f;
    private static final float SPACING = 25f;

    private static final BaseColor TABLE_HEADER_BG = new BaseColor(10,49,82, 1);

    private ShopifyClient shopifyClient = new ShopifyClient();

    private Document document;
    private String period;

    public byte[] generateAndrewCommissionReport(ZonedDateTime from, ZonedDateTime to, BigDecimal commissionRate) throws IOException {
        final Orders orders = shopifyClient.getAllOrders(from, to);
        period = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(from) + " to " + DateTimeFormatter.ofPattern("dd-MM-yyyy").format(to);
        byte[] pdfData = getPdfByteData(orders, commissionRate);
        writePdfToFile("Andrew Commission", pdfData);
        return pdfData;
    }

    private byte[] getPdfByteData(Orders orders, BigDecimal commissionRate) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            document = new Document(PageSize.A4);
            final PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
            pdfWriter.setPageEvent(new PageEventHandler());
            document.open();
            addHeader();
            BigDecimal totalCommissionDue = addOrderCommissionBreakdown(orders, commissionRate);
            addFooter(totalCommissionDue);
            document.close();
        } catch (DocumentException de) {
            LOGGER.error("Problem generating PDF file", de);
        } finally {
            document.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void addHeader() throws DocumentException {
        document.add(createPageTitle("Eye Rackets - Sales Commission Invoice"));

        final PdfPTable eyeRacketsInfoTable = new PdfPTable(1);
        eyeRacketsInfoTable.setWidthPercentage(80f);
        eyeRacketsInfoTable.addCell(createTableCell("Eye Rackets International B.V.", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("Zekeringstraat 17, 1014 BM", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("Amsterdam", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("The Netherlands", ALIGN_LEFT, false));
        eyeRacketsInfoTable.setSpacingAfter(SPACING);

        final PdfPTable andrewInfoTable = new PdfPTable(1);
        andrewInfoTable.setWidthPercentage(100f);
        andrewInfoTable.addCell(createTableCell("Andrew MacBean", ALIGN_RIGHT, false));
        andrewInfoTable.addCell(createTableCell("5 Sandpiper Crescent", ALIGN_RIGHT, false));
        andrewInfoTable.addCell(createTableCell("Coatbridge", ALIGN_RIGHT, false));
        andrewInfoTable.addCell(createTableCell("ML5 4UW", ALIGN_RIGHT, false));
        andrewInfoTable.addCell(createTableCell("Scotland", ALIGN_RIGHT, false));
        eyeRacketsInfoTable.setSpacingAfter(SPACING);

        final PdfPTable headerTable = new PdfPTable(2);
        headerTable.addCell(createTableCell(eyeRacketsInfoTable, false));
        headerTable.addCell(createTableCell(andrewInfoTable, false));

        headerTable.setSpacingAfter(SPACING);

        document.add(headerTable);
    }

    private BigDecimal addOrderCommissionBreakdown(Orders orders, BigDecimal commissionRate) throws DocumentException {
        final PdfPTable commissionBreakdownTable = createFullWidthTable(1,2,1,1,1,1,1);
        commissionBreakdownTable.setWidthPercentage(100f);

        commissionBreakdownTable.addCell(createTableHeaderCell("Order #"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Date"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Order Total"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Tax"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Shipping"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Sales Amount"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Commission Due"));

        BigDecimal totalPriceAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        BigDecimal totalShippingAmount = BigDecimal.ZERO;
        BigDecimal totalSalesAmount = BigDecimal.ZERO;
        BigDecimal totalCommissionPayable = BigDecimal.ZERO;

        for (Order order : orders.getOrders()) {
            final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
            if (!BigDecimal.ZERO.equals(totalPrice)) {
                commissionBreakdownTable.addCell(createTableCell(order.getName()));
                final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
                commissionBreakdownTable.addCell(createTableCell(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm").format(orderDate)));
                totalPriceAmount = totalPriceAmount.add(totalPrice);
                commissionBreakdownTable.addCell(createTableCell(totalPrice, ALIGN_RIGHT));
                final BigDecimal tax = new BigDecimal(order.getTotalTax());
                totalTaxAmount = totalTaxAmount.add(tax);
                commissionBreakdownTable.addCell(createTableCell(tax, ALIGN_RIGHT));
                final BigDecimal shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
                totalShippingAmount = totalShippingAmount.add(shipping);
                commissionBreakdownTable.addCell(createTableCell(shipping, ALIGN_RIGHT));
                final BigDecimal salesAmount = totalPrice.subtract(tax).subtract(shipping);
                totalSalesAmount = totalSalesAmount.add(salesAmount);
                commissionBreakdownTable.addCell(createTableCell(salesAmount, ALIGN_RIGHT));
                final BigDecimal commissionPayable  = salesAmount.multiply(commissionRate);
                totalCommissionPayable = totalCommissionPayable.add(commissionPayable);
                commissionBreakdownTable.addCell(createTableCell(commissionPayable, ALIGN_RIGHT));
            }
        }

        final PdfPCell totalsCell = createTableCell("TOTALS");
        totalsCell.setColspan(2);
        commissionBreakdownTable.addCell(totalsCell);
        commissionBreakdownTable.addCell(createTableCell(totalPriceAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalTaxAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalShippingAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalSalesAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalCommissionPayable, ALIGN_RIGHT));

        document.add(commissionBreakdownTable);

        return totalCommissionPayable;
    }

    private void addFooter(BigDecimal totalCommissionDue) throws DocumentException {
        final PdfPTable footerTable = createFullWidthTable(5,1);

        footerTable.setWidthPercentage(100f);
        footerTable.addCell(createTableHeaderCell("Total Commission due to Andrew MacBean for period " + period));
        footerTable.addCell(createTableHeaderCell(totalCommissionDue, ALIGN_RIGHT));

        document.add(footerTable);
    }

    private void writePdfToFile(String name, byte[] pdfBytes) {
        final String tempDir = System.getProperty("java.io.tmpdir");
        final String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssS").format(LocalDateTime.now());
        final String filename = tempDir + File.separatorChar + name + "-" + timestamp + ".pdf";
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(pdfBytes);
            LOGGER.debug("Temp file written to {}", filename);
        } catch (IOException ioe) {
            LOGGER.error("Unable to write PDF file to {} ", tempDir, ioe);
        }
    }

    //** iText convenience methods **//

    private Paragraph createPageTitle(String text) {
        final Paragraph pageTitle = new Paragraph(text, PAGE_TITLE_FONT);
        pageTitle.setAlignment(ALIGN_CENTER);
        pageTitle.setSpacingAfter(SPACING);
        return pageTitle;
    }

    private PdfPTable createFullWidthTable(int numberOfColumns) {
        final PdfPTable table = new PdfPTable(numberOfColumns);
        return formatTable(table);
    }

    private PdfPTable createFullWidthTable(float... relativeWidths) {
        final PdfPTable table = new PdfPTable(relativeWidths);
        return formatTable(table);
    }

    private PdfPTable formatTable(PdfPTable table) {
        table.setWidthPercentage(ONE_HUNDRED_PERCENT);
        table.setSpacingAfter(SPACING);
        return table;
    }

    private PdfPCell createTableCell(String text) {
        return createTableCell(text, Element.ALIGN_LEFT);
    }

    private PdfPCell createTableCell(BigDecimal value, int alignment) {
        return createTableCell(formatAmount(value), alignment, true, DEFAULT_FONT);
    }

    private PdfPCell createTableCell(String text, int alignment) {
        return createTableCell(text, alignment, true, DEFAULT_FONT);
    }

    private PdfPCell createTableCell(String text, int alignment, boolean hasBorders) {
        return createTableCell(text, alignment, hasBorders, DEFAULT_FONT);
    }

    private PdfPCell createTableCell(String text, int alignment, boolean hasBorders, Font font) {
        final PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(alignment);
        if (!hasBorders) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    private PdfPCell createTableCell(PdfPTable table, boolean hasBorders) {
        final PdfPCell cell = new PdfPCell(table);
        if (!hasBorders) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    private PdfPCell createTableHeaderCell(String text) {
        final PdfPCell cell = createTableCell(text, ALIGN_LEFT, true, TABLE_HEADER_FONT);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        return cell;
    }

    private PdfPCell createTableHeaderCell(BigDecimal value, int alignment) {
        return createTableHeaderCell(formatAmount(value), alignment);
    }

    private PdfPCell createTableHeaderCell(String text, int alignment) {
        final PdfPCell cell = createTableCell(text, alignment, true, TABLE_HEADER_FONT);
        cell.setBackgroundColor(TABLE_HEADER_BG);
        return cell;
    }

    private void addCellsToTable(PdfPTable table, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            table.addCell(cell);
        }
    }

    private String formatAmount(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.UK).format(value);
    }
}

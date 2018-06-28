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
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static java.time.format.DateTimeFormatter.ofPattern;

public class ShopifyReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyReportGenerator.class);

    public static final String DATE_FORMAT = "dd-MMM-yyyy";

    private static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font PAGE_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE, BaseColor.BLACK);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
    private static final String EYE_LOGO_URL = "https://cdn.shopify.com/s/files/1/0016/4996/7140/files/Eye_-_ball_and_name.png";
    private static final float ONE_HUNDRED_PERCENT = 100f;
    private static final float SPACING = 25f;

    private static final BaseColor TABLE_HEADER_BG = new BaseColor(10,49,82, 1);
    private static final String NAME = "Andrew MacBean";
    private static final String REFERENCE_PREFIX = "EYE-MACB-";
    private static final String UK = "GB";
    private static final String IRELAND = "IE";

    private ShopifyClient shopifyClient = new ShopifyClient();

    private Document document;
    private String dateFrom;
    private String dateTo;
    private String reference;

    public byte[] generateAndrewCommissionReport(ZonedDateTime from, ZonedDateTime to) throws IOException {
        final Orders orders = shopifyClient.getAllOrders(from, to);
        this.dateFrom = DateTimeFormatter.ofPattern(DATE_FORMAT).format(from);
        this.dateTo = DateTimeFormatter.ofPattern(DATE_FORMAT).format(to);
        this.reference = REFERENCE_PREFIX + DateTimeFormatter.ofPattern("yyyyMM").format(to);
        byte[] pdfData = getPdfByteData(orders);
        writePdfToFile(reference, pdfData);
        return pdfData;
    }

    private byte[] getPdfByteData(Orders orders) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            document = new Document(PageSize.A4);
            final PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
            pdfWriter.setPageEvent(new PageEventHandler());
            document.open();
            addHeader();
            BigDecimal totalCommissionDue = addOrderCommissionBreakdown(orders);
            addFooter(totalCommissionDue);
            document.close();
        } catch (DocumentException | IOException e) {
            LOGGER.error("Problem generating PDF file", e);
        } finally {
            document.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void addHeader() throws DocumentException, IOException {
        document.add(createPageTitle("Sales Commission Invoice"));

        final Image eyeLogo = Image.getInstance(new URL(EYE_LOGO_URL));
        eyeLogo.scaleToFit(75f,75f);
        eyeLogo.setAlignment(ALIGN_CENTER);
        document.add(eyeLogo);

        final PdfPTable eyeRacketsInfoTable = new PdfPTable(1);
        eyeRacketsInfoTable.setWidthPercentage(80f);
        eyeRacketsInfoTable.addCell(createTableCell("TO:", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("Eye Rackets International B.V.", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("Zekeringstraat 17, 1014 BM", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("Amsterdam", ALIGN_LEFT, false));
        eyeRacketsInfoTable.addCell(createTableCell("The Netherlands", ALIGN_LEFT, false));

        final PdfPTable andrewInfoTable = new PdfPTable(1);
        andrewInfoTable.setWidthPercentage(100f);
        andrewInfoTable.addCell(createTableCell("FROM:", ALIGN_LEFT, false));
        andrewInfoTable.addCell(createTableCell(NAME, ALIGN_LEFT, false));
        andrewInfoTable.addCell(createTableCell("5 Sandpiper Crescent", ALIGN_LEFT, false));
        andrewInfoTable.addCell(createTableCell("Coatbridge", ALIGN_LEFT, false));
        andrewInfoTable.addCell(createTableCell("ML5 4UW", ALIGN_LEFT, false));
        andrewInfoTable.addCell(createTableCell("Scotland", ALIGN_LEFT, false));

        final PdfPTable invoiceInfoTable = new PdfPTable(1);
        invoiceInfoTable.setWidthPercentage(100f);
        invoiceInfoTable.addCell(createTableCell("INVOICE DATE:", ALIGN_RIGHT, false));
        invoiceInfoTable.addCell(createTableCell("REFERENCE:", ALIGN_RIGHT, false));
        invoiceInfoTable.addCell(createTableCell("DATE FROM:", ALIGN_RIGHT, false));
        invoiceInfoTable.addCell(createTableCell("DATE TO:", ALIGN_RIGHT, false));

        final PdfPTable invoiceInfoValuesTable = new PdfPTable(1);
        invoiceInfoValuesTable.setWidthPercentage(100f);
        invoiceInfoValuesTable.addCell(createTableCell(ofPattern(DATE_FORMAT).format(LocalDate.now()), ALIGN_RIGHT, false));
        invoiceInfoValuesTable.addCell(createTableCell(reference, ALIGN_RIGHT, false));
        invoiceInfoValuesTable.addCell(createTableCell(dateFrom, ALIGN_RIGHT, false));
        invoiceInfoValuesTable.addCell(createTableCell(dateTo, ALIGN_RIGHT, false));

        final PdfPTable headerTable = createFullWidthTable(4,3,3,3);
        headerTable.addCell(createTableCell(eyeRacketsInfoTable, false));
        headerTable.addCell(createTableCell(andrewInfoTable, false));
        headerTable.addCell(createTableCell(invoiceInfoTable, false));
        headerTable.addCell(createTableCell(invoiceInfoValuesTable, false));

        document.add(headerTable);
    }

    private BigDecimal addOrderCommissionBreakdown(Orders orders) throws DocumentException {
        final PdfPTable commissionBreakdownTable = createFullWidthTable(4,6,3,4,4,4,4,2,4);
        commissionBreakdownTable.setWidthPercentage(100f);

        commissionBreakdownTable.addCell(createTableHeaderCell("Order #"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Order Date"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Country"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Order Total"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Tax"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Shipping"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Sales Amount"));
        commissionBreakdownTable.addCell(createTableHeaderCell("%"));
        commissionBreakdownTable.addCell(createTableHeaderCell("Commission"));

        BigDecimal totalPriceAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        BigDecimal totalShippingAmount = BigDecimal.ZERO;
        BigDecimal totalSalesAmount = BigDecimal.ZERO;
        BigDecimal totalCommissionPayable = BigDecimal.ZERO;

        for (Order order : orders.getOrders()) {
            if (!"0.00".equals(order.getTotalPrice())) {
                commissionBreakdownTable.addCell(createTableCell(order.getName()));

                final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
                commissionBreakdownTable.addCell(createTableCell(DateTimeFormatter.ofPattern("dd-MM-yy hh:mm").format(orderDate)));

                commissionBreakdownTable.addCell(order.getShippingAddress().getCountryCode());

                final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
                totalPriceAmount = totalPriceAmount.add(totalPrice);
                commissionBreakdownTable.addCell(createTableCell(totalPrice, ALIGN_RIGHT));

                final BigDecimal tax = new BigDecimal(order.getTotalTax());
                totalTaxAmount = totalTaxAmount.add(tax);
                commissionBreakdownTable.addCell(createTableCell(tax, ALIGN_RIGHT));

                BigDecimal shipping = BigDecimal.ZERO;
                if (order.getShippingLines() != null && order.getShippingLines().size() > 0) {
                    shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
                }
                totalShippingAmount = totalShippingAmount.add(shipping);
                commissionBreakdownTable.addCell(createTableCell(shipping, ALIGN_RIGHT));

                final BigDecimal salesAmount = totalPrice.subtract(tax).subtract(shipping);
                totalSalesAmount = totalSalesAmount.add(salesAmount);
                commissionBreakdownTable.addCell(createTableCell(salesAmount, ALIGN_RIGHT));

                final BigDecimal commissionRate = getCommissionRate(order.getShippingAddress().getCountryCode());
                commissionBreakdownTable.addCell(createTableCell(commissionRate.toString(), ALIGN_RIGHT));

                final BigDecimal commissionPayable  = salesAmount.multiply(commissionRate.divide(BigDecimal.valueOf(100)));
                totalCommissionPayable = totalCommissionPayable.add(commissionPayable);
                commissionBreakdownTable.addCell(createTableCell(commissionPayable, ALIGN_RIGHT));
            }
        }

        final PdfPCell totalsCell = createTableCell("Totals");
        totalsCell.setColspan(3);
        commissionBreakdownTable.addCell(totalsCell);
        commissionBreakdownTable.addCell(createTableCell(totalPriceAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalTaxAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalShippingAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalSalesAmount, ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(" ", ALIGN_RIGHT));
        commissionBreakdownTable.addCell(createTableCell(totalCommissionPayable, ALIGN_RIGHT));

        document.add(commissionBreakdownTable);

        return totalCommissionPayable;
    }

    private BigDecimal getCommissionRate(String countryCode) {
        if (countryCode.equalsIgnoreCase(UK) || countryCode.equalsIgnoreCase(IRELAND)) {
            return BigDecimal.valueOf(15);
        }
        else {
            return BigDecimal.valueOf(10);
        }
    }

    private void addFooter(BigDecimal totalCommissionDue) throws DocumentException {
        final PdfPTable footerTable = createFullWidthTable(5,1);

        footerTable.setWidthPercentage(100f);
        footerTable.addCell(createTableHeaderCell("Total Commission due to " + NAME + " for period " + dateFrom + " to " + dateTo));
        footerTable.addCell(createTableHeaderCell(totalCommissionDue, ALIGN_RIGHT));

        document.add(footerTable);
    }

    private void writePdfToFile(String name, byte[] pdfBytes) {
        final String filename = "/Users/Andrew/Desktop" + File.separatorChar + name + ".pdf";
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(pdfBytes);
            LOGGER.debug("File written to {}", filename);
        } catch (IOException ioe) {
            LOGGER.error("Unable to write PDF file to {} ", filename, ioe);
        }
    }

    //** iText convenience methods **//

    private Paragraph createPageTitle(String text) {
        final Paragraph pageTitle = new Paragraph(text, PAGE_TITLE_FONT);
        pageTitle.setAlignment(ALIGN_CENTER);
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

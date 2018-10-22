package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.model.Order;
import com.macbean.tech.shopify.model.Orders;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static com.itextpdf.text.Element.*;
import static java.time.format.DateTimeFormatter.ofPattern;

public class AndrewCommissionReportGenerator extends AbstractShopifyReportGenerator {

    private static final String NAME = "Andrew MacBean";

    private static final String BANK_NAME = "Royal Bank of Scotland";
    private static final String BANK_ACCOUNT_NAME = "MACBEAN A & L";
    private static final String BANK_SORT_CODE = "83-23-10";
    private static final String BANK_ACCOUNT_NO = "00249958";
    private static final String BANK_BIC = "RBOSGB2L";
    private static final String BANK_IBAN = "GB52RBOS83231000249958";

    private static final String UK = "GB";
    private static final String IRELAND = "IE";

    private BigDecimal totalCommissionDue = BigDecimal.ZERO;

    @Override
    String getTitle() {
        return "Sales Commission Invoice";
    }

    @Override
    String getReferencePrefix() {
        return "EYE-MACB-";
    }

    @Override
    void addHeader() throws DocumentException, IOException {
        document.add(getEyeLogo(75f,75f, ALIGN_CENTER));

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

    @Override
    void addContent() throws DocumentException, IOException {
        final Orders orders = shopifyClient.getAllOrders(from, to);

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

        for (Order order : orders.getOrders()) {
            if (!"0.00".equals(order.getTotalPrice())) {
                commissionBreakdownTable.addCell(createTableCell(order.getName()));

                final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
                commissionBreakdownTable.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

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
                totalCommissionDue = totalCommissionDue.add(commissionPayable);
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
        commissionBreakdownTable.addCell(createTableCell(totalCommissionDue, ALIGN_RIGHT));

        document.add(commissionBreakdownTable);
    }

    private BigDecimal getCommissionRate(String countryCode) {
        if (countryCode.equalsIgnoreCase(UK) || countryCode.equalsIgnoreCase(IRELAND)) {
            return BigDecimal.valueOf(15);
        }
        else {
            return BigDecimal.valueOf(10);
        }
    }

    @Override
    void addFooter() throws DocumentException, IOException {
        final PdfPTable miscDetailsTable = createFullWidthTable(3,1);

        final PdfPCell summaryCell = createTableHeaderCell("Payment Summary", ALIGN_CENTER);
        summaryCell.setColspan(2);
        miscDetailsTable.addCell(summaryCell);
        miscDetailsTable.addCell(createTableCell("Total Commission due for period " + dateFrom + " to " + dateTo));
        miscDetailsTable.addCell(createTableCell(totalCommissionDue, ALIGN_RIGHT));

        final BigDecimal miscTotals = addMiscItems(miscDetailsTable);

        final BigDecimal totalDue = totalCommissionDue.add(miscTotals);
        miscDetailsTable.addCell(createTableHeaderCell("TOTAL DUE"));
        miscDetailsTable.addCell(createTableHeaderCell(totalDue, ALIGN_RIGHT));
        document.add(miscDetailsTable);

        final PdfPTable paymentDetailsTable = createFullWidthTable(2);
        paymentDetailsTable.addCell(createTableCell("Bank", ALIGN_RIGHT, false));
        paymentDetailsTable.addCell(createTableCell(BANK_NAME, ALIGN_LEFT, false));
        paymentDetailsTable.addCell(createTableCell("Account Name", ALIGN_RIGHT, false));
        paymentDetailsTable.addCell(createTableCell(BANK_ACCOUNT_NAME, ALIGN_LEFT, false));
        paymentDetailsTable.addCell(createTableCell("Sort Code", ALIGN_RIGHT, false));
        paymentDetailsTable.addCell(createTableCell(BANK_SORT_CODE, ALIGN_LEFT, false));
        paymentDetailsTable.addCell(createTableCell("Account Number", ALIGN_RIGHT, false));
        paymentDetailsTable.addCell(createTableCell(BANK_ACCOUNT_NO, ALIGN_LEFT, false));
        paymentDetailsTable.addCell(createTableCell("BIC", ALIGN_RIGHT, false));
        paymentDetailsTable.addCell(createTableCell(BANK_BIC, ALIGN_LEFT, false));
        paymentDetailsTable.addCell(createTableCell("IBAN", ALIGN_RIGHT, false));
        paymentDetailsTable.addCell(createTableCell(BANK_IBAN, ALIGN_LEFT, false));
        document.add(paymentDetailsTable);
    }

    private BigDecimal addMiscItems(PdfPTable miscDetailsTable) {
        miscDetailsTable.addCell(createTableCell("GSuite Fees", ALIGN_LEFT));
        final BigDecimal gSuiteFees = new BigDecimal(3.30d);
        miscDetailsTable.addCell(createTableCell(gSuiteFees, ALIGN_RIGHT));

        miscDetailsTable.addCell(createTableCell("MyHermes Collections/Deliveries", ALIGN_LEFT));
        final BigDecimal myHermesCosts = new BigDecimal(2.79d + 2.79d + 3.99d);
        miscDetailsTable.addCell(createTableCell(myHermesCosts, ALIGN_RIGHT));

        miscDetailsTable.addCell(createTableCell("Printing & courier costs for EGCC Teamwear (20 shirts)", ALIGN_LEFT));
        final BigDecimal egcc = new BigDecimal(72d);
        miscDetailsTable.addCell(createTableCell(egcc, ALIGN_RIGHT));

        miscDetailsTable.addCell(createTableCell("Printing & courier costs for Abingdon Teamwear (23 shirts)", ALIGN_LEFT));
        final BigDecimal abingdon = new BigDecimal(81d);
        miscDetailsTable.addCell(createTableCell(egcc, ALIGN_RIGHT));

        miscDetailsTable.addCell(createTableCell("Flights for myself & Lisa Aitken to St Georges Hill", ALIGN_LEFT));
        final BigDecimal flights = new BigDecimal(120d);
        miscDetailsTable.addCell(createTableCell(flights, ALIGN_RIGHT));

        miscDetailsTable.addCell(createTableCell("Hotel for myself, Daan, Bo & Lisa Aitken at St Georges Hill", ALIGN_LEFT));
        final BigDecimal hotel = new BigDecimal(700d);
        miscDetailsTable.addCell(createTableCell(hotel, ALIGN_RIGHT));

        return gSuiteFees.add(myHermesCosts).add(egcc).add(abingdon).add(flights).add(hotel);
    }
}

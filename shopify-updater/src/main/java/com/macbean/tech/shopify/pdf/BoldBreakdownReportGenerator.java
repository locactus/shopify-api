package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.model.Order;
import com.macbean.tech.shopify.model.Orders;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_RIGHT;

public class BoldBreakdownReportGenerator extends AbstractShopifyReportGenerator {

    private static final String TRADE_TAG = "trade";
    private static final String PLATINUM_TAG = "platinum";
    private static final String GOLD_TAG = "gold";
    private static final String AFFILIATE_TAG = "affiliate";

    @Override
    Rectangle getPageSize() {
        return PageSize.A4.rotate();
    }

    @Override
    String getTitle() {
        return "Bold Breakdown Sales Report";
    }

    @Override
    String getReferencePrefix() {
        return "EYE-BOLD-";
    }

    @Override
    void addHeader() throws DocumentException, IOException {
        document.add(getEyeLogo(75f,75f, ALIGN_CENTER));
    }

    @Override
    void addContent() throws DocumentException, IOException {
        final Orders orders = shopifyClient.getAllOrders(from, to);

        final PdfPTable freeTable = getPricingGroupTable("Free of Charge Orders");
        final PdfPTable tradeTable = getPricingGroupTable("Trade Sales");
        final PdfPTable platinumTable = getPricingGroupTable("Platinum Sales");
        final PdfPTable goldTable = getPricingGroupTable("Gold Sales");
        final PdfPTable affiliateTable = getPricingGroupTable("Affiliate Sales");
        final PdfPTable directTable = getPricingGroupTable("Direct Sales");

        for (Order order : orders.getOrders()) {
            if ("0.00".equals(order.getTotalPrice())) {
                addOrderToTable(freeTable, order);
            }
            else if (order.getCustomer().getTags().contains(TRADE_TAG)) {
                addOrderToTable(tradeTable, order);
            }
            else if (order.getCustomer().getTags().contains(PLATINUM_TAG)) {
                addOrderToTable(platinumTable, order);
            }
            else if (order.getCustomer().getTags().contains(GOLD_TAG)) {
                addOrderToTable(goldTable, order);
            }
            else if (order.getCustomer().getTags().contains(AFFILIATE_TAG)) {
                addOrderToTable(affiliateTable, order);
            }
            else {
                addOrderToTable(directTable, order);
            }
        }

        document.add(freeTable);
        document.add(tradeTable);
        document.add(platinumTable);
        document.add(goldTable);
        document.add(affiliateTable);
        document.add(directTable);
    }

    private PdfPCell[] getPricingTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Order #"));
        headers.add(createTableHeaderCell("Order Date"));
        headers.add(createTableHeaderCell("Customer Name"));
        headers.add(createTableHeaderCell("Country"));
        headers.add(createTableHeaderCell("Order Total"));
        headers.add(createTableHeaderCell("Tax"));
        headers.add(createTableHeaderCell("Shipping"));
        headers.add(createTableHeaderCell("Sales Amount"));
        headers.add(createTableHeaderCell("Discount"));
        headers.add(createTableHeaderCell("Customer Tags"));
        headers.add(createTableHeaderCell("Order Tags"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPTable getPricingGroupTable(String name) {
        final PdfPTable table = createFullWidthTable(4,6,6,3,4,4,4,4,4,4,4);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell(name, ALIGN_CENTER);
        titleCell.setColspan(getPricingTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getPricingTableHeaders());
        return table;
    }

    private void addOrderToTable(PdfPTable table, Order order) {
        table.addCell(createTableCell(order.getName()));

        final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
        table.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

        table.addCell(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        table.addCell(order.getShippingAddress().getCountryCode());

        final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
        //totalPriceAmount = totalPriceAmount.add(totalPrice);
        table.addCell(createTableCell(totalPrice, ALIGN_RIGHT));

        final BigDecimal tax = new BigDecimal(order.getTotalTax());
        //totalTaxAmount = totalTaxAmount.add(tax);
        table.addCell(createTableCell(tax, ALIGN_RIGHT));

        BigDecimal shipping = BigDecimal.ZERO;
        if (order.getShippingLines() != null && order.getShippingLines().size() > 0) {
            shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
        }
        //totalShippingAmount = totalShippingAmount.add(shipping);
        table.addCell(createTableCell(shipping, ALIGN_RIGHT));

        final BigDecimal salesAmount = totalPrice.subtract(tax).subtract(shipping);
        //totalSalesAmount = totalSalesAmount.add(salesAmount);
        table.addCell(createTableCell(salesAmount, ALIGN_RIGHT));

        table.addCell(createTableCell(new BigDecimal(order.getTotalDiscounts()), ALIGN_RIGHT));
        table.addCell(createTableTagCell(order.getCustomer().getTags()));
        table.addCell(createTableTagCell(order.getTags()));
    }

    @Override
    void addFooter() throws DocumentException, IOException {

    }
}

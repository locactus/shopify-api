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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;

public class BoldBreakdownReportGenerator extends AbstractShopifyReportGenerator {

    private static final String TRADE_TAG = "trade";
    private static final String PLATINUM_TAG = "platinum";
    private static final String GOLD_TAG = "gold";
    private static final String AFFILIATE_TAG = "affiliate";

    private static final String FREE_TAG = "free";
    private static final String DIRECT_TAG = "direct";

    private Map<String, BoldTableTotals> totals = new HashMap<>(6);

    private BoldTableTotals overallTotals = new BoldTableTotals();

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

        final PdfPTable tradeTable = getPricingGroupTable("Trade Sales");
        final PdfPTable platinumTable = getPricingGroupTable("Platinum Sales");
        final PdfPTable goldTable = getPricingGroupTable("Gold Sales");
        final PdfPTable affiliateTable = getPricingGroupTable("Affiliate Sales");
        final PdfPTable directTable = getPricingGroupTable("Direct Sales");
        final PdfPTable freeTable = getPricingGroupTable("Free of Charge Orders");

        totals.put(TRADE_TAG, new BoldTableTotals());
        totals.put(PLATINUM_TAG, new BoldTableTotals());
        totals.put(GOLD_TAG, new BoldTableTotals());
        totals.put(AFFILIATE_TAG, new BoldTableTotals());
        totals.put(DIRECT_TAG, new BoldTableTotals());
        totals.put(FREE_TAG, new BoldTableTotals());

        for (Order order : orders.getOrders()) {
            if ("0.00".equals(order.getTotalPrice())) {
                addOrderToTable(totals.get(FREE_TAG), freeTable, order);
            }
            else if (order.getCustomer().getTags().contains(TRADE_TAG)) {
                addOrderToTable(totals.get(TRADE_TAG), tradeTable, order);
            }
            else if (order.getCustomer().getTags().contains(PLATINUM_TAG)) {
                addOrderToTable(totals.get(PLATINUM_TAG), platinumTable, order);
            }
            else if (order.getCustomer().getTags().contains(GOLD_TAG)) {
                addOrderToTable(totals.get(GOLD_TAG), goldTable, order);
            }
            else if (order.getCustomer().getTags().contains(AFFILIATE_TAG)) {
                addOrderToTable(totals.get(AFFILIATE_TAG), affiliateTable, order);
            }
            else {
                addOrderToTable(totals.get(DIRECT_TAG), directTable, order);
            }
        }

        addTotalRowToTable(totals.get(TRADE_TAG), tradeTable);
        document.add(tradeTable);

        addTotalRowToTable(totals.get(PLATINUM_TAG), platinumTable);
        document.add(platinumTable);

        addTotalRowToTable(totals.get(GOLD_TAG), goldTable);
        document.add(goldTable);

        addTotalRowToTable(totals.get(AFFILIATE_TAG), affiliateTable);
        document.add(affiliateTable);

        addTotalRowToTable(totals.get(DIRECT_TAG), directTable);
        document.add(directTable);

        addTotalRowToTable(totals.get(FREE_TAG), freeTable);
        document.add(freeTable);
    }

    private PdfPTable getSummaryTable() {
        final PdfPTable table = createFullWidthTable(7);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell("Sales Breakdown Summary", ALIGN_CENTER);
        titleCell.setColspan(getSummaryTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getSummaryTableHeaders());
        addCellsToTable(table, getSummaryCells(TRADE_TAG, totals.get(TRADE_TAG)));
        addCellsToTable(table, getSummaryCells(PLATINUM_TAG, totals.get(PLATINUM_TAG)));
        addCellsToTable(table, getSummaryCells(GOLD_TAG, totals.get(GOLD_TAG)));
        addCellsToTable(table, getSummaryCells(AFFILIATE_TAG, totals.get(AFFILIATE_TAG)));
        addCellsToTable(table, getSummaryCells(DIRECT_TAG, totals.get(DIRECT_TAG)));
        addCellsToTable(table, getSummaryCells(FREE_TAG, totals.get(FREE_TAG)));

        addCellsToTable(table, createTableCell("TOTALS"));
        addCellsToTable(table, createTableCell(String.valueOf(overallTotals.getOrderCount()), ALIGN_CENTER));
        addCellsToTable(table, createTableCell(overallTotals.getDiscountTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getOrderTotalTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getTaxTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getShippingTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getSalesTotal(), ALIGN_RIGHT));
        return table;
    }

    private PdfPCell[] getSummaryTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Sales Group"));
        headers.add(createTableHeaderCell("No Of Orders"));
        headers.add(createTableHeaderCell("Discount"));
        headers.add(createTableHeaderCell("Order Total"));
        headers.add(createTableHeaderCell("Tax"));
        headers.add(createTableHeaderCell("Shipping"));
        headers.add(createTableHeaderCell("Sales Amount"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPCell[] getSummaryCells(String description, BoldTableTotals tableTotals) {
        final List<PdfPCell> summaryCells = new ArrayList<>();

        summaryCells.add(createTableCell(description));
        overallTotals.addOrders(tableTotals.getOrderCount());
        summaryCells.add(createTableCell(String.valueOf(tableTotals.getOrderCount()), ALIGN_CENTER));

        overallTotals.addDiscount(tableTotals.getDiscountTotal());
        summaryCells.add(createTableCell(tableTotals.getDiscountTotal(), ALIGN_RIGHT));

        overallTotals.addOrderTotal(tableTotals.getOrderTotalTotal());
        summaryCells.add(createTableCell(tableTotals.getOrderTotalTotal(), ALIGN_RIGHT));

        overallTotals.addTax(tableTotals.getTaxTotal());
        summaryCells.add(createTableCell(tableTotals.getTaxTotal(), ALIGN_RIGHT));

        overallTotals.addShipping(tableTotals.getShippingTotal());
        summaryCells.add(createTableCell(tableTotals.getShippingTotal(), ALIGN_RIGHT));

        overallTotals.addSales(tableTotals.getSalesTotal());
        summaryCells.add(createTableCell(tableTotals.getSalesTotal(), ALIGN_RIGHT));

        return summaryCells.toArray(new PdfPCell[0]);
    }

    private PdfPCell[] getPricingTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Ord #"));
        headers.add(createTableHeaderCell("Order Date"));
        headers.add(createTableHeaderCell("Customer Name"));
        headers.add(createTableHeaderCell("Co."));
        headers.add(createTableHeaderCell("Customer Tags"));
        headers.add(createTableHeaderCell("Order Tags"));
        headers.add(createTableHeaderCell("Discount"));
        headers.add(createTableHeaderCell("Order Total"));
        headers.add(createTableHeaderCell("Tax"));
        headers.add(createTableHeaderCell("Shipping"));
        headers.add(createTableHeaderCell("Sales Amount"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPTable getPricingGroupTable(String name) {
        final PdfPTable table = createFullWidthTable(2,6,6,2,5,5,3,3,3,3,3);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell(name, ALIGN_CENTER);
        titleCell.setColspan(getPricingTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getPricingTableHeaders());
        return table;
    }

    private void addTotalRowToTable(BoldTableTotals totals, PdfPTable table) {
        final PdfPCell titleCell = createTableCell("Totals", ALIGN_LEFT);
        titleCell.setColspan(getPricingTableHeaders().length - 5);
        table.addCell(titleCell);

        final PdfPCell discountCell = createTableCell(totals.getDiscountTotal(), ALIGN_RIGHT);
        table.addCell(discountCell);

        final PdfPCell orderTotalCell = createTableCell(totals.getOrderTotalTotal(), ALIGN_RIGHT);
        table.addCell(orderTotalCell);

        final PdfPCell taxCell = createTableCell(totals.getTaxTotal(), ALIGN_RIGHT);
        table.addCell(taxCell);

        final PdfPCell shippingCell = createTableCell(totals.getShippingTotal(), ALIGN_RIGHT);
        table.addCell(shippingCell);

        final PdfPCell totalCell = createTableCell(totals.getSalesTotal(), ALIGN_RIGHT);
        table.addCell(totalCell);
    }

    private BigDecimal addOrderToTable(BoldTableTotals tableTotals, PdfPTable table, Order order) {
        tableTotals.addOrder();
        table.addCell(createTableCell(order.getName()));

        final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
        table.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

        table.addCell(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        table.addCell(order.getShippingAddress().getCountryCode());

        table.addCell(createTableTagCell(order.getCustomer().getTags()));
        table.addCell(createTableTagCell(order.getTags()));

        BigDecimal discount = new BigDecimal(order.getTotalDiscounts());
        tableTotals.addDiscount(discount);
        table.addCell(createTableCell(discount, ALIGN_RIGHT));

        final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
        tableTotals.addOrderTotal(totalPrice);
        table.addCell(createTableCell(totalPrice, ALIGN_RIGHT));

        final BigDecimal tax = new BigDecimal(order.getTotalTax());
        tableTotals.addTax(tax);
        table.addCell(createTableCell(tax, ALIGN_RIGHT));

        BigDecimal shipping = BigDecimal.ZERO;
        if (order.getShippingLines() != null && order.getShippingLines().size() > 0) {
            shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
        }
        tableTotals.addShipping(shipping);
        table.addCell(createTableCell(shipping, ALIGN_RIGHT));

        final BigDecimal salesAmount = totalPrice.subtract(tax).subtract(shipping);
        tableTotals.addSales(salesAmount);
        table.addCell(createTableCell(salesAmount, ALIGN_RIGHT));

        return salesAmount;
    }

    @Override
    void addFooter() throws DocumentException, IOException {
        final PdfPTable breakdownTable = getSummaryTable();
        document.add(breakdownTable);
    }

    private class BoldTableTotals {

        int orderCount = 0;
        BigDecimal orderTotalTotal = BigDecimal.ZERO;
        BigDecimal salesTotal = BigDecimal.ZERO;
        BigDecimal discountTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal shippingTotal = BigDecimal.ZERO;

        public BigDecimal getSalesTotal() {
            return salesTotal;
        }

        public void addSales(BigDecimal salesTotal) {
            this.salesTotal = this.salesTotal.add(salesTotal);
        }

        public BigDecimal getDiscountTotal() {
            return discountTotal;
        }

        public void addDiscount(BigDecimal discountTotal) {
            this.discountTotal = this.discountTotal.add(discountTotal);
        }

        public BigDecimal getTaxTotal() {
            return taxTotal;
        }

        public void addTax(BigDecimal taxTotal) {
            this.taxTotal = this.taxTotal.add(taxTotal);
        }

        public BigDecimal getShippingTotal() {
            return shippingTotal;
        }

        public void addShipping(BigDecimal shippingTotal) {
            this.shippingTotal = this.shippingTotal.add(shippingTotal);
        }

        public BigDecimal getOrderTotalTotal() {
            return orderTotalTotal;
        }

        public void addOrderTotal(BigDecimal orderTotal) {
            this.orderTotalTotal = this.orderTotalTotal.add(orderTotal);
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void addOrder() {
            this.orderCount++;
        }

        public void addOrders(int orderCount) {
            this.orderCount = this.orderCount + orderCount;
        }
    }
}

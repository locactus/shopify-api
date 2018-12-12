package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.model.DiscountApplications;
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

    private Map<String, BoldTableTotals> totals = new HashMap<>(6);
    private  Map<Long, BigDecimal> allCostsByVariantId = new HashMap<>(100);

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
        allCostsByVariantId = shopifyClient.getAllCostsByVariantId();

        final Orders orders = shopifyClient.getAllOrders(from, to);

        final PdfPTable tradeTable = getPricingGroupTable(ShopifyConstants.TRADE_TAG);
        final PdfPTable platinumTable = getPricingGroupTable(ShopifyConstants.PLATINUM_TAG);
        final PdfPTable goldTable = getPricingGroupTable(ShopifyConstants.GOLD_TAG);
        final PdfPTable affiliateTable = getPricingGroupTable(ShopifyConstants.AFFILIATE_TAG);
        final PdfPTable directTable = getPricingGroupTable(ShopifyConstants.DIRECT_TAG);
        final PdfPTable freeTable = getPricingGroupTable(ShopifyConstants.FREE_TAG);

        totals.put(ShopifyConstants.TRADE_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.PLATINUM_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.GOLD_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.AFFILIATE_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.DIRECT_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.FREE_TAG, new BoldTableTotals());

        for (Order order : orders.getOrders()) {
            if ("0.00".equals(order.getTotalPrice())) {
                addOrderToTable(totals.get(ShopifyConstants.FREE_TAG), freeTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.TRADE_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.TRADE_TAG), tradeTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.PLATINUM_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.PLATINUM_TAG), platinumTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.GOLD_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.GOLD_TAG), goldTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.AFFILIATE_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.AFFILIATE_TAG), affiliateTable, order);
            }
            else {
                addOrderToTable(totals.get(ShopifyConstants.DIRECT_TAG), directTable, order);
            }
        }

        addTotalRowToTable(totals.get(ShopifyConstants.TRADE_TAG), tradeTable);
        document.add(tradeTable);
        document.newPage();

        addTotalRowToTable(totals.get(ShopifyConstants.PLATINUM_TAG), platinumTable);
        document.add(platinumTable);
        document.newPage();

        addTotalRowToTable(totals.get(ShopifyConstants.GOLD_TAG), goldTable);
        document.add(goldTable);
        document.newPage();

        addTotalRowToTable(totals.get(ShopifyConstants.AFFILIATE_TAG), affiliateTable);
        document.add(affiliateTable);
        document.newPage();

        addTotalRowToTable(totals.get(ShopifyConstants.DIRECT_TAG), directTable);
        document.add(directTable);
        document.newPage();

        addTotalRowToTable(totals.get(ShopifyConstants.FREE_TAG), freeTable);
        document.add(freeTable);
        document.newPage();
    }

    private PdfPCell[] getPricingTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Ord #"));
        headers.add(createTableHeaderCell("Order Date"));
        headers.add(createTableHeaderCell("Customer Name"));
        headers.add(createTableHeaderCell("Co."));
        headers.add(createTableHeaderCell("Tags"));
        headers.add(createTableHeaderCell("Discount"));
        headers.add(createTableHeaderCell("Order Total"));
        headers.add(createTableHeaderCell("Tax"));
        headers.add(createTableHeaderCell("Shipping"));
        headers.add(createTableHeaderCell("Sale Amount"));
        headers.add(createTableHeaderCell("Cost"));
        headers.add(createTableHeaderCell("Comm."));
        headers.add(createTableHeaderCell("Profit"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPTable getPricingGroupTable(String name) {
        final PdfPTable table = createFullWidthTable(3,6,7,2,5,4,4,4,4,4,4,4,4);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell(name.toUpperCase() + " SALES", ALIGN_CENTER);
        titleCell.setColspan(getPricingTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getPricingTableHeaders());
        return table;
    }

    private void addTotalRowToTable(BoldTableTotals totals, PdfPTable table) {
        final PdfPCell titleCell = createTableCell("TOTALS", ALIGN_LEFT);
        titleCell.setColspan(getPricingTableHeaders().length - 8);
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

        final PdfPCell costCell = createTableCell(totals.getCostTotal(), ALIGN_RIGHT);
        table.addCell(costCell);

        final PdfPCell commissionCell = createTableCell(totals.getCommissionTotal(), ALIGN_RIGHT);
        table.addCell(commissionCell);

        final PdfPCell profitCell = createTableCell(totals.getProfitTotal(), ALIGN_RIGHT);
        table.addCell(profitCell);
    }

    private BigDecimal addOrderToTable(BoldTableTotals tableTotals, PdfPTable table, Order order) throws IOException {
        tableTotals.addOrder();
        table.addCell(createTableCell(order.getName()));

        final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
        table.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

        table.addCell(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        table.addCell(order.getShippingAddress().getCountryCode());

        table.addCell(createTableTagCell(order.getTags()));

        BigDecimal discount = new BigDecimal(order.getTotalDiscounts());
        tableTotals.addDiscount(discount);
        table.addCell(createTableCell(discount, ALIGN_RIGHT));

        final List<DiscountApplications> discountApplications = order.getDiscountApplications();

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

        final BigDecimal totalOrderCost = shopifyClient.calculateTotalOrderCost(order, allCostsByVariantId);
        tableTotals.addCost(totalOrderCost);
        table.addCell(createTableCell(totalOrderCost, ALIGN_RIGHT));

        final BigDecimal orderCommission = AndrewCommissionReportGenerator.calculateCommission(salesAmount, order.getShippingAddress().getCountryCode());
        tableTotals.addCommission(orderCommission);
        table.addCell(createTableCell(orderCommission, ALIGN_RIGHT));

        final BigDecimal totalOrderProfit = salesAmount.subtract(totalOrderCost).subtract(orderCommission);
        tableTotals.addProfit(totalOrderProfit);
        table.addCell(createTableCell(totalOrderProfit, ALIGN_RIGHT));

        return salesAmount;
    }

    private PdfPTable getSummaryTable() {
        final PdfPTable table = createFullWidthTable(10);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell("SALES BREAKDOWN SUMMARY", ALIGN_CENTER);
        titleCell.setColspan(getSummaryTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getSummaryTableHeaders());
        addCellsToTable(table, getSummaryCells(ShopifyConstants.TRADE_TAG, totals.get(ShopifyConstants.TRADE_TAG)));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.PLATINUM_TAG, totals.get(ShopifyConstants.PLATINUM_TAG)));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.GOLD_TAG, totals.get(ShopifyConstants.GOLD_TAG)));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.AFFILIATE_TAG, totals.get(ShopifyConstants.AFFILIATE_TAG)));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.DIRECT_TAG, totals.get(ShopifyConstants.DIRECT_TAG)));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.FREE_TAG, totals.get(ShopifyConstants.FREE_TAG)));

        addCellsToTable(table, createTableCell("TOTALS"));
        addCellsToTable(table, createTableCell(String.valueOf(overallTotals.getOrderCount()), ALIGN_CENTER));
        addCellsToTable(table, createTableCell(overallTotals.getDiscountTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getOrderTotalTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getTaxTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getShippingTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getSalesTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getCostTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getCommissionTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getProfitTotal(), ALIGN_RIGHT));
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
        headers.add(createTableHeaderCell("Total Cost"));
        headers.add(createTableHeaderCell("Total Commission"));
        headers.add(createTableHeaderCell("Total Profit"));
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

        overallTotals.addCost(tableTotals.getCostTotal());
        summaryCells.add(createTableCell(tableTotals.getCostTotal(), ALIGN_RIGHT));

        overallTotals.addCommission(tableTotals.getCommissionTotal());
        summaryCells.add(createTableCell(tableTotals.getCommissionTotal(), ALIGN_RIGHT));

        overallTotals.addProfit(tableTotals.getProfitTotal());
        summaryCells.add(createTableCell(tableTotals.getProfitTotal(), ALIGN_RIGHT));

        return summaryCells.toArray(new PdfPCell[0]);
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
        BigDecimal costTotal = BigDecimal.ZERO;
        BigDecimal commissionTotal = BigDecimal.ZERO;
        BigDecimal profitTotal = BigDecimal.ZERO;

        BigDecimal getSalesTotal() {
            return salesTotal;
        }

        void addSales(BigDecimal salesTotal) {
            this.salesTotal = this.salesTotal.add(salesTotal);
        }

        BigDecimal getDiscountTotal() {
            return discountTotal;
        }

        void addDiscount(BigDecimal discountTotal) {
            this.discountTotal = this.discountTotal.add(discountTotal);
        }

        BigDecimal getTaxTotal() {
            return taxTotal;
        }

        void addTax(BigDecimal taxTotal) {
            this.taxTotal = this.taxTotal.add(taxTotal);
        }

        BigDecimal getShippingTotal() {
            return shippingTotal;
        }

        void addShipping(BigDecimal shippingTotal) {
            this.shippingTotal = this.shippingTotal.add(shippingTotal);
        }

        BigDecimal getOrderTotalTotal() {
            return orderTotalTotal;
        }

        void addOrderTotal(BigDecimal orderTotal) {
            this.orderTotalTotal = this.orderTotalTotal.add(orderTotal);
        }

        int getOrderCount() {
            return orderCount;
        }

        void addOrder() {
            this.orderCount++;
        }

        void addOrders(int orderCount) {
            this.orderCount = this.orderCount + orderCount;
        }

        public BigDecimal getCostTotal() {
            return costTotal;
        }

        public void addCost(BigDecimal cost) {
            this.costTotal = this.costTotal.add(cost);
        }

        public BigDecimal getProfitTotal() {
            return profitTotal;
        }

        public void addProfit(BigDecimal profit) {
            this.profitTotal = this.profitTotal.add(profit);
        }

        public BigDecimal getCommissionTotal() {
            return commissionTotal;
        }

        public void addCommission(BigDecimal commission) {
            this.commissionTotal = this.commissionTotal.add(commission);
        }
    }
}

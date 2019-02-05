package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.model.Order;
import com.macbean.tech.shopify.model.Orders;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itextpdf.text.Element.*;

public class BoldBreakdownReportGenerator extends AbstractShopifyReportGenerator {

    private final int SCALE = 2;
    private final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private final RoundingMode ROUNDING = RoundingMode.HALF_UP;

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
        final PdfPTable digitalTable = getPricingGroupTable(ShopifyConstants.DIGITAL_TAG);

        totals.put(ShopifyConstants.TRADE_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.PLATINUM_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.GOLD_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.AFFILIATE_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.DIRECT_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.FREE_TAG, new BoldTableTotals());
        totals.put(ShopifyConstants.DIGITAL_TAG, new BoldTableTotals());

        for (Order order : orders.getOrders()) {
            if (order.getShippingAddress() == null) {
                addOrderToTable(totals.get(ShopifyConstants.DIGITAL_TAG), digitalTable, order);
            } else if ("0.00".equals(order.getTotalPrice())) {
                addOrderToTable(totals.get(ShopifyConstants.FREE_TAG), freeTable, order);
            } else if (order.getCustomer().getTags().contains(ShopifyConstants.TRADE_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.TRADE_TAG), tradeTable, order);
            } else if (order.getCustomer().getTags().contains(ShopifyConstants.PLATINUM_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.PLATINUM_TAG), platinumTable, order);
            } else if (order.getCustomer().getTags().contains(ShopifyConstants.GOLD_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.GOLD_TAG), goldTable, order);
            } else if (order.getCustomer().getTags().contains(ShopifyConstants.AFFILIATE_TAG)) {
                addOrderToTable(totals.get(ShopifyConstants.AFFILIATE_TAG), affiliateTable, order);
            } else {
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

        addTotalRowToTable(totals.get(ShopifyConstants.DIGITAL_TAG), digitalTable);
        document.add(digitalTable);
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
        headers.add(createTableHeaderCell("Total exl VAT"));
        headers.add(createTableHeaderCell("Shipping"));
        headers.add(createTableHeaderCell("Sale Amount"));
        headers.add(createTableHeaderCell("Item Costs"));
        headers.add(createTableHeaderCell("PackNed"));
        headers.add(createTableHeaderCell("Commiss."));
        headers.add(createTableHeaderCell("Profit Amount"));
        headers.add(createTableHeaderCell("Profit Margin"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPTable getPricingGroupTable(String name) {
        final PdfPTable table = createFullWidthTable(3,6,7,2,5,4,4,4,4,4,4,4,4,4);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell(name.toUpperCase() + " SALES", ALIGN_CENTER);
        titleCell.setColspan(getPricingTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getPricingTableHeaders());
        return table;
    }

    private void addTotalRowToTable(BoldTableTotals totals, PdfPTable table) {
        final PdfPCell titleCell = createTableCell("TOTALS", ALIGN_LEFT);
        titleCell.setColspan(getPricingTableHeaders().length - 9);
        table.addCell(titleCell);

        final PdfPCell discountCell = createTableCell("", ALIGN_RIGHT);
        table.addCell(discountCell);

        final PdfPCell orderTotalCell = createTableCell(totals.getOrderTotalTotal(), ALIGN_RIGHT);
        table.addCell(orderTotalCell);

        final PdfPCell shippingCell = createTableCell(totals.getShippingTotal(), ALIGN_RIGHT);
        table.addCell(shippingCell);

        final PdfPCell totalCell = createTableCell(totals.getSalesTotal(), ALIGN_RIGHT);
        table.addCell(totalCell);

        final PdfPCell costCell = createTableCell(totals.getCostTotal(), ALIGN_RIGHT);
        table.addCell(costCell);

        final PdfPCell packNedCell = createTableCell(totals.getPackNedTotal(), ALIGN_RIGHT);
        table.addCell(packNedCell);

        final PdfPCell commissionCell = createTableCell(totals.getCommissionTotal(), ALIGN_RIGHT);
        table.addCell(commissionCell);

        final PdfPCell profitCell = createTableCell(totals.getProfitTotal(), ALIGN_RIGHT);
        table.addCell(profitCell);

        final PdfPCell profitMarginCell = createTableCell("", ALIGN_RIGHT);
        table.addCell(profitMarginCell);
    }

    private BigDecimal addOrderToTable(BoldTableTotals tableTotals, PdfPTable table, Order order) throws IOException {
        tableTotals.addOrder();
        table.addCell(createTableCell(order.getName()));

        final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
        table.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

        table.addCell(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        String countryCode = order.getShippingAddress() != null ? order.getShippingAddress().getCountryCode() : ShopifyConstants.NO_COUNTRY_AVAILABLE;
        table.addCell(countryCode);

        table.addCell(createTableTagCell(order.getTags()));

        final BigDecimal totalDiscount = new BigDecimal(order.getTotalDiscounts());
        final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
        final BigDecimal totalPriceBeforeDiscount = new BigDecimal(order.getTotalPrice()).add(totalDiscount);
        final BigDecimal percentageDiscount = totalDiscount.divide(totalPriceBeforeDiscount, SCALE, ROUNDING)
                .multiply(ONE_HUNDRED)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        final BigDecimal totalTax = new BigDecimal(order.getTotalTax());
        final BigDecimal totalPriceExlTax = totalPrice.subtract(totalTax);

        table.addCell(createTableCell(percentageDiscount.toPlainString()+"%", ALIGN_RIGHT));

        tableTotals.addOrderTotal(totalPriceExlTax);
        table.addCell(createTableCell(totalPriceExlTax, ALIGN_RIGHT));

        BigDecimal shipping = BigDecimal.ZERO;
        if (order.getShippingLines() != null && order.getShippingLines().size() > 0) {
            shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
        }
        tableTotals.addShipping(shipping);
        table.addCell(createTableCell(shipping, ALIGN_RIGHT));

        final BigDecimal salesAmount = totalPrice.subtract(totalTax).subtract(shipping);
        tableTotals.addSales(salesAmount);
        table.addCell(createTableCell(salesAmount, ALIGN_RIGHT));

        final BigDecimal totalOrderCost = shopifyClient.calculateTotalOrderCost(order, allCostsByVariantId);
        tableTotals.addCost(totalOrderCost);
        table.addCell(createTableCell(totalOrderCost, ALIGN_RIGHT));

        final BigDecimal packNedCosts = calculatePackNedCost(countryCode, totalPrice, shipping);
        tableTotals.addPackNed(packNedCosts);
        table.addCell(createTableCell(packNedCosts, ALIGN_RIGHT));

        final BigDecimal orderCommission = AndrewCommissionReportGenerator.calculateCommission(salesAmount,
                order.getShippingAddress() != null ? order.getShippingAddress().getCountryCode() : ShopifyConstants.NO_COUNTRY_AVAILABLE);
        tableTotals.addCommission(orderCommission);
        table.addCell(createTableCell(orderCommission, ALIGN_RIGHT));

        final BigDecimal totalOrderProfit = salesAmount.subtract(totalOrderCost).subtract(orderCommission).subtract(packNedCosts);
        tableTotals.addProfit(totalOrderProfit);
        table.addCell(createTableCell(totalOrderProfit, ALIGN_RIGHT));

        final BigDecimal orderProfitMargin = totalPriceExlTax.compareTo(BigDecimal.ZERO) < 1 ? BigDecimal.ZERO :
                totalOrderProfit.divide(totalPriceExlTax, SCALE, ROUNDING)
                        .multiply(ONE_HUNDRED)
                        .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        table.addCell(createTableCell(orderProfitMargin.compareTo(BigDecimal.ZERO) < 1 ? "" : orderProfitMargin.toPlainString()+"%", ALIGN_RIGHT));

        return salesAmount;
    }

    private BigDecimal calculatePackNedCost(String countryCode, BigDecimal orderAmount, BigDecimal shippingAmount) {
        BigDecimal result = shippingAmount;
        if (countryCode.equalsIgnoreCase(ShopifyConstants.UK)) {
            if (shippingAmount.compareTo(BigDecimal.valueOf(5.00)) == 0) {
                result = BigDecimal.valueOf(10.57);
            } else if (shippingAmount.compareTo(BigDecimal.valueOf(7.50)) == 0) {
                result = BigDecimal.valueOf(13.40);
            } else if (shippingAmount.compareTo(BigDecimal.ZERO) == 0) {
                if (orderAmount.compareTo(BigDecimal.valueOf(100)) < 0) {
                    result = BigDecimal.valueOf(10.57);
                }
                else {
                    result = BigDecimal.valueOf(13.40);
                }
            }
        }
        else if (shippingAmount.compareTo(BigDecimal.ZERO) == 0) {
            result = BigDecimal.valueOf(15.00);
        }
        return result;
    }

    private PdfPTable getSummaryTable() {
        final PdfPTable table = createFullWidthTable(getSummaryTableHeaders().length);
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
        addCellsToTable(table, getSummaryCells(ShopifyConstants.DIGITAL_TAG, totals.get(ShopifyConstants.DIGITAL_TAG)));

        addCellsToTable(table, createTableCell("TOTALS"));
        addCellsToTable(table, createTableCell(String.valueOf(overallTotals.getOrderCount()), ALIGN_CENTER));
        addCellsToTable(table, createTableCell(overallTotals.getOrderTotalTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getShippingTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getSalesTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getCostTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getPackNedTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getCommissionTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getProfitTotal(), ALIGN_RIGHT));
        return table;
    }

    private PdfPCell[] getSummaryTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Sales Group"));
        headers.add(createTableHeaderCell("No Of Orders"));
        headers.add(createTableHeaderCell("Order exl VAT"));
        headers.add(createTableHeaderCell("Shipping"));
        headers.add(createTableHeaderCell("Sales Amount"));
        headers.add(createTableHeaderCell("Total Cost"));
        headers.add(createTableHeaderCell("Total PackNed"));
        headers.add(createTableHeaderCell("Total Commission"));
        headers.add(createTableHeaderCell("Total Profit"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPCell[] getSummaryCells(String description, BoldTableTotals tableTotals) {
        final List<PdfPCell> summaryCells = new ArrayList<>();

        summaryCells.add(createTableCell(description));
        overallTotals.addOrders(tableTotals.getOrderCount());
        summaryCells.add(createTableCell(String.valueOf(tableTotals.getOrderCount()), ALIGN_CENTER));

        overallTotals.addOrderTotal(tableTotals.getOrderTotalTotal());
        summaryCells.add(createTableCell(tableTotals.getOrderTotalTotal(), ALIGN_RIGHT));

        overallTotals.addShipping(tableTotals.getShippingTotal());
        summaryCells.add(createTableCell(tableTotals.getShippingTotal(), ALIGN_RIGHT));

        overallTotals.addSales(tableTotals.getSalesTotal());
        summaryCells.add(createTableCell(tableTotals.getSalesTotal(), ALIGN_RIGHT));

        overallTotals.addCost(tableTotals.getCostTotal());
        summaryCells.add(createTableCell(tableTotals.getCostTotal(), ALIGN_RIGHT));

        overallTotals.addPackNed(tableTotals.getPackNedTotal());
        summaryCells.add(createTableCell(tableTotals.getPackNedTotal(), ALIGN_RIGHT));

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
        BigDecimal shippingTotal = BigDecimal.ZERO;
        BigDecimal costTotal = BigDecimal.ZERO;
        BigDecimal packNedTotal = BigDecimal.ZERO;
        BigDecimal commissionTotal = BigDecimal.ZERO;
        BigDecimal profitTotal = BigDecimal.ZERO;

        BigDecimal getSalesTotal() {
            return salesTotal;
        }

        void addSales(BigDecimal salesTotal) {
            this.salesTotal = this.salesTotal.add(salesTotal);
        }

        BigDecimal getPackNedTotal() {
            return packNedTotal;
        }

        void addPackNed(BigDecimal taxTotal) {
            this.packNedTotal = this.packNedTotal.add(taxTotal);
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

package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.model.LineItem;
import com.macbean.tech.shopify.model.Order;
import com.macbean.tech.shopify.model.Orders;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.itextpdf.text.Element.*;

public class BoldBreakdownReportGenerator extends AbstractShopifyReportGenerator {

    private static final double SHOPIFY_FEES = 225d;
    private static final double TRANSACTION_FEES = 467d;
    private static final double SHIPPING_COSTS = 1500d;

    private final int SCALE = 2;
    private final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private Map<String, BoldTableTotals> tableTotalsMap = new HashMap<>(10);
    private Map<Long, BigDecimal> allCostsByVariantId = new HashMap<>(100);

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
        final PdfPTable sponsorshipTable = getPricingGroupTable(ShopifyConstants.SPONSORSHIP_TAG);
        final PdfPTable digitalTable = getPricingGroupTable(ShopifyConstants.DIGITAL_TAG);
        final PdfPTable replacementTable = getPricingGroupTable(ShopifyConstants.REPLACEMENTS_TAG);
        final PdfPTable returnsTable = getPricingGroupTable(ShopifyConstants.RETURN_TAG);
        final PdfPTable demosTable = getPricingGroupTable(ShopifyConstants.DEMOS_TAG);
        final PdfPTable staffTable = getPricingGroupTable(ShopifyConstants.STAFF_TAG);
        final PdfPTable voucherTable = getPricingGroupTable(ShopifyConstants.VOUCHER_TAG);

        final PdfPTable freeProductsTable = getProductBreakdownTable(ShopifyConstants.FREE_TAG);
        final PdfPTable staffProductsTable = getProductBreakdownTable(ShopifyConstants.STAFF_TAG);
        final PdfPTable replacementProductsTable = getProductBreakdownTable(ShopifyConstants.REPLACEMENTS_TAG);

        for (Order order : orders.getOrders()) {
            if (order.getShippingAddress() == null) {
                addOrderToTable(ShopifyConstants.DIGITAL_TAG, digitalTable, order);
            }
            else if (hasDiscountCodeBeenUsed(order)) {
                addOrderToTable(ShopifyConstants.VOUCHER_TAG, voucherTable, order);
            }
            else if (order.getTags().contains(ShopifyConstants.STAFF_TAG)) {
                addOrderToTable(ShopifyConstants.STAFF_TAG, staffTable, order);
                addProducts(staffProductsTable, order);
            }
            else if (order.getTags().contains(ShopifyConstants.REPLACEMENTS_TAG) ||
                    order.getTags().contains(ShopifyConstants.BREAKAGES_TAG) ||
                    order.getTags().contains(ShopifyConstants.FAULTY_TAG)) {
                addOrderToTable(ShopifyConstants.REPLACEMENTS_TAG, replacementTable, order);
                addProducts(replacementProductsTable, order);
            }
            else if (order.getTags().contains(ShopifyConstants.RETURN_TAG) ||
                    order.getTags().contains(ShopifyConstants.EXCHANGE_TAG)) {
                addOrderToTable(ShopifyConstants.RETURN_TAG, returnsTable, order);
            }
            else if (order.getTags().contains(ShopifyConstants.DEMOS_TAG)) {
                addOrderToTable(ShopifyConstants.DEMOS_TAG, demosTable, order);
            }
            else if (order.getTags().contains(ShopifyConstants.SPONSORSHIP_TAG)) {
                addOrderToTable(ShopifyConstants.SPONSORSHIP_TAG, sponsorshipTable, order);
            }
            else if ("0.00".equals(order.getTotalPrice())) {
                addOrderToTable(ShopifyConstants.FREE_TAG, freeTable, order);
                addProducts(freeProductsTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.TRADE_TAG)) {
                addOrderToTable(ShopifyConstants.TRADE_TAG, tradeTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.PLATINUM_TAG)) {
                addOrderToTable(ShopifyConstants.PLATINUM_TAG, platinumTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.GOLD_TAG)) {
                addOrderToTable(ShopifyConstants.GOLD_TAG, goldTable, order);
            }
            else if (order.getCustomer().getTags().contains(ShopifyConstants.AFFILIATE_TAG)) {
                addOrderToTable(ShopifyConstants.AFFILIATE_TAG, affiliateTable, order);
            }
            else {
                addOrderToTable(ShopifyConstants.DIRECT_TAG, directTable, order);
            }
        }

        addTotalRowAndDisplay(ShopifyConstants.TRADE_TAG, tradeTable);
        addTotalRowAndDisplay(ShopifyConstants.PLATINUM_TAG, platinumTable);
        addTotalRowAndDisplay(ShopifyConstants.GOLD_TAG, goldTable);
        addTotalRowAndDisplay(ShopifyConstants.AFFILIATE_TAG, affiliateTable);
        addTotalRowAndDisplay(ShopifyConstants.DIRECT_TAG, directTable);
        addTotalRowAndDisplay(ShopifyConstants.VOUCHER_TAG, voucherTable);
        addTotalRowAndDisplay(ShopifyConstants.DEMOS_TAG, demosTable);
        addTotalRowAndDisplay(ShopifyConstants.DIGITAL_TAG, digitalTable);
        addTotalRowAndDisplay(ShopifyConstants.FREE_TAG, freeTable, freeProductsTable);
        addTotalRowAndDisplay(ShopifyConstants.STAFF_TAG, staffTable, staffProductsTable);
        addTotalRowAndDisplay(ShopifyConstants.SPONSORSHIP_TAG, sponsorshipTable);
        addTotalRowAndDisplay(ShopifyConstants.REPLACEMENTS_TAG, replacementTable, replacementProductsTable);
        addTotalRowAndDisplay(ShopifyConstants.RETURN_TAG, returnsTable);
    }

    private boolean hasDiscountCodeBeenUsed(Order order) {
        return order.getDiscountApplications() != null && order.getDiscountApplications().size() > 0 &&
                order.getDiscountApplications().stream().anyMatch(x -> x.getType().equalsIgnoreCase(ShopifyConstants.DISCOUNT_CODE_TYPE));
    }

    private void addTotalRowAndDisplay(String tag, PdfPTable table) throws DocumentException {
        addTotalRowAndDisplay(tag,table, null);
    }

    private void addTotalRowAndDisplay(String tag, PdfPTable table, PdfPTable productsTable) throws DocumentException {
        addTotalRowToTable(tableTotalsMap.get(tag), table);
        document.add(table);
        if (productsTable != null) {
            document.add(productsTable);
        }
        document.newPage();
    }

    private PdfPCell[] getPricingTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Ord #"));
        headers.add(createTableHeaderCell("Order Date"));
        headers.add(createTableHeaderCell("Customer Name"));
        headers.add(createTableHeaderCell("Co."));
        headers.add(createTableHeaderCell("Tags/Codes"));
        headers.add(createTableHeaderCell("Discount %"));
        headers.add(createTableHeaderCell("Sale Amount (Excl VAT)"));
        headers.add(createTableHeaderCell("Item Costs"));
        headers.add(createTableHeaderCell("Commission"));
        headers.add(createTableHeaderCell("Profit Amount"));
        headers.add(createTableHeaderCell("Profit %"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPCell[] getProductTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("SKU"));
        headers.add(createTableHeaderCell("Product"));
        headers.add(createTableHeaderCell("Variant"));
        headers.add(createTableHeaderCell("Quantity"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPTable getPricingGroupTable(String tag) {
        tableTotalsMap.put(tag, new BoldTableTotals());

        final PdfPTable table = createFullWidthTable(3,6,7,2,5,4,4,4,4,4,4);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell(tag.toUpperCase() + " SALES", ALIGN_CENTER);
        titleCell.setColspan(getPricingTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getPricingTableHeaders());
        return table;
    }

    private PdfPTable getProductBreakdownTable(String tag) {
        PdfPCell[] productTableHeaders = getProductTableHeaders();
        final PdfPTable table = createFullWidthTable(productTableHeaders.length);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell(tag.toUpperCase() + " PRODUCT SUMMARY", ALIGN_CENTER);
        titleCell.setColspan(getPricingTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, productTableHeaders);
        return table;
    }

    private void addTotalRowToTable(BoldTableTotals totals, PdfPTable table) {
        final PdfPCell titleCell = createTableCell("TOTALS", ALIGN_LEFT);
        titleCell.setColspan(getPricingTableHeaders().length - 6);
        table.addCell(titleCell);

        final PdfPCell discountCell = createTableCell("", ALIGN_RIGHT);
        table.addCell(discountCell);

        final PdfPCell salesTotalCell = createTableCell(totals.getSalesTotal(), ALIGN_RIGHT);
        table.addCell(salesTotalCell);

        final PdfPCell costCell = createTableCell(totals.getCostTotal(), ALIGN_RIGHT);
        table.addCell(costCell);

        final PdfPCell commissionCell = createTableCell(totals.getCommissionTotal(), ALIGN_RIGHT);
        table.addCell(commissionCell);

        final PdfPCell profitCell = createTableCell(totals.getProfitTotal(), ALIGN_RIGHT);
        table.addCell(profitCell);

        final PdfPCell profitMarginCell = createTableCell(calculateProfitMargin(totals.getSalesTotal(), totals.getProfitTotal()), ALIGN_RIGHT);
        table.addCell(profitMarginCell);
    }

    private BigDecimal addOrderToTable(String tag, PdfPTable table, Order order) throws IOException {
        final BoldTableTotals tableTotals = tableTotalsMap.get(tag);

        tableTotals.addOrder();
        table.addCell(createTableCell(order.getName()));

        final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
        table.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

        table.addCell(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        String countryCode = order.getShippingAddress() != null ? order.getShippingAddress().getCountryCode() : ShopifyConstants.NO_COUNTRY_AVAILABLE;
        table.addCell(countryCode);

        String discountCodes = StringUtils.EMPTY;
        if (hasDiscountCodeBeenUsed(order)) {
            final List<String> discountCodesList = order.getDiscountCodes().stream()
                    .map(x -> x.get("code"))
                    .collect(Collectors.toList());
            discountCodes = String.join(", ", discountCodesList);
        }

        table.addCell(createTableTagCell(order.getTags() + (StringUtils.isEmpty(discountCodes) ? StringUtils.EMPTY : " [" + discountCodes + "]")));

        final BigDecimal totalDiscount = new BigDecimal(order.getTotalDiscounts());
        final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
        final BigDecimal totalPriceBeforeDiscount = new BigDecimal(order.getTotalPrice()).add(totalDiscount);
        final BigDecimal percentageDiscount = totalDiscount.divide(totalPriceBeforeDiscount, SCALE * SCALE, ROUNDING)
                .multiply(ONE_HUNDRED)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        final BigDecimal totalTax = new BigDecimal(order.getTotalTax());
        final BigDecimal salesTotalExlTax = totalPrice.subtract(totalTax);

        table.addCell(createTableCell(percentageDiscount.toPlainString()+"%", ALIGN_RIGHT));

        tableTotals.addSales(salesTotalExlTax);
        table.addCell(createTableCell(salesTotalExlTax, ALIGN_RIGHT));

        final BigDecimal totalOrderCost = shopifyClient.calculateTotalOrderCost(order, allCostsByVariantId);
        tableTotals.addCost(totalOrderCost);
        table.addCell(createTableCell(totalOrderCost, ALIGN_RIGHT));

        BigDecimal shipping = BigDecimal.ZERO;
        if (order.getShippingLines() != null && order.getShippingLines().size() > 0) {
            shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
        }
        final BigDecimal orderCommission = AndrewCommissionReportGenerator.calculateCommission(salesTotalExlTax.subtract(shipping),
                order.getShippingAddress() != null ? order.getShippingAddress().getCountryCode() : ShopifyConstants.NO_COUNTRY_AVAILABLE);
        tableTotals.addCommission(orderCommission);
        table.addCell(createTableCell(orderCommission, ALIGN_RIGHT));

        final BigDecimal totalOrderProfit = salesTotalExlTax.subtract(totalOrderCost).subtract(orderCommission);
        tableTotals.addProfit(totalOrderProfit);
        table.addCell(createTableCell(totalOrderProfit, ALIGN_RIGHT));

        table.addCell(createTableCell(calculateProfitMargin(salesTotalExlTax, totalOrderProfit), ALIGN_RIGHT));

        return salesTotalExlTax;
    }

    private void addProducts(PdfPTable table, Order order) {
       for (LineItem lineItem : order.getLineItems()) {
           addCellsToTable(table,
                   createTableCell(lineItem.getSku()),
                   createTableCell(lineItem.getTitle()),
                   createTableCell(lineItem.getVariantTitle()),
                   createTableCell(String.valueOf(lineItem.getQuantity()))
           );
       }
    }
    private String calculateProfitMargin(BigDecimal salesAmount, BigDecimal profit) {
        final BigDecimal profitMargin = salesAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                profit.divide(salesAmount, SCALE * SCALE, ROUNDING).multiply(ONE_HUNDRED).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        return salesAmount.compareTo(BigDecimal.ZERO) <= 0 ? "N/A" : profitMargin.toPlainString()+"%";
    }

    private PdfPCell[] getSummaryTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Sales Group"));
        headers.add(createTableHeaderCell("No Of Orders"));
        headers.add(createTableHeaderCell("Sale Amount (Excl VAT)"));
        headers.add(createTableHeaderCell("Total Cost"));
        headers.add(createTableHeaderCell("Total Commission"));
        headers.add(createTableHeaderCell("Total Profit"));
        headers.add(createTableHeaderCell("Total Profit Margin"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPCell[] getSummaryCells(String tag) {
        final BoldTableTotals tableTotals = tableTotalsMap.get(tag);
        final List<PdfPCell> summaryCells = new ArrayList<>();

        summaryCells.add(createTableCell(tag));
        overallTotals.addOrders(tableTotals.getOrderCount());
        summaryCells.add(createTableCell(String.valueOf(tableTotals.getOrderCount()), ALIGN_CENTER));

        overallTotals.addSales(tableTotals.getSalesTotal());
        summaryCells.add(createTableCell(tableTotals.getSalesTotal(), ALIGN_RIGHT));

        overallTotals.addCost(tableTotals.getCostTotal());
        summaryCells.add(createTableCell(tableTotals.getCostTotal(), ALIGN_RIGHT));

        overallTotals.addCommission(tableTotals.getCommissionTotal());
        summaryCells.add(createTableCell(tableTotals.getCommissionTotal(), ALIGN_RIGHT));

        overallTotals.addProfit(tableTotals.getProfitTotal());
        summaryCells.add(createTableCell(tableTotals.getProfitTotal(), ALIGN_RIGHT));

        summaryCells.add(createTableCell(calculateProfitMargin(tableTotals.getSalesTotal(), tableTotals.getProfitTotal()), ALIGN_RIGHT));

        return summaryCells.toArray(new PdfPCell[0]);
    }

    private PdfPTable getSummaryTable() {
        final PdfPTable table = createFullWidthTable(getSummaryTableHeaders().length);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell("SALES BREAKDOWN SUMMARY", ALIGN_CENTER);
        titleCell.setColspan(getSummaryTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getSummaryTableHeaders());

        addCellsToTable(table, getSummaryCells(ShopifyConstants.TRADE_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.PLATINUM_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.GOLD_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.AFFILIATE_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.DIRECT_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.VOUCHER_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.DEMOS_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.DIGITAL_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.FREE_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.STAFF_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.SPONSORSHIP_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.REPLACEMENTS_TAG));
        addCellsToTable(table, getSummaryCells(ShopifyConstants.RETURN_TAG));

        addCellsToTable(table, createTableCell("TOTALS"));
        addCellsToTable(table, createTableCell(String.valueOf(overallTotals.getOrderCount()), ALIGN_CENTER));
        addCellsToTable(table, createTableCell(overallTotals.getSalesTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getCostTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getCommissionTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(overallTotals.getProfitTotal(), ALIGN_RIGHT));
        addCellsToTable(table, createTableCell(calculateProfitMargin(overallTotals.getSalesTotal(), overallTotals.getProfitTotal()), ALIGN_RIGHT));
        return table;
    }

    private PdfPCell[] getExternalCostsTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Sales Profit"));
        headers.add(createTableHeaderCell("Shopify Fees*"));
        headers.add(createTableHeaderCell("Shipping Costs*"));
        headers.add(createTableHeaderCell("Transaction Fees*"));
        headers.add(createTableHeaderCell("Estimated Profit/Surplus"));
        return headers.toArray(new PdfPCell[0]);
    }

    private PdfPCell[] getExternalCostsCells(BigDecimal totalProfit) {
        final List<PdfPCell> externalCostsCells = new ArrayList<>();

        final BigDecimal shopifyFees = BigDecimal.valueOf(SHOPIFY_FEES);
        final BigDecimal transactionFees = BigDecimal.valueOf(TRANSACTION_FEES);
        final BigDecimal shippingCosts = BigDecimal.valueOf(SHIPPING_COSTS);
        final BigDecimal surplus =  totalProfit.subtract(shopifyFees).subtract(transactionFees).subtract(shippingCosts);

        externalCostsCells.add(createTableCell(totalProfit, ALIGN_RIGHT));
        externalCostsCells.add(createTableCell(shopifyFees, ALIGN_RIGHT));
        externalCostsCells.add(createTableCell(shippingCosts, ALIGN_RIGHT));
        externalCostsCells.add(createTableCell(transactionFees, ALIGN_RIGHT));
        externalCostsCells.add(createTableCell(surplus, ALIGN_RIGHT));

        return externalCostsCells.toArray(new PdfPCell[0]);
    }

    private PdfPTable getExternalCostsTable(BigDecimal totalProfit) {
        final PdfPTable table = createFullWidthTable(getExternalCostsTableHeaders().length);
        table.setWidthPercentage(100f);
        final PdfPCell titleCell = createTableHeaderCell("ESTIMATED COST SUMMARY\n(* estimated amount)", ALIGN_CENTER);
        titleCell.setColspan(getSummaryTableHeaders().length);
        table.addCell(titleCell);
        addCellsToTable(table, getExternalCostsTableHeaders());
        addCellsToTable(table, getExternalCostsCells(totalProfit));
        return table;
    }

    @Override
    void addFooter() throws DocumentException, IOException {
        final PdfPTable summaryTable = getSummaryTable();
        document.add(summaryTable);

        final PdfPTable externalCostsTable = getExternalCostsTable(overallTotals.getProfitTotal());
        document.add(externalCostsTable);
    }

    private class BoldTableTotals {

        int orderCount = 0;
        BigDecimal salesTotal = BigDecimal.ZERO;
        BigDecimal costTotal = BigDecimal.ZERO;
        BigDecimal commissionTotal = BigDecimal.ZERO;
        BigDecimal profitTotal = BigDecimal.ZERO;

        BigDecimal getSalesTotal() {
            return salesTotal;
        }

        void addSales(BigDecimal salesTotal) {
            this.salesTotal = this.salesTotal.add(salesTotal);
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

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
import static com.itextpdf.text.Element.ALIGN_LEFT;
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

        final PdfPTable tradeTable = getPricingGroupTable("Trade Sales");
        final PdfPTable platinumTable = getPricingGroupTable("Platinum Sales");
        final PdfPTable goldTable = getPricingGroupTable("Gold Sales");
        final PdfPTable affiliateTable = getPricingGroupTable("Affiliate Sales");
        final PdfPTable directTable = getPricingGroupTable("Direct Sales");
        final PdfPTable freeTable = getPricingGroupTable("Free of Charge Orders");

        BigDecimal tradeTotal = BigDecimal.ZERO;
        BigDecimal platinumTotal = BigDecimal.ZERO;
        BigDecimal goldTotal = BigDecimal.ZERO;
        BigDecimal affiliateTotal = BigDecimal.ZERO;
        BigDecimal directTotal = BigDecimal.ZERO;
        BigDecimal freeTotal = BigDecimal.ZERO;

        for (Order order : orders.getOrders()) {
            if ("0.00".equals(order.getTotalPrice())) {
                freeTotal = freeTotal.add(addOrderToTable(freeTable, order));
            }
            else if (order.getCustomer().getTags().contains(TRADE_TAG)) {
                tradeTotal = tradeTotal.add(addOrderToTable(tradeTable, order));
            }
            else if (order.getCustomer().getTags().contains(PLATINUM_TAG)) {
                platinumTotal = platinumTotal.add(addOrderToTable(platinumTable, order));
            }
            else if (order.getCustomer().getTags().contains(GOLD_TAG)) {
                goldTotal = goldTotal.add(addOrderToTable(goldTable, order));
            }
            else if (order.getCustomer().getTags().contains(AFFILIATE_TAG)) {
                affiliateTotal = affiliateTotal.add(addOrderToTable(affiliateTable, order));
            }
            else {
                directTotal = directTotal.add(addOrderToTable(directTable, order));
            }
        }

        addTotalRowToTable("Trade Sales Total", tradeTotal, tradeTable);
        document.add(tradeTable);

        addTotalRowToTable("Platinum Sales Total", platinumTotal, platinumTable);
        document.add(platinumTable);

        addTotalRowToTable("Gold Sales Total", goldTotal, goldTable);
        document.add(goldTable);

        addTotalRowToTable("Affiliate Sales Total", affiliateTotal, affiliateTable);
        document.add(affiliateTable);

        addTotalRowToTable("Direct Sales Total", directTotal, directTable);
        document.add(directTable);

        addTotalRowToTable("Free Sales Total", freeTotal, freeTable);
        document.add(freeTable);
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

    private void addTotalRowToTable(String text, BigDecimal amount, PdfPTable table) {
        final PdfPCell titleCell = createTableCell(text, ALIGN_LEFT);
        titleCell.setColspan(getPricingTableHeaders().length - 1);
        table.addCell(titleCell);

        final PdfPCell totalCell = createTableCell(amount, ALIGN_RIGHT);
        table.addCell(totalCell);
    }

    private BigDecimal addOrderToTable(PdfPTable table, Order order) {
        table.addCell(createTableCell(order.getName()));

        final TemporalAccessor orderDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(order.getCreatedAt());
        table.addCell(createTableCell(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT).format(orderDate)));

        table.addCell(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());

        table.addCell(order.getShippingAddress().getCountryCode());

        table.addCell(createTableTagCell(order.getCustomer().getTags()));
        table.addCell(createTableTagCell(order.getTags()));

        table.addCell(createTableCell(new BigDecimal(order.getTotalDiscounts()), ALIGN_RIGHT));

        final BigDecimal totalPrice = new BigDecimal(order.getTotalPrice());
        table.addCell(createTableCell(totalPrice, ALIGN_RIGHT));

        final BigDecimal tax = new BigDecimal(order.getTotalTax());
        table.addCell(createTableCell(tax, ALIGN_RIGHT));

        BigDecimal shipping = BigDecimal.ZERO;
        if (order.getShippingLines() != null && order.getShippingLines().size() > 0) {
            shipping = new BigDecimal(order.getShippingLines().get(0).getPrice());
        }
        table.addCell(createTableCell(shipping, ALIGN_RIGHT));

        final BigDecimal salesAmount = totalPrice.subtract(tax).subtract(shipping);
        table.addCell(createTableCell(salesAmount, ALIGN_RIGHT));

        return salesAmount;
    }

    @Override
    void addFooter() throws DocumentException, IOException {

    }
}

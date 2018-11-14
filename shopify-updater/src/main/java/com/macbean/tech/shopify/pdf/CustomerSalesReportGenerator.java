package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.model.Customer;
import com.macbean.tech.shopify.model.Customers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_RIGHT;

public class CustomerSalesReportGenerator extends AbstractShopifyReportGenerator {

    @Override
    Rectangle getPageSize() {
        return PageSize.A4.rotate();
    }

    @Override
    String getTitle() {
        return "Customer Sales Breakdown";
    }

    @Override
    String getReferencePrefix() {
        return "EYE-CUST-SALES-";
    }

    @Override
    void addHeader() throws DocumentException, IOException {
        document.add(getEyeLogo(75f,75f, ALIGN_CENTER));
    }

    @Override
    void addContent() throws DocumentException, IOException {
        final Customers customers = shopifyClient.getAllCustomers();

        final PdfPTable customersTable = createFullWidthTable(2,2,4,1,1,2,2);

        addCellsToTable(customersTable, getCustomerTableHeaders());

        final List<Customer> sortedCustomers = customers.getCustomers().stream()
                .sorted(
                        Comparator.comparing((Customer customer) -> new BigDecimal(customer.getTotalSpent()))
                                .thenComparing(Customer::getOrdersCount)
                                .reversed()
                )
                .collect(Collectors.toList());

        for (Customer customer : sortedCustomers) {

            if (customer.getOrdersCount() > 0) {

                String tag;

                if ("0.00".equals(customer.getTotalSpent())) {
                    tag = ShopifyConstants.FREE_TAG;
                } else if (customer.getTags().contains(ShopifyConstants.TRADE_TAG)) {
                    tag = ShopifyConstants.TRADE_TAG;
                } else if (customer.getTags().contains(ShopifyConstants.PLATINUM_TAG)) {
                    tag = ShopifyConstants.PLATINUM_TAG;
                } else if (customer.getTags().contains(ShopifyConstants.GOLD_TAG)) {
                    tag = ShopifyConstants.GOLD_TAG;
                } else if (customer.getTags().contains(ShopifyConstants.AFFILIATE_TAG)) {
                    tag = ShopifyConstants.AFFILIATE_TAG;
                } else {
                    tag = ShopifyConstants.DIRECT_TAG;
                }

                addCellsToTable(customersTable,
                        createTableCell(customer.getFirstName()),
                        createTableCell(customer.getLastName()),
                        createTableCell(customer.getEmail()),
                        createTableCell(tag, ALIGN_CENTER),
                        createTableCell(String.valueOf(customer.getOrdersCount()), ALIGN_CENTER),
                        createTableCell(new BigDecimal(customer.getTotalSpent()), ALIGN_RIGHT),
                        createTableCell(new BigDecimal(customer.getTotalSpent()).divide(BigDecimal.valueOf(customer.getOrdersCount()), RoundingMode.HALF_UP), ALIGN_RIGHT)
                );
            }
        }

        document.add(customersTable);
    }

    private PdfPCell[] getCustomerTableHeaders() {
        final List<PdfPCell> headers = new ArrayList<>();
        headers.add(createTableHeaderCell("Firstname"));
        headers.add(createTableHeaderCell("Surname"));
        headers.add(createTableHeaderCell("Email"));
        headers.add(createTableHeaderCell("Type"));
        headers.add(createTableHeaderCell("Orders Count"));
        headers.add(createTableHeaderCell("Total Spend"));
        headers.add(createTableHeaderCell("Average Spend"));
        return headers.toArray(new PdfPCell[0]);
    }

    @Override
    void addFooter() throws DocumentException, IOException {

    }
}

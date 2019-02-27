package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.ShopifyUtils;
import com.macbean.tech.shopify.model.Customer;
import com.macbean.tech.shopify.model.Customers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_RIGHT;

public class CustomerSalesReportPdfGenerator extends AbstractShopifyReportPdfGenerator {

    private Map<String, BigDecimal> minSalesAmountByTag = new HashMap<>();
    private Map<String, Set<String>> underPerformingEmailAddressesByTag = new HashMap<>();

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A4.rotate();
    }

    @Override
    protected String getTitle() {
        return "Customer Sales Breakdown";
    }

    @Override
    protected String getReferencePrefix() {
        return "EYE-CUST-SALES-";
    }

    @Override
    protected void addHeader() throws DocumentException, IOException {
        document.add(getEyeLogo(75f,75f, ALIGN_CENTER));
    }

    @Override
    protected void addContent() throws DocumentException, IOException {
        final Customers customers = shopifyClient.getAllCustomers();

        final PdfPTable customersTable = createFullWidthTable(2,2,4,1,1,2,2,1);

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

                final String tag = ShopifyUtils.determineCustomerTag(customer);

                boolean flaggedForUnderperforming =
                        (!customer.getTags().contains(ShopifyConstants.SPONSORSHIP_TAG) && !customer.getTags().contains(ShopifyConstants.PSA_PLAYER_TAG)) &&
                                isCustomerUnderperforming(new BigDecimal(customer.getTotalSpent()), tag);

                if (flaggedForUnderperforming) {
                    addEmailToUnderPerformingList(customer.getEmail(), tag);
                }

                addCellsToTable(customersTable,
                        createTableCell(customer.getFirstName()),
                        createTableCell(customer.getLastName()),
                        createTableCell(customer.getEmail()),
                        createTableCell(tag, ALIGN_CENTER),
                        createTableCell(String.valueOf(customer.getOrdersCount()), ALIGN_CENTER),
                        createTableCell(new BigDecimal(customer.getTotalSpent()), ALIGN_RIGHT),
                        createTableCell(new BigDecimal(customer.getTotalSpent()).divide(BigDecimal.valueOf(customer.getOrdersCount()), RoundingMode.HALF_UP), ALIGN_RIGHT),
                        createTableCell(flaggedForUnderperforming ? "Yes" : "No", ALIGN_CENTER)
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
        headers.add(createTableHeaderCell("Flagged"));
        return headers.toArray(new PdfPCell[0]);
    }

    @Override
    protected void addFooter() throws DocumentException, IOException {
        for (String tag : underPerformingEmailAddressesByTag.keySet()) {
            System.out.println(tag + " (" + minSalesAmountByTag.get(tag).toPlainString() + ")");
            String authorString = String.join(",", underPerformingEmailAddressesByTag.get(tag));
            System.out.println(authorString);
        }
    }

    private boolean isCustomerUnderperforming(BigDecimal salesAmount, String tag) {
        if (minSalesAmountByTag.isEmpty()) {
            minSalesAmountByTag.put(ShopifyConstants.TRADE_TAG, new BigDecimal(2500));
            minSalesAmountByTag.put(ShopifyConstants.PLATINUM_TAG, new BigDecimal(1000));
            minSalesAmountByTag.put(ShopifyConstants.GOLD_TAG, new BigDecimal(500));
        }
        if (!minSalesAmountByTag.containsKey(tag)) {
            return false;
        }
        return salesAmount.compareTo(minSalesAmountByTag.get(tag)) < 0;
    }

    private void addEmailToUnderPerformingList(String email, String tag) {
        Set<String> emails = underPerformingEmailAddressesByTag.get(tag);
        if (emails == null) {
            emails = new HashSet<>();
        }
        emails.add(email);
        underPerformingEmailAddressesByTag.put(tag, emails);
    }
}

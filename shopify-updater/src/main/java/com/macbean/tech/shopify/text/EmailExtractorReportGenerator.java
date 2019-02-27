package com.macbean.tech.shopify.text;

import com.macbean.tech.shopify.ShopifyClient;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.ShopifyUtils;
import com.macbean.tech.shopify.model.Customer;
import com.macbean.tech.shopify.model.Customers;
import com.macbean.tech.shopify.pdf.AbstractShopifyReportPdfGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmailExtractorReportGenerator {

    private ShopifyClient shopifyClient = new ShopifyClient();

    private Map<String, Set<String>> emailAddressesByTag = new HashMap<>();

    public EmailExtractorReportGenerator() throws Exception {
        final Customers customers = shopifyClient.getAllCustomers();

        for (Customer customer : customers.getCustomers()) {
            if (customer.getOrdersCount() > 0) {
                final String tag = ShopifyUtils.determineCustomerTag(customer);
                addEmailForTag(customer.getEmail(), tag);
            }
        }
    }

    public String getEmailsForTag(String tag) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder(String.join(",", emailAddressesByTag.get(tag)));
        return stringBuilder.toString();
    }

    public void writeTextToFile(String text) {
        final String filename = ShopifyConstants.OUTPUT_DIRECTORY + File.separatorChar +
                DateTimeFormatter.ofPattern(AbstractShopifyReportPdfGenerator.FILENAME_DATE_FORMAT).format(LocalDateTime.now()) + "-emails.txt";
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(text);
        } catch (IOException ioe) {
        }
    }

    private void addEmailForTag(String email, String tag) {
        Set<String> emails = emailAddressesByTag.get(tag);
        if (emails == null) {
            emails = new HashSet<>();
        }
        emails.add(email);
        emailAddressesByTag.put(tag, emails);
    }
}

package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.model.InventoryItem;
import com.macbean.tech.shopify.model.InventoryItems;
import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Variant;

import java.io.IOException;
import java.util.*;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ProductReportGenerator extends AbstractShopifyReportGenerator {

    private static final String DEFAULT_TITLE = "Default Title";
    private static final String NOT_APPLICABLE = "n/a";

    @Override
    Rectangle getPageSize() {
        return PageSize.A4;
    }

    @Override
    String getTitle() {
        return "Eye UK & Ireland Products";
    }

    @Override
    String getReferencePrefix() {
        return "EYE-PRODUCTS-";
    }

    @Override
    void addHeader() throws DocumentException, IOException {
        document.add(getEyeLogo(75f,75f, ALIGN_CENTER));
    }

    private Map<String, String> getInventoryIdCosts(Map<String, List<Product>> productsByType) throws IOException {
        final List<String> inventoryIds = new ArrayList<>();
        for (String productType : productsByType.keySet()) {
            for (Product product : productsByType.get(productType)) {
                for (Variant variant : product.getVariants()) {
                    if (!isEmpty(variant.getInventoryItemId())) {
                        inventoryIds.add(variant.getInventoryItemId());
                    }
                }
            }
        }

        final Map<String, String> inventoryIdCosts = new HashMap<>();

        //TODO this needs to be split into 100s
        final InventoryItems inventoryItemsList = shopifyClient.getInventoryItems(inventoryIds.toArray(new String[0]));
        
        for (InventoryItem inventoryItem : inventoryItemsList.getInventoryItems()) {
            inventoryIdCosts.put(String.valueOf(inventoryItem.getId()), inventoryItem.getCost());
        }

        return inventoryIdCosts;
    }

    @Override
    void addContent() throws DocumentException, IOException {
        final Map<String, List<Product>> productsByType = shopifyClient.getAllProductsByType();

        final Map<String, String> inventoryIdCosts = getInventoryIdCosts(productsByType);

        final PdfPTable productTable = createFullWidthTable(3,3,2,1,1,1);

        for (String productType : productsByType.keySet()) {

            final PdfPCell tableHeaderCell = createTableHeaderCell(productType);
            tableHeaderCell.setColspan(productTable.getNumberOfColumns());
            addCellsToTable(productTable, tableHeaderCell);

            for (Product product : productsByType.get(productType)) {

                for (Variant variant : product.getVariants()) {
                    String cost = "";
                    if (!isEmpty(variant.getInventoryItemId())) {
                        cost = inventoryIdCosts.get(variant.getInventoryItemId());
                    }
                    addCellsToTable(productTable,
                            createTableCell(product.getTitle()),
                            createTableCell(DEFAULT_TITLE.equalsIgnoreCase(variant.getTitle()) ? NOT_APPLICABLE : variant.getTitle()),
                            createTableCell(variant.getSku()),
                            createTableCell(cost),
                            createTableCell(variant.getPrice()),
                            createTableCell(variant.getCompareAtPrice())
                    );
                }
            }
        }

        document.add(productTable);
    }

    @Override
    void addFooter() throws DocumentException, IOException {

    }
}

package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Variant;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.itextpdf.text.Element.ALIGN_CENTER;

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

    @Override
    void addContent() throws DocumentException, IOException {
        final Map<String, List<Product>> productsByType = shopifyClient.getAllProductsByType();

        final PdfPTable productTable = createFullWidthTable(3,3,2,1,1);

        for (String productType : productsByType.keySet()) {

            final PdfPCell tableHeaderCell = createTableHeaderCell(productType);
            tableHeaderCell.setColspan(productTable.getNumberOfColumns());
            addCellsToTable(productTable, tableHeaderCell);

            for (Product product : productsByType.get(productType)) {

                for (Variant variant : product.getVariants()) {
                    addCellsToTable(productTable,
                            createTableCell(product.getTitle()),
                            createTableCell(DEFAULT_TITLE.equalsIgnoreCase(variant.getTitle()) ? NOT_APPLICABLE : variant.getTitle()),
                            createTableCell(variant.getSku()),
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

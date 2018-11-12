package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

import java.io.IOException;

import static com.itextpdf.text.Element.ALIGN_CENTER;

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

    }

    @Override
    void addFooter() throws DocumentException, IOException {

    }
}

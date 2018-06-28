package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class ShopifyReportGeneratorTest {

    private ShopifyReportGenerator testInstance = new ShopifyReportGenerator();

    @Test
    public void testGetAllOrdersFromPeriod() throws Exception {
        byte[] reportBytes = testInstance.generateAndrewCommissionReport(
                ZonedDateTime.of(2018, 5, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
                ZonedDateTime.of(2018, 5, 31, 23, 59, 59, 0, ZoneId.systemDefault())
        );
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString("Eye Rackets International B.V."));
        assertThat(textFromPage, containsString("Andrew MacBean"));
    }
}
package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class ShopifyReportGeneratorTest {

    final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    final int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    final int lastDayOfCurrentMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

    final ZonedDateTime FROM = ZonedDateTime.of(currentYear, currentMonth, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    final ZonedDateTime TO = ZonedDateTime.of(currentYear, currentMonth, lastDayOfCurrentMonth, 23, 59, 59, 0, ZoneId.systemDefault());

    @Test
    public void testGetAndrewCommissionFromPeriod() throws Exception {
        final AndrewCommissionReportGenerator testInstance = new AndrewCommissionReportGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
        assertThat(textFromPage, containsString("Eye Rackets International B.V."));
        assertThat(textFromPage, containsString("Andrew MacBean"));
    }

    @Test
    public void testGetBoldBreakdownFromPeriod() throws Exception {
        final BoldBreakdownReportGenerator testInstance = new BoldBreakdownReportGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
    }

    @Test
    public void testCustomerSalesBreakdownFromPeriod() throws Exception {
        final CustomerSalesReportGenerator testInstance = new CustomerSalesReportGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
    }

    @Test
    public void testProductReport() throws Exception {
        final ProductReportGenerator testInstance = new ProductReportGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
    }
}
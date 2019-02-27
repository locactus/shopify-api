package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.macbean.tech.shopify.ShopifyConstants;
import com.macbean.tech.shopify.text.EmailExtractorReportGenerator;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class ShopifyReportGeneratorTest {

    public static final String NEW_LINE = "\n";
    final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    final int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    final int lastDayOfCurrentMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

    final ZonedDateTime FROM = ZonedDateTime.of(currentYear, currentMonth, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    final ZonedDateTime TO = ZonedDateTime.of(currentYear, currentMonth, lastDayOfCurrentMonth, 23, 59, 59, 0, ZoneId.systemDefault());

    @Test
    public void testGetAndrewCommissionFromPeriod() throws Exception {
        final AndrewCommissionReportPdfGenerator testInstance = new AndrewCommissionReportPdfGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
        assertThat(textFromPage, containsString("Eye Rackets International B.V."));
        assertThat(textFromPage, containsString("Andrew MacBean"));
    }

    @Test
    public void testGetBoldBreakdownFromPeriod() throws Exception {
        final BoldBreakdownReportPdfGenerator testInstance = new BoldBreakdownReportPdfGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
    }

    @Test
    public void testCustomerSalesBreakdownFromPeriod() throws Exception {
        final CustomerSalesReportPdfGenerator testInstance = new CustomerSalesReportPdfGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
    }

    @Test
    public void testProductReport() throws Exception {
        final ProductReportPdfGenerator testInstance = new ProductReportPdfGenerator();

        byte[] reportBytes = testInstance.generateReport(FROM, TO);
        assertNotNull(reportBytes);

        final String textFromPage = PdfTextExtractor.getTextFromPage(new PdfReader(reportBytes), 1);
        assertThat(textFromPage, containsString(testInstance.getTitle()));
    }

    @Test
    public void testEmailAddressGets() throws Exception {
        final EmailExtractorReportGenerator emailExtractorReportGenerator = new EmailExtractorReportGenerator();

        final String tradeEmails = emailExtractorReportGenerator.getEmailsForTag(ShopifyConstants.TRADE_TAG);
        final String platinumEmails = emailExtractorReportGenerator.getEmailsForTag(ShopifyConstants.PLATINUM_TAG);
        final String goldEmails = emailExtractorReportGenerator.getEmailsForTag(ShopifyConstants.GOLD_TAG);
        final String affiliateEmails = emailExtractorReportGenerator.getEmailsForTag(ShopifyConstants.AFFILIATE_TAG);

        final StringBuilder output = new StringBuilder()
                .append(ShopifyConstants.TRADE_TAG).append(NEW_LINE).append(tradeEmails).append(NEW_LINE).append(NEW_LINE)
                .append(ShopifyConstants.PLATINUM_TAG).append(NEW_LINE).append(platinumEmails).append(NEW_LINE).append(NEW_LINE)
                .append(ShopifyConstants.GOLD_TAG).append(NEW_LINE).append(goldEmails).append(NEW_LINE).append(NEW_LINE)
                .append(ShopifyConstants.AFFILIATE_TAG).append(NEW_LINE).append(affiliateEmails).append(NEW_LINE).append(NEW_LINE)
                .append("All Emails").append(NEW_LINE).append(String.join(",", tradeEmails, platinumEmails, goldEmails, affiliateEmails)).append(NEW_LINE)
                ;

        emailExtractorReportGenerator.writeTextToFile(output.toString());
    }
}
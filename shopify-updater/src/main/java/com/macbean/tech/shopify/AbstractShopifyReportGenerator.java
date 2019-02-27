package com.macbean.tech.shopify;

import com.itextpdf.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractShopifyReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShopifyReportGenerator.class);

    public static final String DATE_FORMAT = "dd-MMM-yyyy";
    public static final String ORDER_DATE_FORMAT = "dd-MM-yy hh:mm";
    public static final String FILENAME_DATE_FORMAT = "yyyyMM";

    public static final float ONE_HUNDRED_PERCENT = 100f;
    public static final float SPACING = 25f;

    public ShopifyClient shopifyClient;

    /** REPORT PARAMS **/
    public ZonedDateTime from;
    public ZonedDateTime to;
    public Document document;
    public String dateFrom;
    public String dateTo;
    public String reference;

    protected abstract String getReferencePrefix();

    protected abstract String getFileSuffix();

    protected abstract byte[] getByteData();

    public byte[] generateReport(ZonedDateTime from, ZonedDateTime to) throws IOException {
        this.from = from;
        this.to = to;
        this.dateFrom = DateTimeFormatter.ofPattern(DATE_FORMAT).format(from);
        this.dateTo = DateTimeFormatter.ofPattern(DATE_FORMAT).format(to);
        this.reference = getReferencePrefix() + DateTimeFormatter.ofPattern(FILENAME_DATE_FORMAT).format(to);
        this.shopifyClient = new ShopifyClient();
        byte[] byteData = getByteData();
        writeDataToFile(reference, byteData);
        return byteData;
    }

    protected void writeDataToFile(String name, byte[] pdfBytes) {
        final String filename = ShopifyConstants.OUTPUT_DIRECTORY + File.separatorChar + name + getFileSuffix();
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(pdfBytes);
            LOGGER.debug("File written to {}", filename);
        } catch (IOException ioe) {
            LOGGER.error("Unable to write file to {} ", filename, ioe);
        }
    }
}

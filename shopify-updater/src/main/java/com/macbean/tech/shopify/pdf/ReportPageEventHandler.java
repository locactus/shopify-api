package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;

public class ReportPageEventHandler extends PdfPageEventHelper {

    private static final Font FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    private static final float ROTATION = 0f;
    private static final float Y_ADJUST = 10f;

    private String reportName;
    private String reportFrom;
    private String reportTo;

    ReportPageEventHandler(String reportName, String reportFrom, String reportTo) {
        this.reportName = reportName;
        this.reportFrom = reportFrom;
        this.reportTo = reportTo;
    }

    public void onEndPage(PdfWriter writer, Document document) {

        final Phrase leftHeader = new Phrase(ofPattern(AbstractShopifyReportGenerator.DATE_FORMAT).format(LocalDate.now()), FONT);
        final Phrase header = new Phrase(reportName, FONT);
        final Phrase rightHeader = new Phrase(reportFrom + " to " + reportTo, FONT);
        final Phrase footer = new Phrase("Page " + writer.getPageNumber(), FONT);

        final PdfContentByte directContent = writer.getDirectContent();

        float xLeftValue = document.left() + document.leftMargin();
        float xCentreValue = (document.right() - document.left()) / 2 + document.leftMargin();
        float xRightValue = document.right() - document.rightMargin();

        ColumnText.showTextAligned(directContent, Element.ALIGN_LEFT, leftHeader, xLeftValue,document.top() + Y_ADJUST, ROTATION);
        ColumnText.showTextAligned(directContent, Element.ALIGN_CENTER, header, xCentreValue,document.top() + Y_ADJUST, ROTATION);
        ColumnText.showTextAligned(directContent, Element.ALIGN_RIGHT, rightHeader, xRightValue,document.top() + Y_ADJUST, ROTATION);
        ColumnText.showTextAligned(directContent, Element.ALIGN_CENTER, footer, xCentreValue,document.bottom() - Y_ADJUST, ROTATION);
    }
}


package com.macbean.tech.shopify.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;

public class PageEventHandler extends PdfPageEventHelper {

    private static final Font FONT = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
    private static final float ROTATION = 0f;
    private static final float Y_ADJUST = 10f;

    public void onEndPage(PdfWriter writer, Document document) {

        final Phrase header = new Phrase(ofPattern(ShopifyReportGenerator.DATE_FORMAT).format(LocalDate.now()), FONT);
        final Phrase footer = new Phrase("Page " + writer.getPageNumber(), FONT);

        final PdfContentByte directContent = writer.getDirectContent();
        float xValue = (document.right() - document.left()) / 2 + document.leftMargin();
        ColumnText.showTextAligned(directContent, Element.ALIGN_CENTER, header,
                xValue,document.top() + Y_ADJUST, ROTATION);
        ColumnText.showTextAligned(directContent, Element.ALIGN_CENTER, footer,
                xValue,document.bottom() - Y_ADJUST, ROTATION);
    }
}


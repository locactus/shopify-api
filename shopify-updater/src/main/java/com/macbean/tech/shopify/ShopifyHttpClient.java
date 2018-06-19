package com.macbean.tech.shopify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.macbean.tech.shopify.ShopifyConstants.*;

public class ShopifyHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyHttpClient.class);

    private ShopifyInstance shopifyInstance;

    ShopifyHttpClient(ShopifyInstance shopifyInstance) {
        this.shopifyInstance = shopifyInstance;
        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (shopifyInstance.getUserName(), shopifyInstance.getPassword().toCharArray());
            }
        });
    }

    private InputStream get(String url, String mimeType) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(GET_REQUEST_METHOD);
        connection.setRequestProperty(HTTP_ACCEPT_PROPERTY, mimeType);
        return connection.getInputStream();
    }

    private InputStream getJson(String url) throws IOException {
        return get(url, JSON_CONTENT_TYPE);
    }

    InputStream getProductsJson() throws IOException {
        return get(shopifyInstance.getGetProductsUrl(), JSON_CONTENT_TYPE);
    }

    InputStream getOrdersJson(long page) throws IOException {
        return get(shopifyInstance.getGetOrdersUrl(ORDER_STATUS_ANY, ORDER_FINANCIAL_STATUS_PAID,
                String.valueOf(ORDER_LIMIT_MAX), String.valueOf(page), null, null), JSON_CONTENT_TYPE);
    }

    InputStream getOrdersJson(long page, ZonedDateTime from, ZonedDateTime to) throws IOException {
        return get(shopifyInstance.getGetOrdersUrl(ORDER_STATUS_ANY, ORDER_FINANCIAL_STATUS_PAID,
                String.valueOf(ORDER_LIMIT_MAX), String.valueOf(page), from, to), JSON_CONTENT_TYPE);
    }

    public void putVariant(Long variantId, String requestMethod, String payload) throws IOException {
        putJson(shopifyInstance.getPutVariantUrl(variantId), requestMethod, payload);
    }

    private void putJson(String url, String requestMethod, String payload) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty(HTTP_CONTENT_TYPE_PROPERTY, JSON_CONTENT_TYPE);
        connection.setRequestProperty(HTTP_ACCEPT_PROPERTY, JSON_CONTENT_TYPE);
        LOGGER.debug("***** SEND JSON *****");
        LOGGER.debug(url);
        LOGGER.debug(requestMethod);
        LOGGER.debug(payload);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(payload);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        connection.getInputStream();
    }
}

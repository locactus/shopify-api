package com.macbean.tech.shopify;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import static com.macbean.tech.shopify.ShopifyConstants.*;

public class ShopifyHttpClient {

    private ShopifyHttpClient() {}

    static {
        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (SHOPIFY_USERNAME, SHOPIFY_PASSWORD.toCharArray());
            }
        });
    }

    static InputStream get(String url, String mimeType) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(GET_REQUEST_METHOD);
        connection.setRequestProperty(GET_REQUEST_PROPERTY, mimeType);
        return connection.getInputStream();
    }

    static InputStream getJson(String url) throws IOException {
        return get(url, JSON_CONTENT_TYPE);
    }

    static void sendJson(String url, String requestMethod, String payload) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", JSON_CONTENT_TYPE);
        connection.setRequestProperty("Accept", JSON_CONTENT_TYPE);
        System.out.println("***** SEND JSON *****");
        System.out.println(url);
        System.out.println(requestMethod);
        System.out.println(payload);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(payload);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        connection.getInputStream();
    }
}

package com.macbean.tech.shopify;

import java.util.List;

import static java.util.Arrays.asList;

public class ShopifyConstants {

    static final String GET_REQUEST_METHOD = "GET";
    static final String PUT_REQUEST_METHOD = "PUT";

    static final String HTTP_ACCEPT_PROPERTY = "Accept";
    static final String HTTP_CONTENT_TYPE_PROPERTY = "Content-Type";

    static final String JSON_CONTENT_TYPE = "application/json";
    static final String HTTPS_PREFIX = "https://";
    static final String JSON_SUFFIX = ".json";
    static final String GET_PRODUCTS = "/admin/products" + JSON_SUFFIX;
    static final String GET_ORDERS = "/admin/orders" + JSON_SUFFIX;
    static final String PUT_VARIANT = "/admin/variants/";

    static final String ORDER_STATUS_ANY = "any";
    static final String ORDER_FINANCIAL_STATUS_PAID = "paid";
    static final long ORDER_LIMIT_MAX = 250;
    static final ShopifyInstance UK_INSTANCE = new ShopifyInstance(
            "",
            "",
            ""
    );
}

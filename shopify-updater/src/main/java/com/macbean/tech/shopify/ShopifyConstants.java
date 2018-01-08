package com.macbean.tech.shopify;

import java.util.List;

import static java.util.Arrays.asList;

public class ShopifyConstants {

    static final String JSON_CONTENT_TYPE = "application/json";
    static final String GET_REQUEST_METHOD = "GET";
    static final String POST_REQUEST_METHOD = "POST";
    static final String PUT_REQUEST_METHOD = "PUT";
    static final String GET_REQUEST_PROPERTY = "Accept";

    static final List<String> PRICING_PROFILES = asList("(gold)","(silver)","(bronze)","(stringer)","(sponsorship)","(affiliate)");

    static final String INVENTORY_SHOPIFY = "shopify";
    static final String INVENTORY_NON_SHOPIFY = null;

    static final String HTTPS_PREFIX = "https://";
    static final String JSON_SUFFIX = ".json";
    static final String GET_PRODUCTS = "/admin/products" + JSON_SUFFIX;
    static final String PUT_VARIANT = "/admin/variants/";

    static final ShopifyInstance UK_INSTANCE = new ShopifyInstance(
            "squashuk.myshopify.com",
            "ec985b46ffe3b3d340719bfd0e415e31",
            "1b4cabb72773b3299104912ed5b21954"
    );
}

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
    static final String SHOPIFY_USERNAME = "";
    static final String SHOPIFY_PASSWORD = "";
    static final String API_PAIR = SHOPIFY_USERNAME+";"+SHOPIFY_PASSWORD+"@";
    static final String SHOPIFY_INSTANCE = "squashuk.myshopify.com";

    static final String BASE_URL = HTTPS_PREFIX + API_PAIR + SHOPIFY_INSTANCE;

    static final String GET_PRODUCTS = "/admin/products.json";
    static final String PUT_VARIANT(Long variantId) { return "/admin/variants/" + variantId + ".json"; }

    static final String GET_PRODUCTS_URL = BASE_URL + GET_PRODUCTS;
    static final String PUT_VARIANT_URL(Long variantId) { return BASE_URL + PUT_VARIANT(variantId); }
}

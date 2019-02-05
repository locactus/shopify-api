package com.macbean.tech.shopify;

public class ShopifyConstants {

    // Pricing Tags
    public static final String TRADE_TAG = "trade";
    public static final String PLATINUM_TAG = "platinum";
    public static final String GOLD_TAG = "gold";
    public static final String AFFILIATE_TAG = "affiliate";
    public static final String FREE_TAG = "free";
    public static final String DIRECT_TAG = "direct";
    public static final String DIGITAL_TAG = "digital";
    public static final String SPONSORSHIP_TAG = "sponsorship";
    public static final String PSA_PLAYER_TAG = "psaplayer";

    // Country Codes
    public static final String UK = "GB";
    public static final String IRELAND = "IE";

    // HTTP
    static final String GET_REQUEST_METHOD = "GET";
    static final String PUT_REQUEST_METHOD = "PUT";
    static final String HTTP_ACCEPT_PROPERTY = "Accept";
    static final String HTTP_CONTENT_TYPE_PROPERTY = "Content-Type";
    static final String JSON_CONTENT_TYPE = "application/json";

    // URL
    static final String HTTPS_PREFIX = "https://";
    static final String JSON_SUFFIX = ".json";
    static final String ADMIN = "/admin";
    static final String GET_PRODUCTS = ADMIN + "/products" + JSON_SUFFIX;
    static final String GET_ORDERS = ADMIN + "/orders" + JSON_SUFFIX;
    static final String GET_CUSTOMERS = ADMIN + "/customers" + JSON_SUFFIX;
    static final String GET_INVENTORY_ITEMS = ADMIN + "/inventory_items" + JSON_SUFFIX;
    static final String PUT_VARIANT = ADMIN + "/variants/";

    // RESOURCES
    public static final String EYE_LOGO_URL = "https://cdn.shopify.com/s/files/1/0016/4996/7140/files/Eye_-_ball_and_name.png";

    // VARIOUS
    static final String ORDER_STATUS_ANY = "any";
    static final String ORDER_FINANCIAL_STATUS_PAID = "paid";
    static final long ORDER_LIMIT_MAX = 250;
    static final long PRODUCT_LIMIT_MAX = 250;
    static final long CUSTOMER_LIMIT_MAX = 250;
    static final int MAX_INVENTORY_ITEM_PARAMS = 99;
    public static final String NO_COUNTRY_AVAILABLE = "N/A";

    // INSTANCE INFO
    static final ShopifyInstance UK_INSTANCE = new ShopifyInstance(
            "",
            "",
            ""
    );

    // OUTPUT
    public static final String OUTPUT_DIRECTORY = "/Users/andrew/Desktop";
}

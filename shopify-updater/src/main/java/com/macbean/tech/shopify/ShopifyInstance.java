package com.macbean.tech.shopify;

import static com.macbean.tech.shopify.ShopifyConstants.*;

public class ShopifyInstance {

    private String domain;
    private String userName;
    private String password;

    public ShopifyInstance(String domain, String userName, String password) {
        this.domain = domain;
        this.userName = userName;
        this.password = password;
    }

    public String getPutVariantUrl(Long variantId) {
        return getBaseUrl() + PUT_VARIANT + variantId + JSON_SUFFIX;
    }

    public String getGetProductsUrl() {
        return getBaseUrl() + GET_PRODUCTS;
    }

    public String getBaseUrl() {
        return HTTPS_PREFIX + userName + ";" + getPassword() + "@" + domain;
    }

    public String getDomain() {
        return domain;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}

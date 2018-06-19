package com.macbean.tech.shopify;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public String getGetOrdersUrl(String status, String financialStatus, String limit, String page, ZonedDateTime from, ZonedDateTime to) {
        final String fromString = from == null ? null : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(from);
        final String toString = to == null ? null : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(to);
        final StringBuilder getOrdersUrl = new StringBuilder(getBaseUrl()).append(GET_ORDERS)
                .append("?status=").append(status)
                .append("&financial_status=").append(financialStatus)
                .append("&limit=").append(limit)
                .append("&page=").append(page);
        if (fromString != null && toString != null) {
            getOrdersUrl.append("&created_at_min=").append(fromString)
                    .append("&created_at_max=").append(toString);
        }
        return getOrdersUrl.toString();
    }
    private String getBaseUrl() {
        return HTTPS_PREFIX + getUserName() + ";" + getPassword() + "@" + getDomain();
    }

    private String getDomain() {
        return domain;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}

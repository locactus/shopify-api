package com.macbean.tech.shopify;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ShopifyInstanceTest {

    private final ShopifyInstance testInstance = new ShopifyInstance(
            "test.shopify.com",
            "username",
            "password"
    );

    @Test
    public void getPutVariantUrl() {
        assertThat(testInstance.getPutVariantUrl(1967L),
                is("https://username;password@test.shopify.com/admin/variants/1967.json"));
    }

    @Test
    public void getGetProductsUrl() {
        assertThat(testInstance.getGetProductsUrl(),
                is("https://username;password@test.shopify.com/admin/products.json"));
    }
}
package com.macbean.tech.shopify;

import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Products;
import com.macbean.tech.shopify.model.Variant;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;

public class ShopifyClientTest {

    private ShopifyClient testInstance = new ShopifyClient();

    private static final int NO_OF_PRODUCTS = 50;
    private static final int NO_OF_PRODUCT_TYPES = 17;
    private static final int NO_OF_INCORRECT_INVENTORY_VARIANTS = 0;

    @Test
    public void testGetProducts() throws Exception {
        final Products products = testInstance.getAllProducts();
        assertNotNull(products);
        assertNotNull(products.getProducts());
        assertThat(products.getProducts(), hasSize(NO_OF_PRODUCTS));
    }

    @Test
    public void testGetProductsByType() throws Exception {
        Map<String, List<Product>> productsByType = testInstance.getAllProductsByType();
        assertNotNull(productsByType);
        assertNotNull(productsByType.keySet());
        assertThat(productsByType.keySet(), hasSize(NO_OF_PRODUCT_TYPES));
    }

    @Test
    public void testGetIncorrectVariants() throws Exception {
        List<Variant> incorrectInventory = testInstance.getAllVariantsWithIncorrectInventory();
        assertNotNull(incorrectInventory);
        assertThat(incorrectInventory, hasSize(NO_OF_INCORRECT_INVENTORY_VARIANTS));
    }

    @Test
    public void testUpdateIncorrectVariants() throws Exception {
        testInstance.updateIncorrectVariants();
        List<Variant> incorrectInventory = testInstance.getAllVariantsWithIncorrectInventory();
        assertNotNull(incorrectInventory);
        assertThat(incorrectInventory, hasSize(NO_OF_INCORRECT_INVENTORY_VARIANTS));
    }
}
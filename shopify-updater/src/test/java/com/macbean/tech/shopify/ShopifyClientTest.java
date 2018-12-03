package com.macbean.tech.shopify;

import com.macbean.tech.shopify.model.Customers;
import com.macbean.tech.shopify.model.Orders;
import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Products;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class ShopifyClientTest {

    private ShopifyClient testInstance = new ShopifyClient();

    private static final int NO_OF_PRODUCTS = 119;
    private static final int NO_OF_PRODUCT_TYPES = 20;
    private static final int NO_OF_ORDERS_IN_MAY_2018 = 92;

    private static final String INVENTORY_ITEM_ID = "7515712487460";

    @Test
    public void testGetProducts() throws Exception {
        final Products products = testInstance.getAllProducts();
        assertNotNull(products);
        assertNotNull(products.getProducts());
        assertThat(products.getProducts(), hasSize(NO_OF_PRODUCTS));
    }

    @Test
    public void testGetCustomers() throws Exception {
        final Customers customers = testInstance.getAllCustomers();
        assertNotNull(customers);
        assertNotNull(customers.getCustomers());
        assertThat(customers.getCustomers().size(), greaterThan(0));
    }

    @Test
    public void testGetProductsByType() throws Exception {
        Map<String, List<Product>> productsByType = testInstance.getAllProductsByType();
        assertNotNull(productsByType);
        assertNotNull(productsByType.keySet());
        assertThat(productsByType.keySet(), hasSize(NO_OF_PRODUCT_TYPES));
    }

    @Test
    public void testGetAllOrders() throws Exception {
        final Orders orders = testInstance.getAllOrders();
        assertNotNull(orders);
        assertNotNull(orders.getOrders());
    }

    @Test
    public void testGetInventoryItem() throws Exception {
        final InventoryItems inventoryItems = testInstance.getInventoryItems(asList(INVENTORY_ITEM_ID));
        assertNotNull(inventoryItems);
        assertNotNull(inventoryItems.getInventoryItems());
    }

    @Test
    public void testGetAllOrdersFromPeriod() throws Exception {
        final Orders orders = testInstance.getAllOrders(
                ZonedDateTime.of(2018,5,1, 0, 0, 0, 0, ZoneId.systemDefault()),
                ZonedDateTime.of(2018,5,31,23,59,59,0, ZoneId.systemDefault())
        );
        assertNotNull(orders);
        assertNotNull(orders.getOrders());
        assertThat(orders.getOrders().size(), is(NO_OF_ORDERS_IN_MAY_2018));
    }
}
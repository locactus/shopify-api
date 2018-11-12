package com.macbean.tech.shopify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macbean.tech.shopify.model.Customers;
import com.macbean.tech.shopify.model.Orders;
import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Products;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.macbean.tech.shopify.ShopifyConstants.*;

public class ShopifyClient {

    private ShopifyHttpClient shopifyHttpClient = new ShopifyHttpClient(UK_INSTANCE);

    public Customers getAllCustomers() throws IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        long pageCount = 1;
        final Customers allCustomers = new Customers();

        InputStream jsonInputstream = shopifyHttpClient.getCustomersJson(pageCount);
        Customers currentCustomers = jsonMapper.readValue(jsonInputstream, Customers.class);
        allCustomers.setCustomers(currentCustomers.getCustomers());

        while (currentCustomers.getCustomers().size() == CUSTOMER_LIMIT_MAX) {
            pageCount++;
            jsonInputstream = shopifyHttpClient.getCustomersJson(pageCount);
            currentCustomers = jsonMapper.readValue(jsonInputstream, Customers.class);
            allCustomers.getCustomers().addAll(currentCustomers.getCustomers());
        }
        return allCustomers;
    }

    public Products getAllProducts() throws IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        long pageCount = 1;
        final Products allProducts = new Products();

        InputStream jsonInputstream = shopifyHttpClient.getProductsJson(pageCount);
        Products currentProducts = jsonMapper.readValue(jsonInputstream, Products.class);
        allProducts.setProducts(currentProducts.getProducts());

        while (currentProducts.getProducts().size() == PRODUCT_LIMIT_MAX) {
            pageCount++;
            jsonInputstream = shopifyHttpClient.getProductsJson(pageCount);
            currentProducts = jsonMapper.readValue(jsonInputstream, Products.class);
            allProducts.getProducts().addAll(currentProducts.getProducts());
        }
        return allProducts;
    }

    public Map<String, List<Product>> getAllProductsByType() throws IOException {
        final Map<String, List<Product>> result = new HashMap<>();
        final Products allProducts = getAllProducts();
        for (final Product product : allProducts.getProducts()) {
            if (product.getProductType() != null && product.getProductType().length() != 0) {
                List<Product> typeProducts = result.get(product.getProductType());
                if (typeProducts == null) typeProducts = new ArrayList<>();
                typeProducts.add(product);
                result.put(product.getProductType(), typeProducts);
            }
        }
        return result;
    }

    public Orders getAllOrders() throws IOException {
        return getAllOrders(null, null);
    }

    public Orders getAllOrders(ZonedDateTime from, ZonedDateTime to) throws IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        long pageCount = 1;
        final Orders allOrders = new Orders();

        InputStream jsonInputstream = shopifyHttpClient.getOrdersJson(pageCount, from, to);
        Orders currentOrders = jsonMapper.readValue(jsonInputstream, Orders.class);
        allOrders.setProducts(currentOrders.getOrders());

        while (currentOrders.getOrders().size() == ORDER_LIMIT_MAX) {
            pageCount++;
            jsonInputstream = shopifyHttpClient.getOrdersJson(pageCount, from, to);
            currentOrders = jsonMapper.readValue(jsonInputstream, Orders.class);
            allOrders.getOrders().addAll(currentOrders.getOrders());
        }
        return allOrders;
    }
}

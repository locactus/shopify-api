package com.macbean.tech.shopify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macbean.tech.shopify.model.Order;
import com.macbean.tech.shopify.model.Orders;
import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Products;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.macbean.tech.shopify.ShopifyConstants.ORDER_LIMIT_MAX;
import static com.macbean.tech.shopify.ShopifyConstants.PRODUCT_LIMIT_MAX;
import static com.macbean.tech.shopify.ShopifyConstants.UK_INSTANCE;

public class ShopifyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyClient.class);

    private ShopifyHttpClient shopifyHttpClient = new ShopifyHttpClient(UK_INSTANCE);

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

        LOGGER.debug("***** All Products *****");
        allProducts.getProducts().stream().map(Product::getTitle).sorted().forEach(LOGGER::debug);
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
        LOGGER.debug("***** Product Types *****");
        result.keySet().stream().sorted().forEach(LOGGER::debug);
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

        LOGGER.debug("***** All Orders *****");
        allOrders.getOrders().stream().map(Order::getName).sorted().forEach(LOGGER::debug);
        return allOrders;
    }
}

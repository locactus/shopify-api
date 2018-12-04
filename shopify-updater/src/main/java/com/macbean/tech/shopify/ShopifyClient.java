package com.macbean.tech.shopify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macbean.tech.shopify.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.macbean.tech.shopify.ShopifyConstants.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

    public InventoryItems getInventoryItems(List<String> inventoryItemIds) throws IOException {
        int count = 0;
        int newStartIndex = MAX_INVENTORY_ITEM_PARAMS * count;
        int newEndIndex = newStartIndex + MAX_INVENTORY_ITEM_PARAMS;

        List<String> currentSublist = inventoryItemIds.subList(newStartIndex,newEndIndex > inventoryItemIds.size() ? inventoryItemIds.size() : newEndIndex);
        InventoryItems inventoryItems = getInventoryItems(currentSublist.toArray(new String[0]));

        while (newEndIndex <= inventoryItemIds.size()) {
            count++;
            newStartIndex = MAX_INVENTORY_ITEM_PARAMS * count;
            newEndIndex = newStartIndex + MAX_INVENTORY_ITEM_PARAMS;

            currentSublist = inventoryItemIds.subList(newStartIndex, newEndIndex > inventoryItemIds.size() ? inventoryItemIds.size() : newEndIndex);
            inventoryItems.getInventoryItems().addAll(getInventoryItems(currentSublist.toArray(new String[0])).getInventoryItems());
        }
        return inventoryItems;
    }

    private InventoryItems getInventoryItems(String... inventoryItemIds) throws IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        long pageCount = 1;
        InventoryItems allInventoryItems = new InventoryItems();

        InputStream jsonInputstream = shopifyHttpClient.getInventoryItems(pageCount, inventoryItemIds);
        InventoryItems currentInventoryItems = jsonMapper.readValue(jsonInputstream, InventoryItems.class);
        allInventoryItems.setInventoryItems(currentInventoryItems.getInventoryItems());

        while (currentInventoryItems.getInventoryItems().size() == ORDER_LIMIT_MAX) {
            pageCount++;
            jsonInputstream = shopifyHttpClient.getInventoryItems(pageCount, inventoryItemIds);
            currentInventoryItems = jsonMapper.readValue(jsonInputstream, InventoryItems.class);
            allInventoryItems.setInventoryItems(currentInventoryItems.getInventoryItems());
        }
        return allInventoryItems;
    }

    public Map<String, BigDecimal> getAllInventoryIdCosts() throws IOException {
        final Map<String, List<Product>> productsByType = getAllProductsByType();
        return getInventoryIdCosts(productsByType);
    }

    public Map<String, BigDecimal> getInventoryIdCosts(Map<String, List<Product>> productsByType) throws IOException {

        final List<String> inventoryIds = new ArrayList<>();

        for (String productType : productsByType.keySet()) {
            for (Product product : productsByType.get(productType)) {
                for (Variant variant : product.getVariants()) {
                    if (!isEmpty(variant.getInventoryItemId())) {
                        inventoryIds.add(variant.getInventoryItemId());
                    }
                }
            }
        }

        final Map<String, BigDecimal> inventoryIdCosts = new HashMap<>();

        final InventoryItems inventoryItemsList = getInventoryItems(inventoryIds);

        for (InventoryItem inventoryItem : inventoryItemsList.getInventoryItems()) {
            inventoryIdCosts.put(String.valueOf(inventoryItem.getId()), isNotEmpty(inventoryItem.getCost()) ? new BigDecimal(inventoryItem.getCost()) : BigDecimal.ZERO);
        }

        return inventoryIdCosts;
    }
}

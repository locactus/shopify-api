package com.macbean.tech.shopify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macbean.tech.shopify.model.Product;
import com.macbean.tech.shopify.model.Products;
import com.macbean.tech.shopify.model.Variant;
import com.macbean.tech.shopify.model.VariantWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.macbean.tech.shopify.ShopifyConstants.*;

public class ShopifyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyClient.class);

    private ShopifyHttpClient shopifyHttpClient = new ShopifyHttpClient(UK_INSTANCE);

    public Products getAllProducts() throws IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        final InputStream jsonInputstream = shopifyHttpClient.getProductsJson();
        Products products = jsonMapper.readValue(jsonInputstream, Products.class);
        LOGGER.debug("***** All Products *****");
        products.getProducts().stream().map(product -> product.getTitle()).sorted().forEach(LOGGER::debug);
        return products;
    }

    public Map<String, List<Product>> getAllProductsByType() throws IOException {
        final Map<String, List<Product>> result = new HashMap<>();
        final Products allProducts = getAllProducts();
        for (final Product product : allProducts.getProducts()) {
            List<Product> typeProducts = result.get(product.getProductType());
            if (typeProducts == null) typeProducts = new ArrayList<>();
            typeProducts.add(product);
            result.put(product.getProductType(), typeProducts);
        }
        LOGGER.debug("***** Product Types *****");
        result.keySet().stream().sorted().forEach(LOGGER::debug);
        return result;
    }

}

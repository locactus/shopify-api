package com.macbean.tech.shopify;

import com.macbean.tech.shopify.model.Customer;

public class ShopifyUtils {

    public static String determineCustomerTag(Customer customer) {

        String tag;

        if ("0.00".equals(customer.getTotalSpent())) {
            tag = ShopifyConstants.FREE_TAG;
        } else if (customer.getTags().contains(ShopifyConstants.TRADE_TAG)) {
            tag = ShopifyConstants.TRADE_TAG;
        } else if (customer.getTags().contains(ShopifyConstants.PLATINUM_TAG)) {
            tag = ShopifyConstants.PLATINUM_TAG;
        } else if (customer.getTags().contains(ShopifyConstants.GOLD_TAG)) {
            tag = ShopifyConstants.GOLD_TAG;
        } else if (customer.getTags().contains(ShopifyConstants.AFFILIATE_TAG)) {
            tag = ShopifyConstants.AFFILIATE_TAG;
        } else {
            tag = ShopifyConstants.DIRECT_TAG;
        }

        return tag;
    }
}

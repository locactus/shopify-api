package com.macbean.tech.shopify.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Orders {

    @JsonProperty("orders")
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setProducts(List<Order> orders) {
        this.orders = orders;
    }
}

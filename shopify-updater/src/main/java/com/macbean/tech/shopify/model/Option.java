package com.macbean.tech.shopify.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "product_id",
        "name",
        "position",
        "values"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Option {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("position")
    private Long position;
    @JsonProperty("values")
    private List<Object> values = null;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("product_id")
    public Long getProductId() {
        return productId;
    }

    @JsonProperty("product_id")
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("position")
    public Long getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(Long position) {
        this.position = position;
    }

    @JsonProperty("values")
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<Object> values) {
        this.values = values;
    }

}
package com.macbean.tech.shopify.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "product_id",
        "title",
        "price",
        "sku",
        "position",
        "grams",
        "inventory_policy",
        "compare_at_price",
        "fulfillment_service",
        "inventory_management",
        "option1",
        "option2",
        "option3",
        "created_at",
        "updated_at",
        "taxable",
        "barcode",
        "image_id",
        "inventory_quantity",
        "weight",
        "weight_unit",
        "old_inventory_quantity",
        "requires_shipping"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private String price;
    @JsonProperty("sku")
    private String sku;
    @JsonProperty("position")
    private Long position;
    @JsonProperty("grams")
    private Long grams;
    @JsonProperty("inventory_policy")
    private String inventoryPolicy;
    @JsonProperty("compare_at_price")
    private String compareAtPrice;
    @JsonProperty("fulfillment_service")
    private String fulfillmentService;
    @JsonProperty("inventory_management")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String inventoryManagement;
    @JsonProperty("option1")
    private String option1;
    @JsonProperty("option2")
    private Object option2;
    @JsonProperty("option3")
    private Object option3;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("taxable")
    private Boolean taxable;
    @JsonProperty("barcode")
    private String barcode;
    @JsonProperty("image_id")
    private Long imageId;
    @JsonProperty("inventory_quantity")
    private Long inventoryQuantity;
    @JsonProperty("weight")
    private Long weight;
    @JsonProperty("weight_unit")
    private String weightUnit;
    @JsonProperty("old_inventory_quantity")
    private Long oldInventoryQuantity;
    @JsonProperty("requires_shipping")
    private Boolean requiresShipping;

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

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("price")
    public String getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(String price) {
        this.price = price;
    }

    @JsonProperty("sku")
    public String getSku() {
        return sku;
    }

    @JsonProperty("sku")
    public void setSku(String sku) {
        this.sku = sku;
    }

    @JsonProperty("position")
    public Long getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(Long position) {
        this.position = position;
    }

    @JsonProperty("grams")
    public Long getGrams() {
        return grams;
    }

    @JsonProperty("grams")
    public void setGrams(Long grams) {
        this.grams = grams;
    }

    @JsonProperty("inventory_policy")
    public String getInventoryPolicy() {
        return inventoryPolicy;
    }

    @JsonProperty("inventory_policy")
    public void setInventoryPolicy(String inventoryPolicy) {
        this.inventoryPolicy = inventoryPolicy;
    }

    @JsonProperty("compare_at_price")
    public String getCompareAtPrice() {
        return compareAtPrice;
    }

    @JsonProperty("compare_at_price")
    public void setCompareAtPrice(String compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    @JsonProperty("fulfillment_service")
    public String getFulfillmentService() {
        return fulfillmentService;
    }

    @JsonProperty("fulfillment_service")
    public void setFulfillmentService(String fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @JsonProperty("inventory_management")
    public String getInventoryManagement() {
        return inventoryManagement;
    }

    @JsonProperty("inventory_management")
    public void setInventoryManagement(String inventoryManagement) {
        this.inventoryManagement = inventoryManagement;
    }

    @JsonProperty("option1")
    public String getOption1() {
        return option1;
    }

    @JsonProperty("option1")
    public void setOption1(String option1) {
        this.option1 = option1;
    }

    @JsonProperty("option2")
    public Object getOption2() {
        return option2;
    }

    @JsonProperty("option2")
    public void setOption2(Object option2) {
        this.option2 = option2;
    }

    @JsonProperty("option3")
    public Object getOption3() {
        return option3;
    }

    @JsonProperty("option3")
    public void setOption3(Object option3) {
        this.option3 = option3;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("taxable")
    public Boolean getTaxable() {
        return taxable;
    }

    @JsonProperty("taxable")
    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    @JsonProperty("barcode")
    public String getBarcode() {
        return barcode;
    }

    @JsonProperty("barcode")
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @JsonProperty("image_id")
    public Long getImageId() {
        return imageId;
    }

    @JsonProperty("image_id")
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    @JsonProperty("inventory_quantity")
    public Long getInventoryQuantity() {
        return inventoryQuantity;
    }

    @JsonProperty("inventory_quantity")
    public void setInventoryQuantity(Long inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    @JsonProperty("weight")
    public Long getWeight() {
        return weight;
    }

    @JsonProperty("weight")
    public void setWeight(Long weight) {
        this.weight = weight;
    }

    @JsonProperty("weight_unit")
    public String getWeightUnit() {
        return weightUnit;
    }

    @JsonProperty("weight_unit")
    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    @JsonProperty("old_inventory_quantity")
    public Long getOldInventoryQuantity() {
        return oldInventoryQuantity;
    }

    @JsonProperty("old_inventory_quantity")
    public void setOldInventoryQuantity(Long oldInventoryQuantity) {
        this.oldInventoryQuantity = oldInventoryQuantity;
    }

    @JsonProperty("requires_shipping")
    public Boolean getRequiresShipping() {
        return requiresShipping;
    }

    @JsonProperty("requires_shipping")
    public void setRequiresShipping(Boolean requiresShipping) {
        this.requiresShipping = requiresShipping;
    }

}
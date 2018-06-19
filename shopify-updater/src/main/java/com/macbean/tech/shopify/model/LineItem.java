package com.macbean.tech.shopify.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "variant_id",
        "title",
        "quantity",
        "price",
        "sku",
        "variant_title",
        "vendor",
        "fulfillment_service",
        "product_id",
        "requires_shipping",
        "taxable",
        "gift_card",
        "name",
        "variant_inventory_management",
        "properties",
        "product_exists",
        "fulfillable_quantity",
        "grams",
        "total_discount",
        "fulfillment_status",
        "discount_allocations",
        "tax_lines",
        "origin_location"
})
public class LineItem {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("variant_id")
    private Long variantId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("quantity")
    private Long quantity;
    @JsonProperty("price")
    private String price;
    @JsonProperty("sku")
    private String sku;
    @JsonProperty("variant_title")
    private String variantTitle;
    @JsonProperty("vendor")
    private String vendor;
    @JsonProperty("fulfillment_service")
    private String fulfillmentService;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("requires_shipping")
    private Boolean requiresShipping;
    @JsonProperty("taxable")
    private Boolean taxable;
    @JsonProperty("gift_card")
    private Boolean giftCard;
    @JsonProperty("name")
    private String name;
    @JsonProperty("variant_inventory_management")
    private String variantInventoryManagement;
    @JsonProperty("properties")
    private List<Object> properties = null;
    @JsonProperty("product_exists")
    private Boolean productExists;
    @JsonProperty("fulfillable_quantity")
    private Long fulfillableQuantity;
    @JsonProperty("grams")
    private Long grams;
    @JsonProperty("total_discount")
    private String totalDiscount;
    @JsonProperty("fulfillment_status")
    private String fulfillmentStatus;
    @JsonProperty("discount_allocations")
    private List<DiscountAllocation> discountAllocations = null;
    @JsonProperty("tax_lines")
    private List<TaxLine> taxLines = null;
    @JsonProperty("origin_location")
    private OriginLocation originLocation;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("variant_id")
    public Long getVariantId() {
        return variantId;
    }

    @JsonProperty("variant_id")
    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("quantity")
    public Long getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
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

    @JsonProperty("variant_title")
    public String getVariantTitle() {
        return variantTitle;
    }

    @JsonProperty("variant_title")
    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @JsonProperty("fulfillment_service")
    public String getFulfillmentService() {
        return fulfillmentService;
    }

    @JsonProperty("fulfillment_service")
    public void setFulfillmentService(String fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @JsonProperty("product_id")
    public Long getProductId() {
        return productId;
    }

    @JsonProperty("product_id")
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @JsonProperty("requires_shipping")
    public Boolean getRequiresShipping() {
        return requiresShipping;
    }

    @JsonProperty("requires_shipping")
    public void setRequiresShipping(Boolean requiresShipping) {
        this.requiresShipping = requiresShipping;
    }

    @JsonProperty("taxable")
    public Boolean getTaxable() {
        return taxable;
    }

    @JsonProperty("taxable")
    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    @JsonProperty("gift_card")
    public Boolean getGiftCard() {
        return giftCard;
    }

    @JsonProperty("gift_card")
    public void setGiftCard(Boolean giftCard) {
        this.giftCard = giftCard;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("variant_inventory_management")
    public String getVariantInventoryManagement() {
        return variantInventoryManagement;
    }

    @JsonProperty("variant_inventory_management")
    public void setVariantInventoryManagement(String variantInventoryManagement) {
        this.variantInventoryManagement = variantInventoryManagement;
    }

    @JsonProperty("properties")
    public List<Object> getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(List<Object> properties) {
        this.properties = properties;
    }

    @JsonProperty("product_exists")
    public Boolean getProductExists() {
        return productExists;
    }

    @JsonProperty("product_exists")
    public void setProductExists(Boolean productExists) {
        this.productExists = productExists;
    }

    @JsonProperty("fulfillable_quantity")
    public Long getFulfillableQuantity() {
        return fulfillableQuantity;
    }

    @JsonProperty("fulfillable_quantity")
    public void setFulfillableQuantity(Long fulfillableQuantity) {
        this.fulfillableQuantity = fulfillableQuantity;
    }

    @JsonProperty("grams")
    public Long getGrams() {
        return grams;
    }

    @JsonProperty("grams")
    public void setGrams(Long grams) {
        this.grams = grams;
    }

    @JsonProperty("total_discount")
    public String getTotalDiscount() {
        return totalDiscount;
    }

    @JsonProperty("total_discount")
    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @JsonProperty("fulfillment_status")
    public String getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    @JsonProperty("fulfillment_status")
    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    @JsonProperty("discount_allocations")
    public List<DiscountAllocation> getDiscountAllocations() {
        return discountAllocations;
    }

    @JsonProperty("discount_allocations")
    public void setDiscountAllocations(List<DiscountAllocation> discountAllocations) {
        this.discountAllocations = discountAllocations;
    }

    @JsonProperty("tax_lines")
    public List<TaxLine> getTaxLines() {
        return taxLines;
    }

    @JsonProperty("tax_lines")
    public void setTaxLines(List<TaxLine> taxLines) {
        this.taxLines = taxLines;
    }

    @JsonProperty("origin_location")
    public OriginLocation getOriginLocation() {
        return originLocation;
    }

    @JsonProperty("origin_location")
    public void setOriginLocation(OriginLocation originLocation) {
        this.originLocation = originLocation;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
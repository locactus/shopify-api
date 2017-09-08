package com.macbean.tech.shopify.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "title",
        "body_html",
        "vendor",
        "product_type",
        "created_at",
        "handle",
        "updated_at",
        "published_at",
        "template_suffix",
        "published_scope",
        "tags",
        "variants",
        "options",
        "images",
        "image"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("body_html")
    private String bodyHtml;
    @JsonProperty("vendor")
    private String vendor;
    @JsonProperty("product_type")
    private String productType;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("handle")
    private String handle;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("published_at")
    private Object publishedAt;
    @JsonProperty("template_suffix")
    private Object templateSuffix;
    @JsonProperty("published_scope")
    private String publishedScope;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("variants")
    private List<Variant> variants = null;
    @JsonProperty("options")
    private List<Option> options = null;
    @JsonProperty("images")
    private List<Object> images = null;
    @JsonProperty("image")
    private Image image;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("body_html")
    public String getBodyHtml() {
        return bodyHtml;
    }

    @JsonProperty("body_html")
    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @JsonProperty("product_type")
    public String getProductType() {
        return productType;
    }

    @JsonProperty("product_type")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("handle")
    public String getHandle() {
        return handle;
    }

    @JsonProperty("handle")
    public void setHandle(String handle) {
        this.handle = handle;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("published_at")
    public Object getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty("published_at")
    public void setPublishedAt(Object publishedAt) {
        this.publishedAt = publishedAt;
    }

    @JsonProperty("template_suffix")
    public Object getTemplateSuffix() {
        return templateSuffix;
    }

    @JsonProperty("template_suffix")
    public void setTemplateSuffix(Object templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    @JsonProperty("published_scope")
    public String getPublishedScope() {
        return publishedScope;
    }

    @JsonProperty("published_scope")
    public void setPublishedScope(String publishedScope) {
        this.publishedScope = publishedScope;
    }

    @JsonProperty("tags")
    public String getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(String tags) {
        this.tags = tags;
    }

    @JsonProperty("variants")
    public List<Variant> getVariants() {
        return variants;
    }

    @JsonProperty("variants")
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    @JsonProperty("options")
    public List<Option> getOptions() {
        return options;
    }

    @JsonProperty("options")
    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @JsonProperty("images")
    public List<Object> getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(List<Object> images) {
        this.images = images;
    }

    @JsonProperty("image")
    public Image getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(Image image) {
        this.image = image;
    }

}
package com.macbean.tech.shopify.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "browser_ip",
        "accept_language",
        "user_agent",
        "session_hash",
        "browser_width",
        "browser_height"
})
public class ClientDetails {

    @JsonProperty("browser_ip")
    private String browserIp;
    @JsonProperty("accept_language")
    private String acceptLanguage;
    @JsonProperty("user_agent")
    private String userAgent;
    @JsonProperty("session_hash")
    private Object sessionHash;
    @JsonProperty("browser_width")
    private Long browserWidth;
    @JsonProperty("browser_height")
    private Long browserHeight;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("browser_ip")
    public String getBrowserIp() {
        return browserIp;
    }

    @JsonProperty("browser_ip")
    public void setBrowserIp(String browserIp) {
        this.browserIp = browserIp;
    }

    @JsonProperty("accept_language")
    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    @JsonProperty("accept_language")
    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    @JsonProperty("user_agent")
    public String getUserAgent() {
        return userAgent;
    }

    @JsonProperty("user_agent")
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @JsonProperty("session_hash")
    public Object getSessionHash() {
        return sessionHash;
    }

    @JsonProperty("session_hash")
    public void setSessionHash(Object sessionHash) {
        this.sessionHash = sessionHash;
    }

    @JsonProperty("browser_width")
    public Long getBrowserWidth() {
        return browserWidth;
    }

    @JsonProperty("browser_width")
    public void setBrowserWidth(Long browserWidth) {
        this.browserWidth = browserWidth;
    }

    @JsonProperty("browser_height")
    public Long getBrowserHeight() {
        return browserHeight;
    }

    @JsonProperty("browser_height")
    public void setBrowserHeight(Long browserHeight) {
        this.browserHeight = browserHeight;
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
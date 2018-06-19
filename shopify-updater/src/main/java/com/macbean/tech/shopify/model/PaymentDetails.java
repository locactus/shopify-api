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
        "credit_card_bin",
        "avs_result_code",
        "cvv_result_code",
        "credit_card_number",
        "credit_card_company"
})
public class PaymentDetails {

    @JsonProperty("credit_card_bin")
    private String creditCardBin;
    @JsonProperty("avs_result_code")
    private String avsResultCode;
    @JsonProperty("cvv_result_code")
    private String cvvResultCode;
    @JsonProperty("credit_card_number")
    private String creditCardNumber;
    @JsonProperty("credit_card_company")
    private String creditCardCompany;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("credit_card_bin")
    public String getCreditCardBin() {
        return creditCardBin;
    }

    @JsonProperty("credit_card_bin")
    public void setCreditCardBin(String creditCardBin) {
        this.creditCardBin = creditCardBin;
    }

    @JsonProperty("avs_result_code")
    public String getAvsResultCode() {
        return avsResultCode;
    }

    @JsonProperty("avs_result_code")
    public void setAvsResultCode(String avsResultCode) {
        this.avsResultCode = avsResultCode;
    }

    @JsonProperty("cvv_result_code")
    public String getCvvResultCode() {
        return cvvResultCode;
    }

    @JsonProperty("cvv_result_code")
    public void setCvvResultCode(String cvvResultCode) {
        this.cvvResultCode = cvvResultCode;
    }

    @JsonProperty("credit_card_number")
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    @JsonProperty("credit_card_number")
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    @JsonProperty("credit_card_company")
    public String getCreditCardCompany() {
        return creditCardCompany;
    }

    @JsonProperty("credit_card_company")
    public void setCreditCardCompany(String creditCardCompany) {
        this.creditCardCompany = creditCardCompany;
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
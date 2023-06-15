package com.example.seleniumdemo.others;

import com.example.seleniumdemo.seleneum.model.BodyText;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashish on 27/3/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordCountKpiVO {

    @JsonProperty("url")
    private String url;
    @JsonProperty("status")
    private String status;
    @JsonProperty("finalURL")
    private String finalURL;
    @JsonProperty("title")
    private String metaTitle;
    @JsonProperty("metaDesc")
    private String metaDescription;
    @JsonProperty("metaKeywords")
    private String metaKeywords;
    @JsonProperty("h1")
    private List<String> h1 = new ArrayList<>();
    @JsonProperty("h2")
    private List<String> h2 = new ArrayList<>();
    @JsonProperty("h3")
    private List<String> h3 = new ArrayList<>();
    @JsonProperty("h4")
    private List<String> h4 = new ArrayList<>();
    @JsonProperty("words")
    private String bodyContent;
    private BodyText bodyExtracts;
    @JsonProperty("wordscount")
    private Long wordsCount;

    @JsonProperty("HTML")
    private String HTML;
    @JsonProperty("rawHTML")
    private String rawHTML;
    private Boolean isWebSemanticKpisDataRetrieved = Boolean.FALSE;

    public WordCountKpiVO(String url) {
        this.url = url;
    }

}

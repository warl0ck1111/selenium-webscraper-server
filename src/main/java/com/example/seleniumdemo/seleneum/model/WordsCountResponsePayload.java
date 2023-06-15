package com.example.seleniumdemo.seleneum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordsCountResponsePayload {
    private String url;
    private String status;
    private String finalURL;
    private List<String> redirectChain;
    private String words;
    private Integer wordscount;
    private String metaKeywords;
    private String metaDesc;
    private String title;
    private List<String> h1;
    private List<String> uniqueH1;
    private List<String> h2;
    private List<String> uniqueH2;
    private List<String> h3;
    private List<String> uniqueH3;
    private List<String> h4;
    private List<String> uniqueH4;
    private BodyText bodytext;
    @JsonProperty("HTML")
    private String HTML;
    @JsonProperty("rawHTML")
    private String rawHTML;
    private WebSemanticKpisDataSource webSemanticKpisDataSource;
}

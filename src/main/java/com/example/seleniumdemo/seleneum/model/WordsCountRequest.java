package com.example.seleniumdemo.seleneum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordsCountRequest {
    private String id;
    private String url;
    private Boolean webSecurity = false;
    private Boolean ignoreSslErrors = true;
    private Boolean visibleonly = true;
    private Integer minchars = 250;
}
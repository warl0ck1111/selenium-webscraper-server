package com.example.seleniumdemo.seleneum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BodyText {
    private Set<String> p = new HashSet<>();
}

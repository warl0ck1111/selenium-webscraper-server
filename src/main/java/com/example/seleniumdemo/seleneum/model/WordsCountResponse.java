package com.example.seleniumdemo.seleneum.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
public class WordsCountResponse {
    Map<String, String> meta;
    WordsCountResponsePayload payload;
}

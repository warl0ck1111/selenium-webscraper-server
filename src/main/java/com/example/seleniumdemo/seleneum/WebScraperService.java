package com.example.seleniumdemo.seleneum;

import com.example.seleniumdemo.seleneum.model.WordsCountRequest;
import com.example.seleniumdemo.seleneum.model.WordsCountResponse;

import java.util.concurrent.Future;

/**
 * @author Okala Bashir .O.
 */
public interface WebScraperService {
    public Future<WordsCountResponse> ScrapData(WordsCountRequest wordsCountRequest);
}

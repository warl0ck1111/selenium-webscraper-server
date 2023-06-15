package com.example.seleniumdemo.seleneum;

import com.example.seleniumdemo.seleneum.model.WordsCountRequest;
import com.example.seleniumdemo.seleneum.model.WordsCountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author Okala Bashir .O.
 */

@Slf4j
@RestController
public class WebScrapperController {


    @Autowired
    private WebScraperService webScraperService;

    @PostMapping("/")
    public WordsCountResponse scrapData(@RequestBody WordsCountRequest request) {
        log.info("request/url="+request.getUrl());
        WordsCountResponse wordsCountResponse = null;
        try {
            wordsCountResponse = webScraperService.ScrapData(request).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return wordsCountResponse;
    }
}

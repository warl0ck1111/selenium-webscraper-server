package com.example.seleniumdemo.seleneum;

import com.example.seleniumdemo.seleneum.model.*;
import com.example.seleniumdemo.seleneum.util.WebDriverUtil;
import com.example.seleniumdemo.seleneum.util.XPathBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;

/**
 * @author Okala Bashir .O.
 */

@Slf4j
@Service
public class WebScraperServiceImpl implements WebScraperService {

    public static final int TIMEOUT_IN_SECONDS = 3;

    @Override
    public Future<WordsCountResponse> ScrapData(WordsCountRequest wordsCountRequest) {
        return new AsyncResult<> (scrapHtml(wordsCountRequest));
    }

    private WordsCountResponse scrapHtml(WordsCountRequest wordsCountRequest) {
        log.info("scrapHtml/wordsCountRequest/url=" + wordsCountRequest.getUrl());
        if (Strings.isNotEmpty(wordsCountRequest.getUrl())) {

            WordsCountResponsePayload wordsCountResponsePayload = new WordsCountResponsePayload();
            WebDriver chromeDriver = WebDriverUtil.getChromeDriver(true);
            WebDriverUtil.setImplicitWait(chromeDriver, TIMEOUT_IN_SECONDS);
            try {

                chromeDriver.get(wordsCountRequest.getUrl());
                computeWordsCountResponsePayload(chromeDriver,wordsCountRequest.getUrl(), wordsCountResponsePayload);

                WordsCountResponse wordsCountResponse = new WordsCountResponse();
                wordsCountResponse.setMeta(null);
                wordsCountResponse.setPayload(wordsCountResponsePayload);
                return wordsCountResponse;

            } catch (Exception e) {
                log.error("scrapHtml/there was an error: " + e.getMessage());
                return null;
            } finally {
                if (chromeDriver != null) {
                    chromeDriver.quit();
                }
            }

        } else {
            return null;
        }

    }

    private void computeWordsCountResponsePayload(WebDriver driver, String url, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            computeBodyText(driver, wordsCountResponsePayload);
            computeHtml(driver, wordsCountResponsePayload);
            computeH1(driver, wordsCountResponsePayload);
            computeH2(driver, wordsCountResponsePayload);
            computeH3(driver, wordsCountResponsePayload);
            computeH4(driver, wordsCountResponsePayload);
            computeHttpStatus(url,wordsCountResponsePayload);
            computeWordCount(driver, wordsCountResponsePayload);
            computeWords(driver,wordsCountResponsePayload);


            wordsCountResponsePayload.setWebSemanticKpisDataSource(WebSemanticKpisDataSource.SELENIUM);
            wordsCountResponsePayload.setTitle(driver.getTitle());
            wordsCountResponsePayload.setUrl(driver.getCurrentUrl());
            wordsCountResponsePayload.setFinalURL(driver.getCurrentUrl());


            LogEntries logs = driver.manage().logs().get("performance");


        }
    }

    private void computeWords(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        String body = driver.findElement(By.tagName("body")).getText();
        wordsCountResponsePayload.setWords(body);
    }

    private void computeWordCount(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        String body = driver.findElement(By.tagName("body")).getText();
        int wordCount = body.trim().split(" ").length;
        wordsCountResponsePayload.setWordscount(wordCount);

    }

    private void computeH4(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            List<String> h4List = new ArrayList<>();
            List<WebElement> h4Elements = driver.findElements(By.xpath(XPathBuilder.byTagName("H4")));
            for (WebElement h4 : h4Elements) {
                h4List.add(h4.getText());
            }
            wordsCountResponsePayload.setH4(h4List);
        } else {
            log.info("computeH1/driver = NULL");
        }
    }

    private void computeH3(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            List<String> h3List = new ArrayList<>();
            List<WebElement> h3Elements = driver.findElements(By.xpath(XPathBuilder.byTagName("H3")));
            for (WebElement h3 : h3Elements) {
                h3List.add(h3.getText());
            }
            wordsCountResponsePayload.setH3(h3List);
        } else {
            log.info("computeH1/driver = NULL");
        }
    }

    private void computeH2(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            List<String> h2List = new ArrayList<>();
            List<WebElement> h2Elements = driver.findElements(By.xpath(XPathBuilder.byTagName("H2")));
            for (WebElement h2 : h2Elements) {
                h2List.add(h2.getText());
            }
            wordsCountResponsePayload.setH2(h2List);
        } else {
            log.info("computeH1/driver = NULL");
        }
    }

    private void computeH1(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            List<String> h1List = new ArrayList<>();
            List<WebElement> h1Elements = driver.findElements(By.xpath(XPathBuilder.byTagName("H1")));
            for (WebElement h1 : h1Elements) {
                h1List.add(h1.getText());
            }
            wordsCountResponsePayload.setH1(h1List);
        } else {
            log.info("computeH1/driver = NULL");
        }
    }

    private void computeHtml(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            String rawHtml = driver.getPageSource();
            wordsCountResponsePayload.setRawHTML(rawHtml);
            wordsCountResponsePayload.setHTML(rawHtml);
            log.info("computeRawHtml/html:"+rawHtml);
        } else {
            log.info("computeRawHtml/driver = NULL");
        }
    }

    private void computeHttpStatus(String url, WordsCountResponsePayload wordsCountResponsePayload){
        String httpStatus = String.valueOf(getHttpStatusCode(url));
        wordsCountResponsePayload.setStatus(httpStatus);
    }

    private void computeBodyText(WebDriver driver, WordsCountResponsePayload wordsCountResponsePayload) {
        if (driver != null && wordsCountResponsePayload != null) {
            WebElement body = driver.findElement(By.xpath(XPathBuilder.byTagName("body")));
            Set<String> paragraph = new HashSet<>();
            computeParagraphSetToBodyText(paragraph, body);

            BodyText bodyText = new BodyText();
            bodyText.setP(paragraph);
            wordsCountResponsePayload.setBodytext(bodyText);


            // Parse the HTML using Jsoup
            Document document = Jsoup.parse(driver.getPageSource());

            // Use Jsoup to extract data from the document
            String title = document.title();
            System.out.println("Page Title: " + title);

        } else {
            log.info("computeBodyText/driver = NULL");
        }

    }

    private void computeParagraphSetToBodyText(Set<String> p, WebElement body) {
        List<WebElement> paragraphs = body.findElements(By.xpath(XPathBuilder.byTagName("p")));
        for (WebElement paragraph : paragraphs) {
            p.add(paragraph.getText());
        }

    }

    private int getHttpStatusCode(String url) {

        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }


}
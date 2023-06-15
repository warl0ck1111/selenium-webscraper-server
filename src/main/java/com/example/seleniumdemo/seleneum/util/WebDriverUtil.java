package com.example.seleniumdemo.seleneum.util;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY;

/**
 * @author Okala Bashir .O.
 */

public class WebDriverUtil {
    private static final String CHROME_DRIVER_PATH = "driver/chromedriver";//https://sites.google.com/chromium.org/driver/downloads?authuser=0

    public static WebDriver getChromeDriver(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        if (isHeadless){
            // Configure ChromeOptions for headless browsing
            options.addArguments("--headless"); // Run in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU acceleration
            options.addArguments("--enable-javascript"); // enable javaScript
                   }
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        return new ChromeDriver(options);
    }


    public static void setImplicitWait(WebDriver driver, long timeoutInSeconds) {
        if (driver !=null) {
            driver.manage().timeouts().implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
        }else {
            throw new WebDriverException("no driver available");
        }
    }



}


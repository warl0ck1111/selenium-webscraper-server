package com.example.seleniumdemo;

import com.example.seleniumdemo.others.JsoupSemanticKpisServiceImpl;
import com.example.seleniumdemo.seleneum.util.WebDriverUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.http.HttpStatusCode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;


@Slf4j
public class MyTest {
    public static void main(String[] args) {
        String url = "https://www.gammvert.fr/2-1360-outils-de-jardinage/2-3348-croissance-protection-des-vegetaux/3-3728-films-de-croissance-et-voiles-dhivernage/p-46392-cordeau-de-jardin-nylon-3mm-25m-nortene";
        test(url);
        String httpStatus = String.valueOf(HttpStatusCode.valueOf(getHttpStatus(url)));
        System.out.println("get http status code:"+ httpStatus);

        JsoupSemanticKpisServiceImpl jsoupSemanticKpisService = new JsoupSemanticKpisServiceImpl();
        jsoupSemanticKpisService.getFutureSemanticKpis(url);
    }

    public static void test(String url) {
        WebDriver driver = WebDriverUtil.getChromeDriver(true);

        try{
            ChromeOptions options = new ChromeOptions();
            DesiredCapabilities cap = new DesiredCapabilities("chrome","144", Platform.MAC);
            cap.setCapability(ChromeOptions.CAPABILITY, options);

            // set performance logger
            // this sends Network.enable to chromedriver
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            cap.setCapability(ChromeOptions.CAPABILITY, logPrefs);

            driver = new ChromeDriver();

            // navigate to the page
            System.out.println("Navigate to " + url);
            driver.navigate().to(url);

            // and capture the last recorded url (it may be a redirect, or the
            // original url)
            String currentURL = driver.getCurrentUrl();

            // then ask for all the performance logs from this request
            // one of them will contain the Network.responseReceived method
            // and we shall find the "last recorded url" response
            Set<String> logs = driver.manage().logs().getAvailableLogTypes();
            System.out.println("list of available logs");
            logs.forEach(x->x.toString());
//            log.info();

            int status = -1;

            System.out.println("\nList of log entries:\n");

            for (Iterator<String> it = logs.iterator(); it.hasNext();)
            {
                String entry = it.next();

                try
                {
                    JSONObject json = new JSONObject(entry);

                    System.out.println(json.toString());

                    JSONObject message = json.getJSONObject("message");
                    String method = message.getString("method");

                    if (method != null
                            && "Network.responseReceived".equals(method))
                    {
                        JSONObject params = message.getJSONObject("params");

                        JSONObject response = params.getJSONObject("response");
                        String messageUrl = response.getString("url");

                        if (currentURL.equals(messageUrl))
                        {
                            status = response.getInt("status");

                            System.out.println(
                                    "---------- bingo !!!!!!!!!!!!!! returned response for "
                                            + messageUrl + ": " + status);

                            System.out.println(
                                    "---------- bingo !!!!!!!!!!!!!! headers: "
                                            + response.get("headers"));
                        }
                    }
                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            System.out.println("\nstatus code: " + status);
        } finally
        {
            if (driver != null)
            {
                driver.quit();
            }
        }


    }

    private static int getHttpStatus(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;  // Return a custom value to indicate an error
        }
    }
}

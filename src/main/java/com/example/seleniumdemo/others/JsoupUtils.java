package com.example.seleniumdemo.others;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class JsoupUtils {

    //TODO : see usage

    public Document parse(String html) {
        return Jsoup.parse(html);
    }

    public Element parseBody(String html) {
        return Jsoup.parseBodyFragment(html).body();
    }

    public Connection initConnection(String url, int timeoutInSeconds) throws NoSuchAlgorithmException, KeyManagementException {

        Connection connection = Jsoup.connect(url).followRedirects(true);

        //SSL
        //connection.validateTLSCertificates(false);
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        TrustManager[] trustManager = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certificate, String str) {
                    }

                    public void checkServerTrusted(X509Certificate[] certificate, String str) {
                    }
                }
        };
        context.init(null, trustManager, new SecureRandom());
        connection.sslSocketFactory(context.getSocketFactory());

        //set user agent to Google Chrome
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        connection.header("Accept-Encoding", "gzip");
        connection.header("Cache-Control", "no-cache");
        connection.header("Pragma", "no-cache");
        connection.userAgent("Screaming Frog SEO Spider/12.3");

        //set connect and read timeout in seconds
        connection.timeout(timeoutInSeconds * 1000);
        connection.maxBodySize(100 * 1024 * 1024);

        return connection;

    }


}

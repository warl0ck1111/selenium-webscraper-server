package com.example.seleniumdemo.seleneum.util;

/**
 * @author Okala Bashir .O.
 */

public class XPathBuilder {

    public static String byTagName(String tagName) {
        return "//" + tagName;
    }


    public static String build(String selector, String attribute, String value) {
        return new StringBuilder("//").append(selector).append("[@").append(attribute).append("='").append(value).append("']").toString();
    }

}

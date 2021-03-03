package com.cloudcomputingsystems.ChillBill.service;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Simple implementation of lazy loading for json files. It is not thread safe.
 */
public class JSONFileReader {
    private static JSONArray dateRegexes = null;
    private static JSONArray shopNameRegexes = null;

    public static JSONArray getDateRegexes() {
        if(dateRegexes == null) {
            dateRegexes = readRegexFile("res/dateRegex.json");
        }
        return dateRegexes;
    }

    public static JSONArray getShopNameRegexes() {
        if(shopNameRegexes == null) {
            shopNameRegexes = readRegexFile("res/shopNameRegex.json");
        }
        return shopNameRegexes;
    }

    private static JSONArray readRegexFile(String resourceLocation) {
        JSONParser jsonParser = new JSONParser();
        try {
            File file = ResourceUtils.getFile(resourceLocation);
            FileReader reader = new FileReader(file);
            JSONArray regexArray = (JSONArray) jsonParser.parse(reader);
            return  regexArray;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

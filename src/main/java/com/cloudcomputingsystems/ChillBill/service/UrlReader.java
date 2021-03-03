package com.cloudcomputingsystems.ChillBill.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class UrlReader {

    public static Document readUrl(String URL) {

        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }


}

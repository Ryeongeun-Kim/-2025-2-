package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SymbolSearch {

    public static String searchSymbol(String input) {

        if (containsKorean(input))
            return searchKoreanStock(input);

        if (containsAlphabet(input)) {

            if (isTickerFormat(input))
                return input.toUpperCase();

            return searchForeignStock(input);
        }

        return null;
    }

    //한국주식
    private static String searchKoreanStock(String name) {
        try {
            String url = "https://search.naver.com/search.naver?query=" +
                    java.net.URLEncoder.encode(name + " 주가", "UTF-8");

            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(8000).get();

            Elements links = doc.select("a[href*=/item/main.naver?code=]");
            if (links.isEmpty()) return null;

            String href = links.get(0).attr("href");

            int idx = href.indexOf("code=");
            if (idx == -1) return null;

            String code = href.substring(idx + 5, idx + 11);
            return code + ".KS";

        } catch (Exception e) {
            return null;
        }
    }

    //해외주식
    private static String searchForeignStock(String name) {
        try {
            String url = "https://finance.yahoo.com/lookup?s=" +
                    java.net.URLEncoder.encode(name, "UTF-8");

            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(8000).get();
            Elements quotes = doc.select("a[href^=/quote/]");

            if (quotes.isEmpty()) return null;

            String href = quotes.get(0).attr("href");

            String after = href.split("/quote/")[1];
            String ticker = after.replaceAll("[^A-Za-z0-9]", "");

            return ticker.toUpperCase();

        } catch (Exception e) {
            return null;
        }
    }

    private static boolean containsKorean(String s) {
        for (char c : s.toCharArray())
            if (c >= 0xAC00 && c <= 0xD7A3) return true;
        return false;
    }

    private static boolean containsAlphabet(String s) {
        return s.matches(".*[A-Za-z].*");
    }

    private static boolean isTickerFormat(String s) {
        return s.equals(s.toUpperCase()) && s.matches("[A-Z0-9]{1,6}");
    }
}

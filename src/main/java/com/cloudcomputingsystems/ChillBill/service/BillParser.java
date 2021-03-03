package com.cloudcomputingsystems.ChillBill.service;

import com.cloudcomputingsystems.ChillBill.model.bill.Bill;
import com.cloudcomputingsystems.ChillBill.repository.ProductRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.vision.v1.*;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

import javax.persistence.EntityManager;


@Service
public class BillParser {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private Firestore firestore;
//    @Autowired
//    private static FirebaseAuth firebaseAuth;

    @Autowired
    ProductRepository productRepository;

    private static Resource regexFile;


    public String parse(byte[] image, String userId) {
        String billId = "";

        byte[] arr = new byte[0];
        AnnotateImageResponse dirtyBill = AnnotateImageResponse.newBuilder().build();
        // try {
        //arr = Files.readAllBytes(Paths.get("paragon9.jpg"));
        // } catch (IOException e) {
        //     e.printStackTrace();
        //  }
        arr = image;
        try {
            dirtyBill = VisionService.detectTextTest(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Specyfy language of the recept

        dirtyBill.getFullTextAnnotation().getPagesList().size();

        // In future can be used for detecting other lang, boecouse diffrent countries
        // have difrent bills layouts.
        float[] sumLanguageConfidence = new float[]{0, 0};
        for (TextAnnotation.DetectedLanguage lang : dirtyBill.getFullTextAnnotation().getPages(0).getProperty().getDetectedLanguagesList()) {
            if (lang.getLanguageCode().contains("pl")) {
                sumLanguageConfidence[0] += lang.getConfidence();

            }
            if (lang.getLanguageCode().contains("en")) {
                sumLanguageConfidence[1] += lang.getConfidence();
            }

        }

        int maxAt = 0;
        for (int i = 0; i < sumLanguageConfidence.length; i++) {
            maxAt = sumLanguageConfidence[i] > sumLanguageConfidence[maxAt] ? i : maxAt;
        }

        // Parse language
        ArrayList<String> languageSectionKeyWords = new ArrayList<String>();
        //Bill is in Polish
        if (maxAt == 0) {
            // paragon fiskalny
            languageSectionKeyWords.add("pl");
            languageSectionKeyWords.add("NIP( ?)(((([0-9]{3})((-| )?([0-9]{2})){2}(-| )?([0-9]{3}))|(([0-9]{3})(-| )?([0-9]{3})(-| )?([0-9]{2})(-| )?([0-9]{2})))|(([0-9]{3})(-| )?([0-9]{2})(-| )?([0-9]{3})(-| )?([0-9]{2}))|(([0-9]{2})(-| )?([0-9]{3})(-| )?([0-9]{2})(-| )?([0-9]{3}))|(([0-9]{2})-([0-9]{3})(-| )?([0-9]{3})(-| )?([0-9]{2}))|(([0-9]{2})(-| )?([0-9]{2})(-| )?([0-9]{3})(-| )?([0-9]{3})))");
            //languageSectionKeyWords.add("NIP");
            languageSectionKeyWords.add("PARAGON FISKALNY");
            languageSectionKeyWords.add("SPRZEDAŻ OPODATKOWANA");
            languageSectionKeyWords.add("SUMA PLN");
            // paragon nie fiskalny
        }
        //Bill is in English
        if (maxAt == 1) {
            languageSectionKeyWords.add("I do not know yet what to add here XD.");
        }


        Bill result = extractSections(dirtyBill.getFullTextAnnotation(), languageSectionKeyWords);
        result.categorizeProducts(entityManager);

        billId = writeBill(result, userId);
        return billId;
    }

    public String writeBill(Bill bill, String userId) {
        ApiFuture<DocumentReference> apiFuture = this.firestore.collection("Users")
                .document(userId).collection("Bills").add(bill);
        try {
            return apiFuture.get().getId();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Bill extractSections(TextAnnotation fullTextAnnotation, ArrayList<String> languageSectionKeyWords) {
        String[] result;
        Bill finalBill = null;
        if (languageSectionKeyWords.get(0).equals("pl")) {

            String text = fullTextAnnotation.getText();

            Pattern p = Pattern.compile(languageSectionKeyWords.get(1), Pattern.MULTILINE);
            Matcher matcher = p.matcher(text);
            String dateString = "";
            // we only need firs occurence
            if (matcher.find()) {

                dateString = matcher.replaceFirst("191984EREHTUC78596");
                dateString = dateString.substring(dateString.indexOf("191984EREHTUC78596") + 18, dateString.indexOf(languageSectionKeyWords.get(2)));
            }
            //String billNumber = parseBillNumber(dateString); Future feature
            Date billDate = parseDate(dateString);


            String shopName = parseShopName(fullTextAnnotation);


            float finalSum = parseFinalSum(fullTextAnnotation, languageSectionKeyWords);

            String prePrasedProducts = preParseProducts(fullTextAnnotation, languageSectionKeyWords);

            finalBill = parseProducts(prePrasedProducts, new Bill(shopName, finalSum, billDate), languageSectionKeyWords);


        }

        return finalBill;
    }

    public static Date parseDate(String date) {
        JSONArray jsonArray = JSONFileReader.getDateRegexes();
        for (Object obj : jsonArray) {
            JSONObject entry = (JSONObject) obj;
            String entryRegex = (String) entry.get("regex");
            String entryDatePattern = (String) entry.get("datePattern");
            String sanitizedDate = matchAndSanitizeDate(entryRegex, date);
            if (sanitizedDate != null) {
                return generateDate(entryDatePattern, sanitizedDate);
            }
        }
        return new Date();
    }

    private static String matchAndSanitizeDate(String regex, String date) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(date);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private static Date generateDate(String pattern, String date) {
        Date resultDate;
        try {
            resultDate = new SimpleDateFormat(pattern).parse(date);
            return resultDate;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    private static String parseShopName(TextAnnotation fullTextAnnotation) {

        String text = fullTextAnnotation.getText();
        JSONArray jsonArray = JSONFileReader.getShopNameRegexes();

        for (Object obj : jsonArray) {
            JSONObject entry = (JSONObject) obj;
            JSONArray keyPhrases = (JSONArray) entry.get("keyPhrases");
            for (Object o : keyPhrases) {
                String phrase = (String) o;

                if (Pattern.compile(Pattern.quote(phrase), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(text).find()) {
                    return entry.get("name").toString();
                }
            }
        }
        return text.split("\n", 2)[0];
    }

    private static float parseFinalSum(TextAnnotation fullTextAnnotation, ArrayList<String> LanguageSectionKeywords) {
        String text = fullTextAnnotation.getText();

        text = text.substring(text.indexOf(LanguageSectionKeywords.get(4)) + 8);

        final String regex = "([0-9]+)(,|\\.)([0-9]{2})";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(text);
        float price = 0;
        if (matcher.find()) {
            try {
                price = Float.parseFloat(matcher.group(0).replace(",", "."));
            } catch (NumberFormatException e) {
                // TODO:error handle
            }
        }
        return price;
    }

    private static String preParseProducts(TextAnnotation fullTextAnnotation, ArrayList<String> languageSectionKeywords) {
        String text = fullTextAnnotation.getText();
        text = text.substring(text.indexOf(languageSectionKeywords.get(2)) + 16, text.indexOf(languageSectionKeywords.get(4)));

        final String regex = "(s|\\.)?(p|\\.)?(r|\\.)?(z|\\.)?(e|\\.)?(d|\\.)?(\\.)?(a?(z|ż)?)( ?) o(\\.)?(p|\\.)?o(\\.)?(d|\\.)?(a|\\.)?(t|\\.)?(k|\\.)?(o|\\.)?(w|\\.)?(a|\\.)?(n|\\.)?(a|\\.)?";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            text = text.substring(0, text.indexOf(matcher.group(0)));
        }
        return text;

    }

    private static Bill parseProducts(String products, Bill emptyBill, ArrayList<String> languageSelectionKeywords) {

        StringBuffer name = new StringBuffer();
        StringBuffer quantityTimesPrice = new StringBuffer();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> qTPs = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();

        String[] results = products.split("\n");

        for (int i = 0; i < results.length; i++) {
            //Find names
            final String regex = "\\b[^\\d\\W]+\\b";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.UNICODE_CHARACTER_CLASS);
            final Matcher matcher = pattern.matcher(results[i]);
            name = new StringBuffer();
            while (matcher.find()) {
                name.append(matcher.group(0) + " ");
            }
            names.add(name.toString());


            //Find quantity and price per quantity
            final String regex2 = "(([0-9]{1,}((\\,|\\.) ?)[0-9]{1,})|(\\d))( ?(\\*|x|X) ?)(([0-9]{1,}((\\,|\\.) ?)[0-9]{1,})|(\\d))";
            final Pattern pattern2 = Pattern.compile(regex2, Pattern.MULTILINE);
            final Matcher matcher2 = pattern2.matcher(results[i]);
            quantityTimesPrice = new StringBuffer();
            while (matcher2.find()) {
                quantityTimesPrice.append(matcher2.group(0));
            }
            qTPs.add(quantityTimesPrice.toString());


        }


        String currentName = "";
        String currentQTPs = "";
        int switchedCurrentName = 0;
        int switchedCurrentQTPs = 0;


        //Clean mess that is single letters in array
        for (int i = 0; i < names.size(); i++) {

            if (names.get(i).length() <= 3) {
                String temp = names.get(i).replaceAll(" ?[A-Z] ?", "");
                names.set(i, temp);
            }

        }

        int start = 0;
        for (int i = 0; i < names.size(); i++) {
            if (!(names.get(i).equals("") || names.get(i).equals(" ") || Pattern.compile("([Rr.])([Aa.])([Bb.])([Aa.])([Tt.])").matcher(names.get(i)).find())) {
                currentName = names.get(i);
                //System.out.println("Index for names is: " + i + "  currentName is:  " + currentName);
                for (int j = start; j < qTPs.size(); j++) {
                    if (!(qTPs.get(j).equals("") || qTPs.get(j).equals(" "))) {
                        currentQTPs = qTPs.get(j);
                        //System.out.println("Index qtps is: " + j + "  currentQtps is:  " + currentQTPs);
                        j++; // because j must be incremented before loop is break. In other way it gat stuck on number one
                        start = j;
                        try {
                            com.cloudcomputingsystems.ChillBill.model.bill.Product product = new com.cloudcomputingsystems.ChillBill.model.bill.Product(currentName, currentQTPs);
                            emptyBill.addProduct(product);
                        } catch (Exception e) {
                            //TODO::handle this
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }
        }

        return emptyBill;
    }
}
package com.cloudcomputingsystems.ChillBill.controller;

import com.cloudcomputingsystems.ChillBill.service.BillParser;
import com.cloudcomputingsystems.ChillBill.service.CloudStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    @Autowired
    BillParser billParser;
    @Autowired
    CloudStorage cloudStorage;

    @GetMapping("/parseBill")
    public String parseBill(@RequestParam String userImage) throws IOException {
        String userId = userImage.split("/")[0];
        byte[] billImg;
        try {
            billImg = cloudStorage.readBillImg(userImage);
        } catch (Exception e) {
            return "Couldn't load image.";
        }
        return billParser.parse(billImg, userId);
    }

    @GetMapping("/parseBillTest")
    public String readGcsFile() throws IOException {
        byte[] billImg = cloudStorage.readBillImg("wamsbPnoRGdv6QPPBDa4vN13As42/1643190034");
        return billParser.parse(billImg, "EvAtLwuPnuPIlENrRBhz");
    }

    @GetMapping("/visionTest")
    public String testVision() throws IOException {
        byte[] arr = Files.readAllBytes(Paths.get("res/paragon7.jpg"));

        return billParser.parse(arr, "EvAtLwuPnuPIlENrRBhz");
    }

}

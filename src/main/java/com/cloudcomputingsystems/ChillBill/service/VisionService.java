package com.cloudcomputingsystems.ChillBill.service;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;


@Service
public class VisionService {

    public static AnnotateImageResponse detectTextTest(byte[] imageArr) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.copyFrom(imageArr);

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);


        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return null;
                }
                System.out.println(res);
                String detectedText = res.getTextAnnotations(0).getDescription();
                return res;
            }
        }
        return null;
    }


}

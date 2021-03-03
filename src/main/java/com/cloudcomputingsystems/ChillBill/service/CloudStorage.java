package com.cloudcomputingsystems.ChillBill.service;

import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;

@Service
public class CloudStorage {

    Storage storage = StorageOptions.getDefaultInstance().getService();

    public byte[] readBillImg(String billPath) {
        Blob blob = storage.get("chillbill-297814.appspot.com", billPath);
        byte[] content = blob.getContent();
        return content;
    }
}

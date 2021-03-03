package com.cloudcomputingsystems.ChillBill.model;

import com.cloudcomputingsystems.ChillBill.model.bill.Category;

public class VoteBody {
    String productName;
    String shopName;
    String category;

    public VoteBody(String productName, String shopName, String category ) {
        this.productName = productName;
        this.shopName = shopName;
        this.category = category;
    }

    public VoteBody() { }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

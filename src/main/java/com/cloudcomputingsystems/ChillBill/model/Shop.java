package com.cloudcomputingsystems.ChillBill.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    private String shop_name;

    public Shop(String shop_name) {
        this.shop_name = shop_name;
    }

    public Shop() {

    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    @Id
    public String getShop_name() {
        return shop_name;
    }
}

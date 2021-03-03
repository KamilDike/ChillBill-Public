package com.cloudcomputingsystems.ChillBill.model;

import javax.persistence.*;

@Entity
@IdClass(CompositeKeyProduct.class)
@Table(name = "products")
public class Product {

    @Id
    private String product_name;
    @Id
    private String shop_name;

    private int votes_id;

    public Product(String product_name, String shop_name, int votes_id) {
        this.product_name = product_name;
        this.shop_name = shop_name;
        this.votes_id = votes_id;
    }

    public Product() { }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public int getVotes_id() {
        return votes_id;
    }

    public void setVotes_id(int votes_id) {
        this.votes_id = votes_id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_name='" + product_name + '\'' +
                ", shop_name='" + shop_name + '\'' +
                ", votes_id=" + votes_id +
                '}';
    }
}

package com.cloudcomputingsystems.ChillBill.model;

import java.io.Serializable;
import java.util.Objects;

public class CompositeKeyProduct implements Serializable {
    private String product_name;
    private String shop_name;

    public CompositeKeyProduct(String product_name, String shop_name) {
        this.product_name = product_name;
        this.shop_name = shop_name;
    }

    public CompositeKeyProduct() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeKeyProduct)) return false;
        CompositeKeyProduct that = (CompositeKeyProduct) o;
        return product_name.equals(that.product_name) && shop_name.equals(that.shop_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product_name, shop_name);
    }
}

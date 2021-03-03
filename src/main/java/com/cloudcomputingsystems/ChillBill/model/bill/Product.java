package com.cloudcomputingsystems.ChillBill.model.bill;

import java.io.Serializable;
import java.text.NumberFormat;

public class Product implements Serializable {

    private String name;
    private float pricePerName;
    private Category category;
    private float quantity;
    private float discount;


    public Product (String name,String quantityTimesDiscount) throws Exception {
        this.name = name;

        final String regex = "x ?|X ?|\\* ?";
        String[] qnt = quantityTimesDiscount.split(regex);

        // Clean eventual spaces in prices
        qnt[0] = qnt[0].replace(" ","");
        qnt[1] = qnt[1].replace(" ","");

        // Parser works better when have only , as decimal delimieter
        qnt[0] = qnt[0].replace(",",".");
        qnt[1] = qnt[1].replace(",",".");

        //Check if price was detect properly
        if(qnt.length==2){
            quantity = NumberFormat.getInstance().parse(qnt[0]).floatValue();
            pricePerName = NumberFormat.getInstance().parse(qnt[1]).floatValue();

            discount=0;
        }else {
            throw new Exception("Wrong number of args");
        }
    }

    public Product() { }

    public float getPrice(){
        return quantity*pricePerName;
    }
    public Product(String name, float price, Category category, float quantity, float discount) {
        this.name = name;
        this.pricePerName = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getDiscount() {
        return discount;
    }

    public String getName() {
        return name;
    }

    public float getPricePerName() {
        return pricePerName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategoryName(String category) {
        category = category.toLowerCase();
        if (category.equals("blue")) {
            this.category = Category.BLUE;
        }
        else if (category.equals("purple")) {
            this.category = Category.PURPLE;
        }
        else if (category.equals("orange")) {
            this.category = Category.ORANGE;
        }
        else if (category.equals("yellow")) {
            this.category = Category.YELLOW;
        }
        if (category.equals("green")) {
            this.category = Category.GREEN;
        }
    }
}

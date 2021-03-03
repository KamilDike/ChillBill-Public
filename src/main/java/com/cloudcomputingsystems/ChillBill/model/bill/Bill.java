package com.cloudcomputingsystems.ChillBill.model.bill;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bill implements Serializable {

    private String shopName;
    private float totalAmount;
    private Date date;
    private ArrayList<Double> categoryPercentage;
    private String savingsJar;
    private ArrayList<Product> productList;
    private boolean totalAmountMatchesSumProductPrices;

    public Bill(String shopName, float totalAmount, Date date) {
        this.shopName = shopName;
        this.totalAmount = totalAmount;
        this.date = date;
        categoryPercentage = new ArrayList<>() {
            {
                add(20.0); //purple
                add(20.0); //yellow
                add(20.0); //green
                add(20.0); //orange
                add(20.0); //blue
            }
        };
        productList = new ArrayList<Product>();
        savingsJar = "##noJar";
        totalAmountMatchesSumProductPrices = false;

    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("Shop name:  " + shopName+ "\n");
        result.append("Date:  " + date.toString() + "\n");
        result.append("Sum:   " + totalAmount + "\n");
        result.append("Products:   " + "\n");
        for(Product product :productList){
            result.append("     " + product.getName() + "        " + product.getPrice() + "\n");
        }
        return  result.toString();
    }

    public void addProduct(Product product) {

        productList.add(product);
        float price = 0;
        for (Product product1 : productList) {
            price += product1.getPricePerName();
        }
        if (price == this.totalAmount) {
            totalAmountMatchesSumProductPrices = true;
        }
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Double> getCategoryPercentage() {
        return categoryPercentage;
    }

    public void setCategoryPercentage(ArrayList<Double> categoryPercentage) {
        this.categoryPercentage = categoryPercentage;
    }

    public String getSavingsJar() {
        return savingsJar;
    }

    public void setSavingsJar(String savingsJar) {
        this.savingsJar = savingsJar;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public boolean isTotalAmountMatchesSumProductPrices() {
        return totalAmountMatchesSumProductPrices;
    }

    public void setTotalAmountMatchesSumProductPrices(boolean totalAmountMatchesSumProductPrices) {
        this.totalAmountMatchesSumProductPrices = totalAmountMatchesSumProductPrices;
    }

    public void categorizeProducts(EntityManager entityManager) {
        ArrayList<Double> categoriesPercentage = new ArrayList<>() {
            {
                add(0.0); //purple
                add(0.0); //yellow
                add(0.0); //green
                add(0.0); //orange
                add(0.0); //blue
            }
        };
        double total = 0.0;

        for (int i = 0; i < productList.size(); i++) {
            String cat = getCategory(productList.get(i).getName(), this.shopName, entityManager);
            float currentPrice = productList.get(i).getPricePerName() * productList.get(i).getQuantity();
            productList.get(i).setCategoryName(cat);
            total += currentPrice;

            cat = cat.toUpperCase();
            switch (cat) {
                case "PURPLE":
                    categoriesPercentage.set(0, currentPrice + categoriesPercentage.get(0));
                    break;
                case "YELLOW":
                    categoriesPercentage.set(1, currentPrice + categoriesPercentage.get(1));
                    break;
                case "GREEN":
                    categoriesPercentage.set(2, currentPrice + categoriesPercentage.get(2));
                    break;
                case "ORANGE":
                    categoriesPercentage.set(3, currentPrice + categoriesPercentage.get(3));
                    break;
                case "BLUE":
                    categoriesPercentage.set(4, currentPrice + categoriesPercentage.get(4));
                    break;
            }
        }

        for (int i = 0; i < 5; i++) {
            categoriesPercentage.set(i, categoriesPercentage.get(i) * 100 / total);
        }

        this.categoryPercentage = categoriesPercentage;
    }

    private String getCategory(String productName, String shopName, EntityManager entityManager) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("public.getCat")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, productName)
                .setParameter(2, shopName);
        query.execute();
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0).toString();
        }
        return "";
    }
}

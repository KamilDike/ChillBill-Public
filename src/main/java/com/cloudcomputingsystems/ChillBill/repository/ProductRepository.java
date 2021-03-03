package com.cloudcomputingsystems.ChillBill.repository;

import com.cloudcomputingsystems.ChillBill.model.CompositeKeyProduct;
import com.cloudcomputingsystems.ChillBill.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, CompositeKeyProduct> { }

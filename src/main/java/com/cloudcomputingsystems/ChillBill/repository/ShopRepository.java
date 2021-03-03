package com.cloudcomputingsystems.ChillBill.repository;

import com.cloudcomputingsystems.ChillBill.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, String> { }

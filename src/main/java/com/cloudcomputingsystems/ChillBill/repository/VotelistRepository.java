package com.cloudcomputingsystems.ChillBill.repository;

import com.cloudcomputingsystems.ChillBill.model.Votelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotelistRepository extends JpaRepository<Votelist, Integer> { }

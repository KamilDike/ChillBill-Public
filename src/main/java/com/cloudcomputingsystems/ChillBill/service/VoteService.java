package com.cloudcomputingsystems.ChillBill.service;

import com.cloudcomputingsystems.ChillBill.model.CompositeKeyProduct;
import com.cloudcomputingsystems.ChillBill.model.Product;
import com.cloudcomputingsystems.ChillBill.model.Shop;
import com.cloudcomputingsystems.ChillBill.model.Votelist;
import com.cloudcomputingsystems.ChillBill.repository.ProductRepository;
import com.cloudcomputingsystems.ChillBill.repository.ShopRepository;
import com.cloudcomputingsystems.ChillBill.repository.VotelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VotelistRepository votelistRepository;
    @Autowired
    private ShopRepository shopRepository;

    public void addVote(String productName, String shopName, String category) {

        CompositeKeyProduct compositeKeyProduct = new CompositeKeyProduct(productName, shopName);
        Optional<Product> byId = productRepository.findById(compositeKeyProduct);
        if (byId.isPresent()) {
            Optional<Votelist> votelist = votelistRepository.findById(byId.get().getVotes_id());
            votelist.get().addVote(category);
            votelistRepository.save(votelist.get());
        } else {
            Optional<Shop> shop = shopRepository.findById(shopName);
            if (shop.isEmpty()) {
                shopRepository.save(new Shop(shopName));
            }
            Votelist savedVotelist = new Votelist(0, 0, 0, 0, 0);
            savedVotelist.addVote(category);
            votelistRepository.save(savedVotelist);
            productRepository.save(new Product(productName, shopName, savedVotelist.getVotes_id()));
        }
    }

    public void removeVote(String productName, String shopName, String category) {
        Optional<Product> product = productRepository.findById(new CompositeKeyProduct(productName, shopName));
        if (product.isPresent()) {
            Optional<Votelist> votelist = votelistRepository.findById(product.get().getVotes_id());
            votelist.get().removeVote(category);
            votelistRepository.save(votelist.get());
        }
    }
}

package com.cloudcomputingsystems.ChillBill.controller;

import com.cloudcomputingsystems.ChillBill.model.VoteBody;
import com.cloudcomputingsystems.ChillBill.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    VoteService voteService;

    @PostMapping("/addVote")
    public void addVote(@RequestBody VoteBody voteBody) {
        voteService.addVote(voteBody.getProductName(), voteBody.getShopName(), voteBody.getCategory());
    }

    @PostMapping("/removeVote")
    public void removeVote(@RequestBody VoteBody voteBody) {
        voteService.removeVote(voteBody.getProductName(), voteBody.getShopName(), voteBody.getCategory());
    }
}

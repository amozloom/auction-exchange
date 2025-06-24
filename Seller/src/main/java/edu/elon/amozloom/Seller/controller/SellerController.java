package edu.elon.amozloom.Seller.controller;

import edu.elon.amozloom.Seller.models.ForSaleItem;
import edu.elon.amozloom.Seller.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"itemforsale"})
public class SellerController {
    @Autowired
    SellerService sellerService;

    public SellerController() {
    }

    @PostMapping
    public ForSaleItem listItem(@RequestBody ForSaleItem item) {
        return this.sellerService.listItemForSale(item);
    }
}

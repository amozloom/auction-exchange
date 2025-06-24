package edu.elon.amozloom.Seller.services;

import edu.elon.amozloom.Seller.models.ForSaleItem;
import edu.elon.amozloom.Seller.processor.AuctionThreadProcessor;
import edu.elon.amozloom.Seller.services.aws.MessageNotificationService;
import edu.elon.amozloom.Seller.services.aws.MessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SellerService {
    @Value("${auctionCompleteArn}")
    private String auctionCompleteArn;
    @Value("${forSaleArn}")
    private String forSaleArn;
    @Autowired
    MessageQueueService messageQueueService;
    @Autowired
    MessageNotificationService messageNotificationService;

    public SellerService() {
    }

    public ForSaleItem listItemForSale(ForSaleItem itemRequest) {
        itemRequest.setAuctionId(UUID.randomUUID().toString());
        String queueName = "auction-" + itemRequest.getAuctionId();
        String queueUrl = this.messageQueueService.createMessageQueue(queueName, 30);
        itemRequest.setBidQueueUrl(queueUrl);
        String notificationId = this.publishItemForSale(itemRequest);
        itemRequest.setNotificationId(notificationId);
        System.out.println("Published Auction Id " + itemRequest.getAuctionId());
        PrintStream var10000 = System.out;
        String var10001 = itemRequest.getProductTitle();
        var10000.println("\t" + var10001 + "-" + itemRequest.getDescription());
        System.out.println("\tin category " + itemRequest.getProductCategory());
        Thread processingThread = new Thread(new AuctionThreadProcessor(itemRequest, this.auctionCompleteArn, this.messageQueueService, this.messageNotificationService));
        processingThread.start();
        return itemRequest;
    }

    private String publishItemForSale(ForSaleItem forSaleItem) {
        Map<String, String> attributes = new HashMap();
        attributes.put("auctionId", forSaleItem.getAuctionId());
        attributes.put("category", forSaleItem.getProductCategory());
        attributes.put("length", Integer.valueOf(forSaleItem.getAuctionLength()).toString());
        attributes.put("queue", forSaleItem.getBidQueueUrl());
        return this.messageNotificationService.publishNotification(this.forSaleArn, forSaleItem.getProductTitle(), forSaleItem.getDescription(), attributes);
    }
}

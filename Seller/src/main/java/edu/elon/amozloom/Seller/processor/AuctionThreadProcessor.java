package edu.elon.amozloom.Seller.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.elon.amozloom.Seller.models.Bid;
import edu.elon.amozloom.Seller.models.ForSaleItem;
import edu.elon.amozloom.Seller.models.WinningBidNotification;
import edu.elon.amozloom.Seller.services.aws.MessageNotificationService;
import edu.elon.amozloom.Seller.services.aws.MessageQueueService;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionThreadProcessor implements Runnable {
    private MessageQueueService messageQueueService;
    private MessageNotificationService messageNotificationService;
    private ForSaleItem forSaleItem;
    private String auctionCompleteArn;

    public AuctionThreadProcessor(ForSaleItem forSaleItem, String auctionCompleteArn, MessageQueueService messageQueueService, MessageNotificationService messageNotificationService) {
        this.forSaleItem = forSaleItem;
        this.auctionCompleteArn = auctionCompleteArn;
        this.messageQueueService = messageQueueService;
        this.messageNotificationService = messageNotificationService;
    }

    public void run() {
        try {
            System.out.println("About to sleep " + this.forSaleItem.getAuctionLength());
            Thread.sleep((long)(this.forSaleItem.getAuctionLength() * 1000));
            System.out.println("Woke up to process auction " + this.forSaleItem.getAuctionId());
            this.processAuction();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void processAuction() {
        List<Message> bidList = this.messageQueueService.readMessages(this.forSaleItem.getBidQueueUrl());
        if (bidList.size() == 0) {
            System.out.println("No bids received for auction " + this.forSaleItem.getAuctionId());
        } else {
            this.processBids(this.forSaleItem, bidList);
        }

        System.out.println("Closing auction " + this.forSaleItem.getAuctionId());
        this.messageQueueService.deleteMessageQueue(this.forSaleItem.getBidQueueUrl());
    }

    public void processBids(ForSaleItem forSaleItem, List<Message> bidList) {
        List<Bid> bidObjectList = this.pullBidsFromQueue(forSaleItem.getBidQueueUrl(), bidList);
        Bid winningBid = this.pickWinningBid(bidObjectList);
        this.informWinningBid(forSaleItem, winningBid);
        this.notifyEveryone(forSaleItem.getAuctionId(), winningBid);
    }

    private List<Bid> pullBidsFromQueue(String queueUrl, List<Message> bidList) {
        List<Bid> bidObjectList = new ArrayList();
        bidList.forEach((message) -> {
            String messageBody = message.body();
            String receiptHandle = message.receiptHandle();
            Bid bid = this.convertToBidObject(messageBody);
            if (bid != null) {
                bidObjectList.add(bid);
                this.messageQueueService.deleteMessageFromQueue(queueUrl, receiptHandle);
            }

        });
        return bidObjectList;
    }

    private Bid pickWinningBid(List<Bid> bidList) {
        Bid winningBid = null;

        for(Bid bid : bidList) {
            if (winningBid == null || bid.getBidAmount() > winningBid.getBidAmount()) {
                winningBid = bid;
            }
        }

        return winningBid;
    }

    private void informWinningBid(ForSaleItem forSaleItem, Bid winningBid) {
        PrintStream var10000 = System.out;
        String var10001 = forSaleItem.getAuctionId();
        var10000.println("The winning bid for " + var10001 + " was " + winningBid.getEmail() + " for " + winningBid.getBidAmount());
        WinningBidNotification winningBidNotification = new WinningBidNotification(forSaleItem.getAuctionId(), winningBid.getBidAmount());
        this.messageQueueService.writeMessageToQueue(winningBid.getAcceptanceQueueURL(), this.convertObjectToJson(winningBidNotification));
    }

    private String notifyEveryone(String auctionId, Bid winningBid) {
        Map<String, String> attributes = new HashMap();
        attributes.put("auctionId", auctionId);
        return this.messageNotificationService.publishNotification(this.auctionCompleteArn, "Auction Complete", "Sold for " + winningBid.getBidAmount(), attributes);
    }

    private Bid convertToBidObject(String bidString) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Bid bid = (Bid)mapper.readValue(bidString, Bid.class);
            return bid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertObjectToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

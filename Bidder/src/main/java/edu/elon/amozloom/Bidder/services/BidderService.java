package edu.elon.amozloom.Bidder.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.elon.amozloom.Bidder.models.Bid;
import edu.elon.amozloom.Bidder.models.BidderInfo;
import edu.elon.amozloom.Bidder.models.ForSaleItem;
import edu.elon.amozloom.Bidder.models.WinningBidNotification;
import edu.elon.amozloom.Bidder.models.notification.NotificationMessage;
import edu.elon.amozloom.Bidder.models.notification.NotificationMessageAttribute;
import edu.elon.amozloom.Bidder.models.policy.SubscriptionQueuePolicy;
import edu.elon.amozloom.Bidder.services.aws.MessageNotificationService;
import edu.elon.amozloom.Bidder.services.aws.MessageQueueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.PrintStream;
import java.util.*;

@Service
@PropertySources({@PropertySource({"classpath:application.properties"}), @PropertySource(
        value = {"file:application.properties"},
        ignoreResourceNotFound = true
)})
public class BidderService {
    @Value("${bidderName}")
    private String bidderName;
    @Value("${forSaleArn}")
    private String forSaleArn;
    @Value("${auctionCompleteArn}")
    private String auctionCompleteArn;
    @Value("${categories}")
    private String filterCategories;
    @Value("${bidderEmail}")
    private String bidderEmail;
    @Autowired
    private MessageQueueService messageQueueService;
    @Autowired
    private MessageNotificationService messageNotificationService;
    private double accountBalance = (double)100.0F;
    private Map<String, Bid> bidMap = new HashMap();
    private Map<String, WinningBidNotification> wonBidMap = new HashMap();
    private BidderInfo cleanupBidderInfo = null;

    public BidderService() {
    }

    public void runBidderProcess() {
        System.out.println("Starting Bidder Process for " + this.bidderName);
        BidderInfo bidderInfo = this.setupBidder();
        System.out.println("Waiting for Bids");

        try {
            while(this.accountBalance > (double)0.0F || this.bidMap.size() > 0) {
                Thread.sleep(20000L);
                this.bidOnItems(bidderInfo);
                if (this.bidMap.size() > 0) {
                    this.checkAcceptedBids(bidderInfo.getAcceptedBidsQueueUrl());
                    this.checkClosedAuctions(bidderInfo.getAuctionCompleteMessageQueueUrl());
                }
            }

            System.out.println("Out of Money. Exiting the process");
            this.cleanupQueues();
        } catch (InterruptedException var3) {
        }

    }

    private BidderInfo setupBidder() {
        String acceptedBidsQueueUrl = this.createBidAcceptanceQueue();
        String forSaleMessageQueueUrl = this.createSubscriptionQueue("subscription_queue");
        Map<String, List> filterMap = this.buildAttributeFilters("category", this.filterCategories);
        String subscriptionId = this.subscribeToTopic(this.forSaleArn, forSaleMessageQueueUrl, filterMap);
        String auctionCompleteMessageQueueUrl = this.createSubscriptionQueue("auction_complete_queue");
        String auctionCompleteSubscriptionId = this.subscribeToTopic(this.auctionCompleteArn, auctionCompleteMessageQueueUrl, (Map)null);
        BidderInfo bidderInfo = new BidderInfo();
        bidderInfo.setAcceptedBidsQueueUrl(acceptedBidsQueueUrl);
        bidderInfo.setForSaleMessageQueueUrl(forSaleMessageQueueUrl);
        bidderInfo.setForSaleSubscriptionId(subscriptionId);
        bidderInfo.setAuctionCompleteMessageQueueUrl(auctionCompleteMessageQueueUrl);
        bidderInfo.setAuctionCompleteSubscriptionId(auctionCompleteSubscriptionId);
        this.cleanupBidderInfo = bidderInfo;
        return bidderInfo;
    }

    private String createBidAcceptanceQueue() {
        String queueName = this.bidderName + "_accepted_bids_queue";
        return this.messageQueueService.createMessageQueue(queueName, 30);
    }

    private String createSubscriptionQueue(String queueSuffice) {
        String queueName = this.bidderName + "_" + queueSuffice;
        return this.messageQueueService.createMessageQueue(queueName, 30);
    }

    private String subscribeToTopic(String topicArn, String messageQueueUrl, Map<String, List> filterMap) {
        String queueArn = this.messageQueueService.getQueueArnFromUrl(messageQueueUrl);
        SubscriptionQueuePolicy policy = new SubscriptionQueuePolicy(topicArn, queueArn);
        this.messageQueueService.addPolicy(messageQueueUrl, policy.getPolicyDocument());
        String subscriptionArn = this.messageNotificationService.createSubscription(topicArn, "sqs", queueArn, (Map)null);
        if (filterMap != null) {
            this.filterTopic(subscriptionArn, filterMap);
        }

        return subscriptionArn;
    }

    private void filterTopic(String subscriptionArn, Map<String, List> filterMap) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String filterString = mapper.writeValueAsString(filterMap);
            this.messageNotificationService.SetSubscriptionAttribute(subscriptionArn, "FilterPolicy", filterString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map<String, List> buildAttributeFilters(String attributeKey, String attributeFilters) {
        Map<String, List> filterMap = new HashMap();
        List<String> attributeFilterList = Arrays.asList(attributeFilters.split(","));
        filterMap.put(attributeKey, attributeFilterList);
        return filterMap;
    }

    private void bidOnItems(BidderInfo bidderInfo) {
        System.out.println("Looking for items to bid on");
        List<Message> messagesToBidOn = this.messageQueueService.readMessages(bidderInfo.getForSaleMessageQueueUrl());
        if (messagesToBidOn.size() == 0) {
            System.out.println("Nothing available to bid on");
        } else {
            messagesToBidOn.forEach((bidMessage) -> {
                this.createBid(bidMessage, bidderInfo.getAcceptedBidsQueueUrl());
                this.messageQueueService.deleteMessageFromQueue(bidderInfo.getForSaleMessageQueueUrl(), bidMessage.receiptHandle());
            });
            PrintStream var10000 = System.out;
            Object[] var10002 = new Object[]{this.accountBalance};
            var10000.println("Account Balance is " + String.format("%.2f", var10002));
        }

    }

    private void createBid(Message bidMessage, String acceptanceQueueUrl) {
        NotificationMessage bidNotification = this.convertToNotificationMessageObject(bidMessage.body());
        ForSaleItem forSaleItem = new ForSaleItem();
        forSaleItem.setAuctionId(((NotificationMessageAttribute)bidNotification.getMessageAttributes().get("auctionId")).getValue());
        forSaleItem.setBidQueueUrl(((NotificationMessageAttribute)bidNotification.getMessageAttributes().get("queue")).getValue());
        forSaleItem.setProductTitle(bidNotification.getSubject());
        forSaleItem.setDescription(bidNotification.getMessage());
        Bid bid = new Bid();
        bid.setEmail(this.bidderEmail);
        bid.setBidAmount(this.generateRandomBidAmount((double)1.0F, this.accountBalance));
        if (this.accountBalance - bid.getBidAmount() > (double)0.0F) {
            this.accountBalance -= bid.getBidAmount();
            bid.setAcceptanceQueueURL(acceptanceQueueUrl);
            if (this.submitBid(forSaleItem.getBidQueueUrl(), bid)) {
                this.bidMap.put(forSaleItem.getAuctionId(), bid);
                PrintStream var10000 = System.out;
                String var10001 = forSaleItem.getAuctionId();
                var10000.println("Submitted bid for Auction Id " + var10001 + " The bid was " + String.format("%.2f", bid.getBidAmount()) + " for " + forSaleItem.getDescription());
            }
        } else {
            PrintStream var6 = System.out;
            String var7 = forSaleItem.getAuctionId();
            var6.println("Out of Money. Cannot submit bid for Auction Id " + var7 + " for " + forSaleItem.getDescription());
        }

    }

    private boolean submitBid(String bidQueueUrl, Bid bid) {
        String messageBody = this.convertObjectToJson(bid);
        if (this.messageQueueService.doesQueueExist(bidQueueUrl)) {
            this.messageQueueService.writeMessageToQueue(bidQueueUrl, messageBody);
            return true;
        } else {
            System.out.println("Queue " + bidQueueUrl + " does not exist");
            this.accountBalance += bid.getBidAmount();
            return false;
        }
    }

    public void checkAcceptedBids(String acceptedBidsQueueUrl) {
        System.out.println("Looking for accepted bids");
        List<Message> messagesAcceptedBids = this.messageQueueService.readMessages(acceptedBidsQueueUrl);
        if (messagesAcceptedBids.size() == 0) {
            System.out.println("No bids have been accepted");
        } else {
            messagesAcceptedBids.forEach((acceptedBidMessage) -> {
                this.processAcceptedBid(acceptedBidMessage);
                this.messageQueueService.deleteMessageFromQueue(acceptedBidsQueueUrl, acceptedBidMessage.receiptHandle());
            });
        }

    }

    private void processAcceptedBid(Message bidMessage) {
        WinningBidNotification winningBidNotification = this.convertToWinningBidNotificationObject(bidMessage.body());
        this.wonBidMap.put(winningBidNotification.getAuctionId(), winningBidNotification);
        PrintStream var10000 = System.out;
        String var10001 = winningBidNotification.getAuctionId();
        var10000.println("I won auction " + var10001 + " with a price of " + winningBidNotification.getAmountOfBid());
    }

    private void checkClosedAuctions(String auctionCompleteMessageUrl) {
        System.out.println("Looking for closed auctions");
        List<Message> messagesClosedAuctions = this.messageQueueService.readMessages(auctionCompleteMessageUrl);
        if (messagesClosedAuctions.size() > 0) {
            messagesClosedAuctions.forEach((closedAuctionMessage) -> {
                this.closeAuction(closedAuctionMessage);
                this.messageQueueService.deleteMessageFromQueue(auctionCompleteMessageUrl, closedAuctionMessage.receiptHandle());
            });
        }

    }

    private void closeAuction(Message closedAuctionMessage) {
        NotificationMessage closedAuctionNotification = this.convertToNotificationMessageObject(closedAuctionMessage.body());
        String auctionId = ((NotificationMessageAttribute)closedAuctionNotification.getMessageAttributes().get("auctionId")).getValue();
        String message = closedAuctionNotification.getMessage();
        if (this.bidMap.containsKey(auctionId)) {
            if (!this.wonBidMap.containsKey(auctionId)) {
                System.out.println("Auction completed for " + auctionId + " " + message);
                this.accountBalance += ((Bid)this.bidMap.get(auctionId)).getBidAmount();
                PrintStream var10000 = System.out;
                Object[] var10002 = new Object[]{this.accountBalance};
                var10000.println("Account Balance is now " + String.format("%.2f", var10002));
            }

            this.bidMap.remove(auctionId);
        }

    }

    private NotificationMessage convertToNotificationMessageObject(String notificationMessageString) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            NotificationMessage notification = (NotificationMessage)mapper.readValue(notificationMessageString, NotificationMessage.class);
            return notification;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private WinningBidNotification convertToWinningBidNotificationObject(String messageString) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            WinningBidNotification winningBidNotification = (WinningBidNotification)mapper.readValue(messageString, WinningBidNotification.class);
            return winningBidNotification;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cleanupQueues() {
        if (this.cleanupBidderInfo != null) {
            this.messageNotificationService.unsubscribe(this.cleanupBidderInfo.getForSaleSubscriptionId());
            this.messageNotificationService.unsubscribe(this.cleanupBidderInfo.getAuctionCompleteSubscriptionId());
            this.messageQueueService.deleteMessageQueue(this.cleanupBidderInfo.getForSaleMessageQueueUrl());
            this.messageQueueService.deleteMessageQueue(this.cleanupBidderInfo.getAuctionCompleteMessageQueueUrl());
            this.messageQueueService.deleteMessageQueue(this.cleanupBidderInfo.getAcceptedBidsQueueUrl());
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

    private double generateRandomBidAmount(double min, double max) {
        Random random = new Random();
        double randomValue = min + (max - min) * random.nextDouble();
        return (double)Math.round(randomValue * (double)100.0F) / (double)100.0F;
    }
}


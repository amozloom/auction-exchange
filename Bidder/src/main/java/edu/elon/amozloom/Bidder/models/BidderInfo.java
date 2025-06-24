package edu.elon.amozloom.Bidder.models;

public class BidderInfo {
    private String acceptedBidsQueueUrl;
    private String forSaleMessageQueueUrl;
    private String forSaleSubscriptionId;
    private String auctionCompleteMessageQueueUrl;
    private String auctionCompleteSubscriptionId;

    public BidderInfo() {
    }

    public BidderInfo(final String acceptedBidsQueueUrl, final String forSaleMessageQueueUrl, final String forSaleSubscriptionId, final String auctionCompleteMessageQueueUrl, final String auctionCompleteSubscriptionId) {
        this.acceptedBidsQueueUrl = acceptedBidsQueueUrl;
        this.forSaleMessageQueueUrl = forSaleMessageQueueUrl;
        this.forSaleSubscriptionId = forSaleSubscriptionId;
        this.auctionCompleteMessageQueueUrl = auctionCompleteMessageQueueUrl;
        this.auctionCompleteSubscriptionId = auctionCompleteSubscriptionId;
    }

    public String getAcceptedBidsQueueUrl() {
        return this.acceptedBidsQueueUrl;
    }

    public String getForSaleMessageQueueUrl() {
        return this.forSaleMessageQueueUrl;
    }

    public String getForSaleSubscriptionId() {
        return this.forSaleSubscriptionId;
    }

    public String getAuctionCompleteMessageQueueUrl() {
        return this.auctionCompleteMessageQueueUrl;
    }

    public String getAuctionCompleteSubscriptionId() {
        return this.auctionCompleteSubscriptionId;
    }

    public void setAcceptedBidsQueueUrl(final String acceptedBidsQueueUrl) {
        this.acceptedBidsQueueUrl = acceptedBidsQueueUrl;
    }

    public void setForSaleMessageQueueUrl(final String forSaleMessageQueueUrl) {
        this.forSaleMessageQueueUrl = forSaleMessageQueueUrl;
    }

    public void setForSaleSubscriptionId(final String forSaleSubscriptionId) {
        this.forSaleSubscriptionId = forSaleSubscriptionId;
    }

    public void setAuctionCompleteMessageQueueUrl(final String auctionCompleteMessageQueueUrl) {
        this.auctionCompleteMessageQueueUrl = auctionCompleteMessageQueueUrl;
    }

    public void setAuctionCompleteSubscriptionId(final String auctionCompleteSubscriptionId) {
        this.auctionCompleteSubscriptionId = auctionCompleteSubscriptionId;
    }
}


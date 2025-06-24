package edu.elon.amozloom.Seller.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForSaleItem {
    private String auctionId;
    private String productTitle;
    private String productCategory;
    private String description;
    private int auctionLength;
    private String bidQueueUrl;
    private String notificationId;

    public ForSaleItem() {
    }

    public String getAuctionId() {
        return this.auctionId;
    }

    public String getProductTitle() {
        return this.productTitle;
    }

    public String getProductCategory() {
        return this.productCategory;
    }

    public String getDescription() {
        return this.description;
    }

    public int getAuctionLength() {
        return this.auctionLength;
    }

    public String getBidQueueUrl() {
        return this.bidQueueUrl;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public void setAuctionId(final String auctionId) {
        this.auctionId = auctionId;
    }

    public void setProductTitle(final String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductCategory(final String productCategory) {
        this.productCategory = productCategory;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setAuctionLength(final int auctionLength) {
        this.auctionLength = auctionLength;
    }

    public void setBidQueueUrl(final String bidQueueUrl) {
        this.bidQueueUrl = bidQueueUrl;
    }

    public void setNotificationId(final String notificationId) {
        this.notificationId = notificationId;
    }
}


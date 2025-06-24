package edu.elon.amozloom.Seller.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WinningBidNotification {
    private String auctionId;
    private double amountOfBid;

    public WinningBidNotification() {
    }

    public WinningBidNotification(final String auctionId, final double amountOfBid) {
        this.auctionId = auctionId;
        this.amountOfBid = amountOfBid;
    }

    public String getAuctionId() {
        return this.auctionId;
    }

    public double getAmountOfBid() {
        return this.amountOfBid;
    }

    public void setAuctionId(final String auctionId) {
        this.auctionId = auctionId;
    }

    public void setAmountOfBid(final double amountOfBid) {
        this.amountOfBid = amountOfBid;
    }
}

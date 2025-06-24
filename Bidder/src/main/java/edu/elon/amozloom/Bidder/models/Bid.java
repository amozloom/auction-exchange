package edu.elon.amozloom.Bidder.models;

public class Bid {
    private String email;
    private double bidAmount;
    private String acceptanceQueueURL;

    public Bid() {
    }

    public String getEmail() {
        return this.email;
    }

    public double getBidAmount() {
        return this.bidAmount;
    }

    public String getAcceptanceQueueURL() {
        return this.acceptanceQueueURL;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setBidAmount(final double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public void setAcceptanceQueueURL(final String acceptanceQueueURL) {
        this.acceptanceQueueURL = acceptanceQueueURL;
    }
}


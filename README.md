# Auction Exchange Platform

This project implements an online sealed-bid auction system using AWS services including SNS and SQS. It was developed as part of the Software Design course (Spring 2025) to demonstrate full-stack development and distributed messaging architecture using Java, Spring Boot, and Docker.

## 📦 Project Structure

This solution includes two components:

- `Seller`: A Spring Boot API that posts auction items and selects winning bids.
- `Bidder`: A Java-based CLI application that subscribes to item notifications, places bids, and listens for results.

---

## 🚀 Features

### Seller
- Accepts POST requests to list an auction item.
- Broadcasts item details via AWS SNS (`for-sale-topic`).
- Creates an auction-specific SQS queue for incoming bids.
- Automatically closes auctions after a set time.
- Selects the highest bid and:
  - Sends a confirmation to the winning bidder's acceptance queue.
  - Publishes an auction completion notice to SNS (`auction-complete-topic`).

### Bidder
- Subscribes to relevant product categories using filtered SNS subscriptions.
- Creates 3 SQS queues:
  - For sale notifications
  - Auction completion notices
  - Accepted bids
- Maintains a wallet starting with $100.
- Auto-generates a random bid (within budget) when an item is posted.
- Adjusts balance based on outcome of bids.
- Deletes all queues on shutdown.

---

## 🧪 Example Auction Flow

1. Seller posts item:
    ```json
    {
      "productTitle": "Sweater",
      "productCategory": "Clothing",
      "description": "Blue v-neck",
      "auctionLength": 60
    }
    ```

2. Bidder receives notification, places bid:
    ```json
    {
      "email": "bidder1@elon.edu",
      "bidAmount": 86.25,
      "acceptanceQueueURL": "https://sqs.us-east-1.amazonaws.com/.../bidder1_accepted_bids_queue"
    }
    ```

3. Seller selects highest bidder, sends:
    - **Winner's queue**:
      ```json
      {
        "auctionId": "abc123",
        "amountOfBid": 86.25
      }
      ```
    - **Auction-complete topic**:
      ```json
      {
        "auctionId": "abc123",
        "message": "Sold for 86.25"
      }
      ```

4. Losing bidder is notified via auction-complete topic and refunded.

---

## ⚙️ Deployment

### Prerequisites
- AWS account with access to SNS and SQS
- Manually create:
  - `for-sale-topic`
  - `auction-complete-topic`
- Java 17+
- Docker & AWS CLI configured

### Build & Run

#### Seller
```bash
docker build -t auction-seller .
docker run -p 8080:8080 auction-seller
```
### Bidder
```
java -jar bidder.jar
```

---

## 🧹 Cleanup
At shutdown, bidder deletes its queues:

- Bidder For Sale Queue
- Bidder Auction Complete Queue
- Bidder Accepted Bids Queue

---

## 👨‍💻 Contributors
- Anthony Mozloom
- Professor: Brendan Haggerty

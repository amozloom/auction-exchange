package edu.elon.amozloom.Bidder.models.policy;

import org.springframework.util.StringUtils;

public class SubscriptionQueuePolicy {
    private String topicArn;
    private String queueArn;
    private String policyDocument = "{\n    \"Version\": \"2012-10-17\",\n    \"Statement\": [\n        {\n            \"Sid\": \"topic-subscription-{topicArn}\",\n            \"Effect\": \"Allow\",\n            \"Principal\": {\n                \"AWS\": \"*\"\n            },\n            \"Action\": \"SQS:SendMessage\",\n            \"Resource\": \"{queueArn}\",\n            \"Condition\": {\n                \"ArnLike\": {\n                    \"aws:SourceArn\": \"{topicArn}\"\n                }\n            }\n        }\n    ]\n}";

    public SubscriptionQueuePolicy(String topicArn, String queueArn) {
        this.topicArn = topicArn;
        this.queueArn = queueArn;
        this.policyDocument = this.buildPolicy();
    }

    private String buildPolicy() {
        String policy = StringUtils.replace(this.policyDocument, "{topicArn}", this.topicArn);
        policy = StringUtils.replace(policy, "{queueArn}", this.queueArn);
        return policy;
    }

    public SubscriptionQueuePolicy() {
    }

    public String getTopicArn() {
        return this.topicArn;
    }

    public String getQueueArn() {
        return this.queueArn;
    }

    public String getPolicyDocument() {
        return this.policyDocument;
    }

    public void setTopicArn(final String topicArn) {
        this.topicArn = topicArn;
    }

    public void setQueueArn(final String queueArn) {
        this.queueArn = queueArn;
    }

    public void setPolicyDocument(final String policyDocument) {
        this.policyDocument = policyDocument;
    }
}


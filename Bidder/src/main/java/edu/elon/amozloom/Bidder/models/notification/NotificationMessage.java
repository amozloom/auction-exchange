package edu.elon.amozloom.Bidder.models.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
public class NotificationMessage {
    @JsonProperty("Type")
    private String type;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("TopicArn")
    private String topicArn;
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("SignatureVersion")
    private String signatureVersion;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("SigningCertURL")
    private String signingCertURL;
    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;
    @JsonProperty("MessageAttributes")
    private Map<String, NotificationMessageAttribute> messageAttributes;

    public NotificationMessage() {
    }

    public String getType() {
        return this.type;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getTopicArn() {
        return this.topicArn;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getSignatureVersion() {
        return this.signatureVersion;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getSigningCertURL() {
        return this.signingCertURL;
    }

    public String getUnsubscribeURL() {
        return this.unsubscribeURL;
    }

    public Map<String, NotificationMessageAttribute> getMessageAttributes() {
        return this.messageAttributes;
    }

    @JsonProperty("Type")
    public void setType(final String type) {
        this.type = type;
    }

    @JsonProperty("MessageId")
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("TopicArn")
    public void setTopicArn(final String topicArn) {
        this.topicArn = topicArn;
    }

    @JsonProperty("Subject")
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    @JsonProperty("Message")
    public void setMessage(final String message) {
        this.message = message;
    }

    @JsonProperty("Timestamp")
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("SignatureVersion")
    public void setSignatureVersion(final String signatureVersion) {
        this.signatureVersion = signatureVersion;
    }

    @JsonProperty("Signature")
    public void setSignature(final String signature) {
        this.signature = signature;
    }

    @JsonProperty("SigningCertURL")
    public void setSigningCertURL(final String signingCertURL) {
        this.signingCertURL = signingCertURL;
    }

    @JsonProperty("UnsubscribeURL")
    public void setUnsubscribeURL(final String unsubscribeURL) {
        this.unsubscribeURL = unsubscribeURL;
    }

    @JsonProperty("MessageAttributes")
    public void setMessageAttributes(final Map<String, NotificationMessageAttribute> messageAttributes) {
        this.messageAttributes = messageAttributes;
    }
}


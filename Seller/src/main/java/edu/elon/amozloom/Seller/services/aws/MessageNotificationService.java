package edu.elon.amozloom.Seller.services.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageNotificationService {
    private SnsClient snsClient;

    @Autowired
    public MessageNotificationService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String publishNotification(String topicARN, String subject, String message, Map<String, String> attributes) {
        PublishRequest publishRequest = (PublishRequest)PublishRequest.builder().topicArn(topicARN).subject(subject).message(message).messageAttributes(this.buildMassageAttributes(attributes)).build();
        PublishResponse publishResponse = this.snsClient.publish(publishRequest);
        return publishResponse.messageId();
    }

    private Map<String, MessageAttributeValue> buildMassageAttributes(Map<String, String> attributeMap) {
        Map<String, MessageAttributeValue> messageAttributeMap = new HashMap();
        attributeMap.forEach((attributeName, attributeValue) -> {
            MessageAttributeValue messageAttributeValue = (MessageAttributeValue)MessageAttributeValue.builder().dataType("String").stringValue(attributeValue).build();
            messageAttributeMap.put(attributeName, messageAttributeValue);
        });
        return messageAttributeMap;
    }
}

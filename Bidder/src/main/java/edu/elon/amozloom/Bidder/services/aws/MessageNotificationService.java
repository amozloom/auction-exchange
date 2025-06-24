package edu.elon.amozloom.Bidder.services.aws;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.HashMap;
import java.util.List;
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

    public String createSubscription(String topicArn, String protocol, String endpoint, Map<String, List> filterMap) {
        SubscribeRequest subscribeRequest = (SubscribeRequest)SubscribeRequest.builder().build();
        if (filterMap != null) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String filterString = objectMapper.writeValueAsString(filterMap);
                Map<String, String> filterMapObject = new HashMap();
                filterMapObject.put("FilterPolicy", filterString);
                subscribeRequest = (SubscribeRequest)SubscribeRequest.builder().topicArn(topicArn).protocol(protocol).endpoint(endpoint).attributes(filterMapObject).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            subscribeRequest = (SubscribeRequest)SubscribeRequest.builder().topicArn(topicArn).protocol(protocol).endpoint(endpoint).build();
        }

        SubscribeResponse subscribeResponse = this.snsClient.subscribe(subscribeRequest);
        return subscribeResponse.subscriptionArn();
    }

    public void SetSubscriptionAttribute(String subscriptionArn, String attributeName, String attributeValue) {
        SetSubscriptionAttributesRequest setSubscriptionAttributesRequest = (SetSubscriptionAttributesRequest)SetSubscriptionAttributesRequest.builder().subscriptionArn(subscriptionArn).attributeName(attributeName).attributeValue(attributeValue).build();
        this.snsClient.setSubscriptionAttributes(setSubscriptionAttributesRequest);
    }

    public void unsubscribe(String subscriptionArn) {
        UnsubscribeRequest unsubscribeRequest = (UnsubscribeRequest)UnsubscribeRequest.builder().subscriptionArn(subscriptionArn).build();
        this.snsClient.unsubscribe(unsubscribeRequest);
    }
}


package edu.elon.amozloom.Bidder.services.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageQueueService {
    private SqsClient sqsClient;

    @Autowired
    public MessageQueueService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String createMessageQueue(String queueName, Integer visibilityTimeout) {
        Map<QueueAttributeName, String> attributesMap = new HashMap();
        attributesMap.put(QueueAttributeName.VISIBILITY_TIMEOUT, visibilityTimeout.toString());
        CreateQueueRequest createQueueRequest = (CreateQueueRequest)CreateQueueRequest.builder().queueName(queueName).attributes(attributesMap).build();
        CreateQueueResponse response = this.sqsClient.createQueue(createQueueRequest);
        return response.queueUrl();
    }

    public void deleteMessageQueue(String queueURL) {
        DeleteQueueRequest deleteQueueRequest = (DeleteQueueRequest)DeleteQueueRequest.builder().queueUrl(queueURL).build();
        this.sqsClient.deleteQueue(deleteQueueRequest);
    }

    public List<Message> readMessages(String queueUrl) {
        List<Message> receivedMessages = new ArrayList();
        ReceiveMessageRequest receiveMessageRequest = (ReceiveMessageRequest)ReceiveMessageRequest.builder().queueUrl(queueUrl).waitTimeSeconds(5).maxNumberOfMessages(10).build();
        boolean stillMessages = true;

        while(stillMessages) {
            ReceiveMessageResponse receiveMessageResponse = this.sqsClient.receiveMessage(receiveMessageRequest);
            if (receiveMessageResponse.hasMessages()) {
                List<Message> newMessages = receiveMessageResponse.messages();
                receivedMessages.addAll(newMessages);
            } else {
                stillMessages = false;
            }
        }

        return receivedMessages;
    }

    public void deleteMessageFromQueue(String queueUrl, String receiptHandle) {
        DeleteMessageRequest deleteMessageRequest = (DeleteMessageRequest)DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(receiptHandle).build();
        this.sqsClient.deleteMessage(deleteMessageRequest);
    }

    public void writeMessageToQueue(String queueUrl, String messageBody) {
        SendMessageRequest sendMessageRequest = (SendMessageRequest)SendMessageRequest.builder().queueUrl(queueUrl).messageBody(messageBody).build();
        this.sqsClient.sendMessage(sendMessageRequest);
    }

    public void addPolicy(String queueUrl, String policy) {
        Map<String, String> attributeMap = new HashMap();
        attributeMap.put("Policy", policy);
        SetQueueAttributesRequest setQueueAttributesRequest = (SetQueueAttributesRequest)SetQueueAttributesRequest.builder().queueUrl(queueUrl).attributesWithStrings(attributeMap).build();
        this.sqsClient.setQueueAttributes(setQueueAttributesRequest);
    }

    public String getQueueArnFromUrl(String queueUrl) {
        GetQueueAttributesRequest getQueueAttributesRequest = (GetQueueAttributesRequest)GetQueueAttributesRequest.builder().queueUrl(queueUrl).attributeNames(new QueueAttributeName[]{QueueAttributeName.QUEUE_ARN}).build();
        GetQueueAttributesResponse getQueueAttributesResponse = this.sqsClient.getQueueAttributes(getQueueAttributesRequest);
        Map<String, String> attributeMap = getQueueAttributesResponse.attributesAsStrings();
        return (String)attributeMap.get(QueueAttributeName.QUEUE_ARN.toString());
    }

    public boolean doesQueueExist(String queueUrl) {
        try {
            GetQueueAttributesRequest request = (GetQueueAttributesRequest)GetQueueAttributesRequest.builder().queueUrl(queueUrl).build();
            this.sqsClient.getQueueAttributes(request);
            return true;
        } catch (QueueDoesNotExistException var3) {
            return false;
        }
    }
}


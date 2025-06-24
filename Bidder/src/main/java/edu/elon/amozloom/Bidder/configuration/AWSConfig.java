package edu.elon.amozloom.Bidder.configuration;

import edu.elon.amozloom.Bidder.services.BidderService;
import edu.elon.amozloom.Bidder.services.aws.MessageNotificationService;
import edu.elon.amozloom.Bidder.services.aws.MessageQueueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource(value = "file:application.properties", ignoreResourceNotFound = true)
})
public class AWSConfig {

    @Value("${aws.region}")
    private String awsRegion;

    public AWSConfig() {
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    BidderService bidderService() {
        return new BidderService();
    }

    @Bean
    MessageQueueService messageQueueService() {
        return new MessageQueueService(this.sqsClient());
    }

    @Bean
    MessageNotificationService messageNotificationService() {
        return new MessageNotificationService(this.snsClient());
    }
}

package edu.elon.amozloom.Bidder;

import edu.elon.amozloom.Bidder.configuration.AWSConfig;
import edu.elon.amozloom.Bidder.services.BidderService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class BidderApplication {
	public BidderApplication() {
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(new Class[]{AWSConfig.class});
		BidderService bidderService = (BidderService)context.getBean(BidderService.class);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (args.length > 0 && args[0].equalsIgnoreCase("cleanup")) {
				bidderService.cleanupQueues();
			}

		}));
		bidderService.runBidderProcess();
		context.close();
	}
}

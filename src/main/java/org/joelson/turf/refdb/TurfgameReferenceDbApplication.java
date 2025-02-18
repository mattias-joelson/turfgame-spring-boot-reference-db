package org.joelson.turf.refdb;

import org.joelson.turf.refdb.service.FeedFileImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TurfgameReferenceDbApplication {

	private static final Logger logger = LoggerFactory.getLogger(TurfgameReferenceDbApplication.class);

	@Autowired
	FeedFileImportService feedFileImportService;

	public static void main(String[] args) {
		SpringApplication.run(TurfgameReferenceDbApplication.class, args);
	}

	@Bean
	public CommandLineRunner argumentHandler(ApplicationContext ignoredContext) {
		return args -> {
			// arguments passed through -Dspring-boot.run.arguments="test1 test2"
			logArray("Program arguments:", "No program arguments.", args);
			feedFileImportService.importFeedFiles(args);
		};
	}

	private void logArray(String hasElements, String noElements, String[] strings) {
		if (strings.length > 0) {
			logger.info(hasElements);
            for (int i = 0; i < strings.length; i += 1) {
				logger.info("  [{}] {}", i, strings[i]);
			}
		} else {
			logger.info(noElements);
		}
	}
}

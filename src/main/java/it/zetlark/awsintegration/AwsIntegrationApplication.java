package it.zetlark.awsintegration;

import it.zetlark.awsintegration.application.config.DocumentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DocumentProperties.class)
public class AwsIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsIntegrationApplication.class, args);
	}

}

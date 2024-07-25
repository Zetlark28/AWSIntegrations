package it.zetlark.awsintegration.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "document")
public class DocumentProperties {
	private String basePath;
	private String awsS3Folder;
}

package fr.benjaminbillet.dataflow;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@ConfigurationProperties("csv-to-json-transformer")
@Configuration
public class CsvToJsonTransformerProperties {
  private JsonTemplateResource template;
}

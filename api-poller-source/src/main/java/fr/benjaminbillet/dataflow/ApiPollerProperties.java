package fr.benjaminbillet.dataflow;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties("api-poller")
@Configuration
@Validated
public class ApiPollerProperties {

  @NotBlank
  private String uri;

  @NotBlank
  private String verb = HttpMethod.GET.toString();
}

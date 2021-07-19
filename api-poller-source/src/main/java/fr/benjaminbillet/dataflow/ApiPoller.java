package fr.benjaminbillet.dataflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.function.Supplier;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApiPoller {

  private final RestTemplate restTemplate;
  private final ApiPollerProperties properties;

  @Bean
  public Supplier<String> sendEvents() {
    return () -> {
      log.info("Polling {}", properties.getUri());
      ResponseEntity<String> response = restTemplate.exchange(properties.getUri(), HttpMethod.resolve(properties.getVerb()), null, String.class, Collections.emptyMap());
      String data = response.getBody();
      log.info("Polled data from {}: {}", properties.getUri(), data);
      return data;
    };
  }
}

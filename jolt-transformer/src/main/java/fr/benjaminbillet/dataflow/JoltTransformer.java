package fr.benjaminbillet.dataflow;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JoltTransformer {

  private final ObjectMapper objectMapper;
  private final JoltTransformerProperties properties;

  @Bean
  public Function<String, String> joltTransform() {
    return input -> {
      Chainr chain = Chainr.fromSpec(JsonUtils.jsonToList(properties.getJoltSpecification().getData()));
      try {
        Object inputObject = objectMapper.readValue(input, Object.class);
        Object outputObject = chain.transform(inputObject);

        String output = objectMapper.writeValueAsString(outputObject);
        log.info("Transformed data from {} to {}", input, output);

        return output;
      } catch (Exception e) {
        log.error("Transformation error {}", input, e);
        throw new RuntimeException(e);
      }
    };
  }

}

package fr.benjaminbillet.dataflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class CsvToJsonTransformer {

  private final ObjectMapper objectMapper;
  private final CsvToJsonTransformerProperties properties;

  @Bean
  public Function<String, String> csvTransform() {
    return input -> {
      String json = properties.getTemplate().getData();
      try {
        String[] items = parseLine(input);
        for (int i = 0; i < items.length; i++) {
          json = json.replace("{{" + i + "}}", items[i]);
        }

        // check that the produced json is valid
        objectMapper.readValue(input, Object.class);

        return json;
      } catch (Exception e) {
        log.error("Transformation error {}", input, e);
        throw new RuntimeException(e);
      }
    };
  }

  private String[] parseLine(String line) throws IOException, CsvException {
    try (CSVReader csvReader = new CSVReader(new StringReader(line))) {
      List<String[]> list = csvReader.readAll();
      if (list.isEmpty()) {
        log.warn("Input CSV is empty");
        return new String[0];
      } else if (list.size() > 1) {
        log.warn("Input CSV contains more than one line: {}", line);
      }
      return list.get(0);
    }
  }
}

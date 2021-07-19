package fr.benjaminbillet.dataflow;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class InMemoryStoreApi {

  private final InMemoryStore inMemoryStore;

  @RequestMapping(value = "/api/store/{key}",
    produces = {"application/json"},
    method = RequestMethod.GET)
  public ResponseEntity<String> getData(@Size(min = 1) @PathVariable("key") String key) {
    Optional<String> result = inMemoryStore.lookup(key);
    if (result.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(result.get());
  }

  @RequestMapping(value = "/api/store/keys",
    produces = {"application/json"},
    method = RequestMethod.GET)
  public ResponseEntity<Set<String>> listKeys() {
    Set<String> keys = inMemoryStore.listKeys();
    return ResponseEntity.ok(keys);
  }

}

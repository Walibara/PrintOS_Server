import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final HealthRepository healthRepo;

    public HealthController(HealthRepository healthRepo) {
        this.healthRepo = healthRepo;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime dbTime = statusRepo.getDatabaseTime();
            response.put("timestamp", dbTime.toString());
            response.put("message", "found");
            return ResponseEntity.ok(response);  //HTTP 200 good :)
        } catch (Exception e) {
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("message", "not found");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response); //HTTP 503 bad:(
        }
    }
}

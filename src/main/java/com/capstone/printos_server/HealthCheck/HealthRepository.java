import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface HealthRepository extends JpaRepository<Job, Long> {
    @Query(value = "SELECT NOW()", nativeQuery = true)
    LocalDateTime getDatabaseTime();
}

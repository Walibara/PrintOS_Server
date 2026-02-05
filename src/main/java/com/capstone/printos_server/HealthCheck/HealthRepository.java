import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface HealthRepository extends JpaRepository<Job, Long> {

    @Query(value = "SELECT NOW()", nativeQuery = true)
    Timestamp getDatabaseTime();
}
//mona
package com.capstone.printos_server.jobs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Used for debugging database time
    @Query(value = "SELECT CURRENT_TIMESTAMP", nativeQuery = true)
    String getDatabaseTimestamp();

    // Find jobs whose heartbeat timed out and are null
    @Query(value = """
    SELECT * FROM jobs
    WHERE status = 'IN_PROGRESS'
    AND (last_heartbeat_at IS NULL OR last_heartbeat_at < :timeout)
    """, nativeQuery = true)
    List<Job> findTimedOutJobs(@Param("timeout") Timestamp timeout);
    List<Job> findByUploadedByUserId(Long uploadedByUserId);// Maria 4/15
}

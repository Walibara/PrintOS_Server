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
    //Query for "My Library"
    @Query(value = """
    SELECT * FROM jobs j1
    WHERE j1.uploaded_by_user_id = :userId
    AND j1.s3_key IS NOT NULL
    AND j1.s3_key != ''
    AND j1.created_at = (
        SELECT MAX(j2.created_at)
        FROM jobs j2
        WHERE j2.original_file = j1.original_file
        AND j2.uploaded_by_user_id = :userId
    )
    ORDER BY j1.created_at DESC
    """, nativeQuery = true)
    List<Job> findDistinctFilesByUserId(@Param("userId") Long userId);
}

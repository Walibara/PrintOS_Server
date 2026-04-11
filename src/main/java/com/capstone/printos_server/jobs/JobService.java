//mona

package com.capstone.printos_server.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Service layer containing the core business logic
 * for job management.
 */

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    /*
     * This method finds jobs whose workers likely crashed
     * and places them back into the queue.
     */

    public void requeueTimedOutJobs() {
        /*
         * Define timeout threshold.
         * If worker hasn't sent heartbeat in 2 minutes,
         * we assume it died.
         */
        //LocalDateTime timeout = LocalDateTime.now().minusMinutes(2);

        /*
         * Query database for jobs that are stuck.
         */
        //List<Job> timedOutJobs = jobRepository.findTimedOutJobs(timeout);
        Timestamp timeout = Timestamp.valueOf(LocalDateTime.now().minusMinutes(5));
        List<Job> timedOutJobs = jobRepository.findTimedOutJobs(timeout);
        for (Job job : timedOutJobs) {

            /*
             * If retry limit not exceeded,
             * requeue the job.
             */
            if (job.getRetryCount() < job.getMaxRetries()) {

                job.setStatus("CREATED");

                job.setRetryCount(job.getRetryCount() + 1);

                /*
                 * Clear worker ownership
                 * so another worker can claim it.
                 */
                job.setCurrentWorkerId(null);

                System.out.println("Requeuing job " + job.getId());

            }
            else {

                /*
                 * Too many retries → mark job failed.
                 */
                job.setStatus("FAILED");

                System.out.println("Job failed after retries: " + job.getId());
            }

            /*
             * Save updates to database
             */
            jobRepository.save(job);
        }
    }
}
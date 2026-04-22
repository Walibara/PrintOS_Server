package com.capstone.printos_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.capstone.printos_server.jobs.JobService;

/*
 * Background scheduler that periodically checks
 * for jobs stuck in PROCESSING state.
 *
 * This implements the "manager timeout detection"
 * shown in your system architecture diagram.
 */

@Component
public class JobScheduler {

    @Autowired
    private JobService jobService;

    /*
     * Runs every 60 seconds.
     *
     * fixedRate = 60000 milliseconds
     */

    @Scheduled(fixedRate = 60000)
    public void checkTimedOutJobs() {

        System.out.println("Scanning for timed-out jobs...");

        /*
         * Call service that performs requeue logic
         */
        jobService.requeueTimedOutJobs();
    }
}
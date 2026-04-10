package com.capstone.printos_server.jobs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/dw/jobs")
public class DigitalWorkerController {
    private final JobRepository repo;

    // Constructor injection grabs repo
    public DigitalWorkerController(JobRepository repo) {
        this.repo = repo;
    }

    // Emma - Heartbeat, return timestamp for a job
    @PutMapping("/{id}/heartbeat")
    public ResponseEntity<String> heartbeat(@PathVariable Long id) {
        System.out.println("Heartbeat reached from digital worker, job id is = " + id);
        String dbTime = repo.getDatabaseTimestamp();
        return ResponseEntity.ok(dbTime);
    }

    // Emma - Job Result API
    @PutMapping("/{id}/result")
    public ResponseEntity<?> jobResults(@PathVariable("id") Long jobId, @RequestBody Map<String, String> digitalWorkerResponseBody) {
        // Possible responses from the digital worker: success, failed, error, timeout
        Optional<Job> jobOption = repo.findById(jobId);
        // findById() returns an Optional/container object
        // Ensure container is not empty, if it is, job is not found
        System.out.println("In the jobResults");
        if (jobOption.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Optional exists, so the job does as well
        Job job = jobOption.get();

        // If status != success, then that means the digital worker died or something
        if ("failed".equals(digitalWorkerResponseBody.get("status"))) {
            job.setStatus("FAILED");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        } else if ("error".equals(digitalWorkerResponseBody.get("status"))) {
            job.setStatus("ERROR");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        } else if ("timeout".equals(digitalWorkerResponseBody.get("status"))) {
            job.setStatus("TIMEOUT");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        }
        System.out.println("boopppp");

        // OTHERWISE if everything looks gucci, then set the job status to be finished.
        job.setStatus("FINISHED");
        job.setLastUpdatedBy("digital-worker");
        repo.save(job);
        System.out.println("boopppp");
        return ResponseEntity.ok().build(); // 200
    }

    // Claim the oldest available jobs for a digital worker
    @PostMapping("/claim")
    public ResponseEntity<?> claimJob(@RequestParam(defaultValue = "1") int count) {
        try {
            System.out.println("Claim request reached from digital worker");

            List<Job> jobs = repo.findAll();
            List<Job> createdJobs = new ArrayList<>();

            for (Job job : jobs) {
                if ("CREATED".equalsIgnoreCase(job.getStatus())) {
                    createdJobs.add(job);
                }
            }

            createdJobs.sort((a, b) -> {
                if (a.getCreatedAt() == null && b.getCreatedAt() == null) {
                    return 0;
                }
                if (a.getCreatedAt() == null) {
                    return 1;
                }
                if (b.getCreatedAt() == null) {
                    return -1;
                }
                return a.getCreatedAt().compareTo(b.getCreatedAt());
            });

            // 204 No Content → no jobs available
            if (createdJobs.isEmpty()) {
                System.out.println("No claimable job found");
                return ResponseEntity.noContent().build();
            }

            int jobsToClaim = Math.min(count, createdJobs.size());
            List<Job> claimedJobs = new ArrayList<>();

            for (int i = 0; i < jobsToClaim; i++) {
                Job claimableJob = createdJobs.get(i);
                claimableJob.setStatus("IN_PROGRESS");
                claimableJob.setLastUpdatedBy("digital-worker");
                claimedJobs.add(claimableJob);
            }

            repo.saveAll(claimedJobs);

            List<Map<String, Object>> response = new ArrayList<>();

            for (Job savedJob : claimedJobs) {
                Map<String, Object> jobResponse = new LinkedHashMap<>();
                jobResponse.put("message", "Job successfully claimed");
                jobResponse.put("jobId", savedJob.getId());
                jobResponse.put("jobType", savedJob.getJobType());
                jobResponse.put("quantity", savedJob.getQuantity());
                jobResponse.put("material", savedJob.getMaterial());
                jobResponse.put("originalFile", savedJob.getOriginalFile());
                jobResponse.put("fileType", savedJob.getFileType());
                jobResponse.put("additionalCustomization", savedJob.getAdditionalCustomization());
                jobResponse.put("additionalComments", savedJob.getAdditionalComments());
                jobResponse.put("cost", savedJob.getCost());
                jobResponse.put("status", savedJob.getStatus());
                jobResponse.put("createdAt", savedJob.getCreatedAt());
                jobResponse.put("uploadedByUserId", savedJob.getUploadedByUserId());
                jobResponse.put("lastUpdatedBy", savedJob.getLastUpdatedBy());

                response.add(jobResponse);
            }

            System.out.println("Jobs successfully claimed, count = " + response.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Server error while claiming job: " + e.getMessage());

            // 500 Internal Server Error
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Server error"));
        }
    }
}
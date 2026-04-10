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

    public DigitalWorkerController(JobRepository repo) {
        this.repo = repo;
    }

    // FIXED HEARTBEAT (IMPORTANT)
    @PutMapping("/{id}/heartbeat")
    public ResponseEntity<String> heartbeat(@PathVariable Long id) {
        System.out.println("Heartbeat reached from digital worker, job id is = " + id);

        Optional<Job> jobOption = repo.findById(id);
        if (jobOption.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Job job = jobOption.get();

        String dbTime = repo.getDatabaseTimestamp();

        // IMPORTANT: update heartbeat timestamp
        job.setLastHeartbeatAt(dbTime);
        job.setLastUpdatedBy("digital-worker");

        repo.save(job);

        return ResponseEntity.ok(dbTime);
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<?> jobResults(@PathVariable("id") Long jobId,
                                        @RequestBody Map<String, String> digitalWorkerResponseBody) {

        Optional<Job> jobOption = repo.findById(jobId);

        System.out.println("In the jobResults");

        if (jobOption.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Job job = jobOption.get();

        if ("failed".equals(digitalWorkerResponseBody.get("status"))) {
            job.setStatus("FAILED");
        } else if ("error".equals(digitalWorkerResponseBody.get("status"))) {
            job.setStatus("ERROR");
        } else if ("timeout".equals(digitalWorkerResponseBody.get("status"))) {
            job.setStatus("TIMEOUT");
        } else {
            job.setStatus("FINISHED");
        }

        job.setLastUpdatedBy("digital-worker");
        repo.save(job);

        return ResponseEntity.ok().build();
    }

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
                if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return a.getCreatedAt().compareTo(b.getCreatedAt());
            });

            if (createdJobs.isEmpty()) {
                System.out.println("No claimable job found");
                return ResponseEntity.noContent().build();
            }

            int jobsToClaim;

            // 🔥 THIS IS THE KEY CHANGE
            if (count <= 0) {
                jobsToClaim = createdJobs.size(); // claim ALL
            } else {
                jobsToClaim = Math.min(count, createdJobs.size());
            }

            List<Job> claimedJobs = new ArrayList<>();

            for (int i = 0; i < jobsToClaim; i++) {
                Job job = createdJobs.get(i);
                job.setStatus("IN_PROGRESS");
                job.setLastUpdatedBy("digital-worker");
                claimedJobs.add(job);
            }

            repo.saveAll(claimedJobs);

            List<Map<String, Object>> response = new ArrayList<>();

            for (Job savedJob : claimedJobs) {
                Map<String, Object> jobResponse = new LinkedHashMap<>();
                jobResponse.put("jobId", savedJob.getId());
                jobResponse.put("status", savedJob.getStatus());
                jobResponse.put("createdAt", savedJob.getCreatedAt());
                response.add(jobResponse);
            }

            System.out.println("Jobs successfully claimed, count = " + response.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Server error while claiming job: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }
}
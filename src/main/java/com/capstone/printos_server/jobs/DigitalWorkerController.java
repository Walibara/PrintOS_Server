package com.capstone.printos_server.jobs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
//    @PutMapping("/{id}/heartbeat")
  //  public ResponseEntity<LocalDateTime> heartbeat(@PathVariable Long id) {
   //     System.out.println("Heartbeat reached from digital worker, job id is = " + id);
    //    String dbTime = repo.getDatabaseTimestamp();
     //   return ResponseEntity.ok(dbTime);
   // }
   //mona
     @PutMapping("/{id}/heartbeat")
     public ResponseEntity<String> heartbeat(@PathVariable Long id) {
          System.out.println("Heartbeat reached from digital worker, job id is = " + id);
          String dbTime = repo.getDatabaseTimestamp();
          return ResponseEntity.ok(dbTime);
     }

    //Emma - Job Result API
    @PutMapping("/{id}/result")
    public ResponseEntity<?> jobResults(@PathVariable("id") Long jobId, @RequestBody Map<String, String> digitalWorkerResponseBody){
        //Possible responses from the digital worker: success, failed, error, timeout
        Optional<Job> jobOption = repo.findById(jobId);
        //findById() returns an Optional/container object
        //Ensure container is not empty, if it is, job is not found
        System.out.println("In the jobResults");
        if (jobOption.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        //Optional exists, so the job does as well
        Job job = jobOption.get();

        //If status != success, then that means the digital worker died or something
        if("failed".equals(digitalWorkerResponseBody.get("status"))){
            job.setStatus("FAILED");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        }else if("error".equals(digitalWorkerResponseBody.get("status"))){
            job.setStatus("ERROR");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        }else if("timeout".equals(digitalWorkerResponseBody.get("status"))){
            job.setStatus("TIMEOUT");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        }
        System.out.println("boopppp");

        //OTHERWISE if everything looks gucci, then set the job status to be finished.
        job.setStatus("FINISHED");
        job.setLastUpdatedBy("digital-worker");
        repo.save(job);
        System.out.println("boopppp");
        return ResponseEntity.ok().build();//200
    }

    // Claim the oldest available job for a digital worker
    @PostMapping("/claim")
    public ResponseEntity<?> claimJob() {
        try {
            System.out.println("Claim request reached from digital worker");

            List<Job> jobs = repo.findAll();
            Job claimableJob = null;

            for (Job job : jobs) {
                if ("CREATED".equalsIgnoreCase(job.getStatus())) {
                    if (claimableJob == null || job.getCreatedAt().before(claimableJob.getCreatedAt())) {
                        claimableJob = job;
                    }
                }
            }

            // 204 No Content → no jobs available
            if (claimableJob == null) {
                System.out.println("No claimable job found");
                return ResponseEntity.noContent().build();
            }

            // 409 Conflict → job exists but is not claimable
            if (!"CREATED".equalsIgnoreCase(claimableJob.getStatus())) {
                System.out.println("Job already claimed, id = " + claimableJob.getId());
                return ResponseEntity.status(409)
                        .body(Map.of(
                                "message", "Job already claimed",
                                "jobId", claimableJob.getId(),
                                "status", claimableJob.getStatus()
                        ));
            }

            claimableJob.setStatus("IN_PROGRESS");
            claimableJob.setLastUpdatedBy("digital-worker");

            Job savedJob = repo.save(claimableJob);

            System.out.println("Job successfully claimed, id = " + savedJob.getId());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Job successfully claimed");
            response.put("jobId", savedJob.getId());
            response.put("jobType", savedJob.getJobType());
            response.put("quantity", savedJob.getQuantity());
            response.put("material", savedJob.getMaterial());
            response.put("originalFile", savedJob.getOriginalFile());
            response.put("fileType", savedJob.getFileType());
            response.put("additionalCustomization", savedJob.getAdditionalCustomization());
            response.put("additionalComments", savedJob.getAdditionalComments());
            response.put("cost", savedJob.getCost());
            response.put("status", savedJob.getStatus());
            response.put("createdAt", savedJob.getCreatedAt());
            response.put("uploadedByUserId", savedJob.getUploadedByUserId());
            response.put("lastUpdatedBy", savedJob.getLastUpdatedBy());

            // 200 OK → job claimed successfully
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Server error while claiming job: " + e.getMessage());

            // 500 Internal Server Error
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Server error"));
        }
    }
}
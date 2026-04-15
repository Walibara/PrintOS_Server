package com.capstone.printos_server.jobs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.Timestamp;

@RestController
@RequestMapping("/dw/jobs")
public class DigitalWorkerController {
    private final JobRepository repo;

    // Constructor injection grabs repo
    public DigitalWorkerController(JobRepository repo) {
        this.repo = repo;
    }

    //Emma - Heartbeat, return timestamp for a job
     @PutMapping("/{id}/heartbeat")
     public ResponseEntity<String> heartbeat(@PathVariable Long id) {

        System.out.println("DIGITAL WORKER PATH (6): Digital worker called the heartbeat endpoint"); 
        Optional<Job> jobOption = repo.findById(id);

        if (jobOption.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        
        Job job = jobOption.get();
        String dbTime = repo.getDatabaseTimestamp();

        System.out.println("DIGITAL WORKER PATH (7): Last heartbeat timestamp updated to: " + dbTime); 
        job.setLastHeartbeatAt(new Timestamp(System.currentTimeMillis()));
        repo.save(job); 
        
        System.out.println("DIGITAL WORKER PATH (8): Database heartbeat timestamp updated successfully"); //Emma
        System.out.println("DIGITAL WORKER PATH (9): Digital worker recieved 200 ok from EC2"); 
        return ResponseEntity.ok(dbTime);
     }

    //Emma - Job Result API
    @PutMapping("/{id}/result")
    public ResponseEntity<?> jobResults(@PathVariable("id") Long jobId, @RequestBody Map<String, String> digitalWorkerResponseBody){
        System.out.println("DIGITAL WORKER PATH (10): Digital worker calls POST /dw/jobs/{jobId}/result endpoint"); 
        //Possible responses from the digital worker: success, failed, error, timeout
        Optional<Job> jobOption = repo.findById(jobId);
        //findById() returns an Optional/container object
        //Ensure container is not empty, if it is, job is not found
        if (jobOption.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        
        Job job = jobOption.get();

        //If status != success, then that means the digital worker died or something
        if("failed".equals(digitalWorkerResponseBody.get("status"))){
            System.out.println("DIGITAL WORKER PATH (11): Updating job result status to: FAILED"); 
            job.setStatus("FAILED");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        }else if("error".equals(digitalWorkerResponseBody.get("status"))){
            System.out.println("DIGITAL WORKER PATH (11): Updating job result status to: ERROR"); 
            job.setStatus("ERROR");
            job.setLastUpdatedBy("digital-worker");
            repo.save(job);
            return ResponseEntity.ok().build();
        }

        //OTHERWISE if everything looks gucci, then set the job status to be finished.
        job.setStatus("FINISHED");
        
        System.out.println("DIGITAL WORKER PATH (11): Updating job result status to: FINISHED"); 
        job.setLastUpdatedBy("digital-worker");
        repo.save(job);

        System.out.println("DIGITAL WORKER PATH (12): Job status saved in the database"); 
        System.out.println("DIGITAL WORKER PATH (13): HAPPY PATH IS CONCLUDED :) !"); 
        return ResponseEntity.ok().build();
    }

    // Malek - Claim the oldest available job for a digital worker
    @PostMapping("/claim")
    public ResponseEntity<?> claimJob() {
        System.out.println(""); 
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("DIGITAL WORKER PATH (1): EventBridge Trigger Worker Execution"); //Also prints out in lambda, this is just here for clarity on ec2 debug logs
        System.out.println("DIGITAL WORKER PATH (2): Claim Job (GET, GET + PUT,...) "); //Emma added this print statement   

        try {
            
            List<Job> jobs = repo.findAll();
            Job claimableJob = null;

            for (Job job : jobs) {
                if ("CREATED".equalsIgnoreCase(job.getStatus())) {
                    if (claimableJob == null || (job.getCreatedAt() != null && claimableJob.getCreatedAt() != null && job.getCreatedAt().before(claimableJob.getCreatedAt()))) {
                        claimableJob = job;
                    }
                }
            }

            // 204 No Content → no jobs available
            if (claimableJob == null) {
                return ResponseEntity.noContent().build();
            }

            System.out.println("DIGITAL WORKER PATH (3): Return oldest ready job + update 'in progress/running/...' "); //Emma

            // 409 Conflict → job exists but is not claimable
            if (!"CREATED".equalsIgnoreCase(claimableJob.getStatus())) {
                return ResponseEntity.status(409)
                        .body(Map.of(
                                "message", "Job already claimed",
                                "jobId", claimableJob.getId(),
                                "status", claimableJob.getStatus()
                        ));
            }

            claimableJob.setStatus("IN_PROGRESS");
            claimableJob.setLastUpdatedBy("digital-worker");
            System.out.println("DIGITAL WORKER PATH (4): Job Row Updated to In Progress + Job Payload"); //Emma

            Job savedJob = repo.save(claimableJob);

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

            System.out.println("DIGITAL WORKER PATH (5): Digital worker got the job id and updated payload successfully"); //Emma

            // 200 OK → job claimed successfully
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 500 Internal Server Error
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Server error"));
        }
    }
}

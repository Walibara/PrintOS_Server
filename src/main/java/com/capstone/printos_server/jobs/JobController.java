package com.capstone.printos_server.jobs;

import com.capstone.printos_server.errors.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository repo;

    public JobController(JobRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody CreateJobRequest req) {
        // Manual validation (so you still meet “return 400 for invalid requests”)
        if (req == null) throw new ApiException(400, "Request body is required");
        if (req.jobType == null || req.jobType.trim().isEmpty())
            throw new ApiException(400, "jobType is required");
        if (req.quantity == null || req.quantity < 1)
            throw new ApiException(400, "quantity must be at least 1");

        try {
            Job job = new Job();
            job.setJobType(req.jobType.trim());
            job.setQuantity(req.quantity);
            job.setMaterial(req.material);
            job.setOriginalFile(req.originalFile);
            job.setFileType(req.fileType);
            job.setAdditionalCustomization(req.additionalCustomization);
            job.setAdditionalComments(req.additionalComments);
            job.setUploadedByUserId(req.uploadedByUserId);

            job.setStatus("CREATED");
            job.setLastUpdatedBy(req.uploadedByUserId == null ? "user:unknown" : "user:" + req.uploadedByUserId);

            Job saved = repo.save(job);

            return ResponseEntity
                    .created(URI.create("/api/jobs/" + saved.getId()))
                    .body(saved);

        } catch (Exception e) {
            throw new ApiException(500, "Failed to create job: " + e.getMessage());
        }
    }

    //Emma 2/2/26 Get Mapping - Get request http://3.144.187.189:8080/api/jobs
    @GetMapping
    public ResponseEntity<Job> getJob(
            @RequestParam String jobType,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String originalFile,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String additionalCustomization,
            @RequestParam(required = false) String additionalComments,
            @RequestParam(required = false) String uploadedByUserId
    ) {
        
        if (jobType == null || jobType.trim().isEmpty())
            throw new ApiException(400, "jobType is required");
        if (quantity == null || quantity < 1)
            throw new ApiException(400, "quantity must be at least 1");

        Job job = new Job();
        job.setJobType(jobType.trim());
        job.setQuantity(quantity);
        job.setMaterial(material);
        job.setOriginalFile(originalFile);
        job.setFileType(fileType);
        job.setAdditionalCustomization(additionalCustomization);
        job.setAdditionalComments(additionalComments);
        job.setUploadedByUserId(uploadedByUserId);

        job.setStatus("GOTTEN");
        job.setLastUpdatedBy(uploadedByUserId == null ? "user:unknown" : "user:" + uploadedByUserId);

        Job saved = repo.save(job);

        return ResponseEntity.ok(saved);
    }
}

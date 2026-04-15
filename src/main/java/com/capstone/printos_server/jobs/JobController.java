package com.capstone.printos_server.jobs;

import com.capstone.printos_server.errors.ApiException;
import com.capstone.printos_server.users.User;
import com.capstone.printos_server.users.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt; 
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import java.net.URI;
import java.util.List;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository repo;
    private final UserRepository userRepository;
     
    public JobController(JobRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @GetMapping("/userId")
    public ResponseEntity<User> getUser(@PathVariable Long userId){
        List<User> users = userRepository.findAllById(userId);
        return ResponseEntity.ok(users);  
    }

    //Maria
    @PostMapping
    public ResponseEntity<Job> createJob(
            @RequestBody CreateJobRequest req,
            @AuthenticationPrincipal Jwt jwt
    ) {

        // Manual validation (so you still meet “return 400 for invalid requests”)
        if (req == null) throw new ApiException(400, "Request body is required");
        if (req.jobType == null || req.jobType.trim().isEmpty())
            throw new ApiException(400, "jobType is required");
        if (req.quantity == null || req.quantity < 1)
            throw new ApiException(400, "quantity must be at least 1");
        if (req.originalFile == null || req.originalFile.trim().isEmpty()) 
            throw new ApiException(400, "originalFile is required");
        if (jwt == null) 
            throw new ApiException(401, "User authentication is required");

        try {
            String cognitoSub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email"); 

            if (cognitoSub == null || cognitoSub.trim().isEmpty()) { 
                throw new ApiException(401, "Invalid token: missing user sub");
            }

            User user = userRepository.findByCognitoSub(cognitoSub) 
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setCognitoSub(cognitoSub);
                        newUser.setEmail(email);

                        String name = jwt.getClaimAsString("name");
                        if (name == null || name.trim().isEmpty()){
                            name = jwt.getClaimAsString("cognito:username");
                        }
                        if (name == null || name.trim().isEmpty()) {
                            name = email != null ? email.split("@")[0] : "Unknown";
                        }
                        newUser.setName(name);
                        
                        return userRepository.save(newUser);
                    });

        
            Job job = new Job();
            job.setJobType(req.jobType.trim());
            job.setQuantity(req.quantity);
            job.setMaterial(req.material);
            job.setOriginalFile(req.originalFile);
            job.setFileType(req.fileType);
            job.setAdditionalComments(req.additionalComments);
            job.setUploadedByUserId(user.getId()); 
            job.setCreatedAt(new Timestamp(System.currentTimeMillis()));//Emma added this line 4/11

            job.setStatus("CREATED");
            job.setLastUpdatedBy("user:" + user.getId());
            

            Job saved = repo.save(job);

            return ResponseEntity
                    .created(URI.create("/api/jobs/" + saved.getId()))
                    .body(saved);
            
        } catch (ApiException e) {
            throw e;

        } catch (Exception e) {
            throw new ApiException(500, "Failed to create job: " + e.getMessage());
        }
    }

    //Emma 2/2/26 Get Mapping - Get request http://3.144.187.189:8080/api/jobs
    @GetMapping
    public ResponseEntity<List<Job>> getJobs() {
        List<Job> jobs = repo.findAll();
        return ResponseEntity.ok(jobs);
    }

    // Malek - DELETE API endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {

        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404
        }

        try {
            repo.deleteById(id);
            return ResponseEntity.noContent().build(); // 204

        } catch (Exception e) {
            throw new ApiException(500, "Failed to delete job: " + e.getMessage());
        }
    }
}

package com.capstone.printos_server.jobs;

import com.capstone.printos_server.errors.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.capstone.printos_server.jobs.JobRepository;

import java.net.URI;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/dw/jobs")
public class DigitalWorkerController{
    private final JobRepository repo;

    //Constructor injection grabs repo
    public DigitalWorkerController(JobRepository repo){
        this.repo = repo; 
    }

    //Emma - Heartbeat, return timestamp for a job
    @PutMapping("/{id}/heartbeat")
    public ResponseEntity<LocalDateTime> heartbeat(@PathVariable Long id) {
        //Return db time! 
        LocalDateTime dbTime = repo.getDatabaseTimestamp();
        return ResponseEntity.ok(dbTime);
    }
}

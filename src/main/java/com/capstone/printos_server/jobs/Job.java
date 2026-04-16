package com.capstone.printos_server.jobs;

import jakarta.persistence.*;
import java.sql.Timestamp;

/*
 Job Entity
 Represents a row inside the jobs table
*/

@Entity
@Table(name = "jobs")
public class Job {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_number", unique = true)
    private String jobNumber;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "original_file")
    private String originalFile;

    @Column(name = "s3_key")
    private String s3Key;

    @Column(name = "file_type")
    private String fileType;

    // Worker system fields
    @Column(name = "current_worker_id")
    private String currentWorkerId;

    @Column(name = "last_heartbeat_at")
    private Timestamp lastHeartbeatAt;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "max_retries")
    private int maxRetries;

    private String status;

    // Metadata
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @Column(name = "additional_comments")
    private String additionalComments;

    // -----------------------------------
    // Extra fields used by controllers
    // -----------------------------------

    private Integer quantity;

    private String material;

    private Double cost;

    // -----------------------------------
    // Getters and Setters
    // -----------------------------------

    public Long getId() {
        return id;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
    this.jobNumber = jobNumber;
   }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getS3Key() {
        return s3Key;
    }
 
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getCurrentWorkerId() {
        return currentWorkerId;
    }

    public void setCurrentWorkerId(String currentWorkerId) {
        this.currentWorkerId = currentWorkerId;
    }

    public Timestamp getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(Timestamp lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt; 
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public Long getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(Long uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    // -------- Custom Fields --------

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(String originalFile) {
        this.originalFile = originalFile;
    }


    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}

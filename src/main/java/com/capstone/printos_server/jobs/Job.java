package com.capstone.printos_server.jobs;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="job_type", nullable = false, length = 100)
    private String jobType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 100)
    private String material;

    @Column(name="original_file", length = 500)
    private String originalFile;

    @Column(name="file_type", length = 50)
    private String fileType;

    @Column(name="additional_customization", length = 255)
    private String additionalCustomization;

    @Column(name="additional_comments", length = 255)
    private String additionalComments;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name="created_at", updatable = false)
    private Instant createdAt;

    @Column(name="last_updated_at")
    private Instant lastUpdatedAt;

    @Column(name="uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name="last_updated_by", length = 100)
    private String lastUpdatedBy;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.lastUpdatedAt = now;
        if (this.status == null || this.status.isBlank()) {
            this.status = "CREATED";
        }
    }

    @PreUpdate
    void onUpdate() {
        this.lastUpdatedAt = Instant.now();
    }

    

    // Getters & setters
    public Long getId() { return id; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getOriginalFile() { return originalFile; }
    public void setOriginalFile(String originalFile) { this.originalFile = originalFile; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getAdditionalCustomization() { return additionalCustomization; }
    public void setAdditionalCustomization(String additionalCustomization) { this.additionalCustomization = additionalCustomization; }

    public String getAdditionalComments() { return additionalComments; }
    public void setAdditionalComments(String additionalComments) { this.additionalComments = additionalComments; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }

    public Long getUploadedByUserId() { return uploadedByUserId; }
    public void setUploadedByUserId(Long uploadedByUserId) { this.uploadedByUserId = uploadedByUserId; }

    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
}

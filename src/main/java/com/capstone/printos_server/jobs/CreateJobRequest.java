package com.capstone.printos_server.jobs;

public class CreateJobRequest {
    public String jobType;
    public Integer quantity;

    public String material;
    public String originalFile;
    public String fileType;
    public String additionalCustomization;
    public String additionalComments;
    public Long uploadedByUserId;
}

package com.capstone.printos_server.jobs;

public class CreateJobRequest {
    public String jobType;
    public Integer quantity;

    public String material;
    public String originalFile;
    public String s3Key;  // added this 
    public String fileType;
    public String additionalComments;
}
//removed uploadedByUserId and uploadedByUserId. (Maria)

package com.capstone.printos_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/* this is the main entry point for spring boot server
 * when we start the srever, spring loads this class first and automatically scans the project for:
 * controllers, services, repositories, components
 *
 * the @enablescheduling annotation enables background scheduled tasks. we need this for our requeue logic
 * because the system must periodically check for jobs whose workers crashed or stopped sending heartbeats
 */

@SpringBootApplication
@EnableScheduling //allows spring to run scheduled background jobs
public class PrintosServerApplication {

    public static void main(String[] args) {

        /* this starts the Spring Boot server.
         *
         * spring will:
         * 1. Start the embedded web server (Tomcat)
         * 2. Initialize database connections
         * 3. Register controllers and services
         * 4. start scheduled tasks (like our jobs requeue checker)
         */

        SpringApplication.run(PrintosServerApplication.class, args);
    }
}
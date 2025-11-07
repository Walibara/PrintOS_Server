package com.capstone.printos_server;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
    //Adding more origins - Maria 
    origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "https://main.d22sjrfdf1uqnw.amplifyapp.com",
        "https://dkdavnbhgrmho.cloudfront.net"
    }
)
@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
}

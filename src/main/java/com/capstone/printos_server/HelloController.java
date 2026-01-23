package com.capstone.printos_server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Checking if the pipeline sends the update... 1/23/26 @ 8:41am";
    }
}


package com.example.printos_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.capstone.printos_server.PrintosServerApplication; //Emma - importing in main class for test (otherwise github action failure) 10/22

@SpringBootTest(classes = PrintosServerApplication.class)
class PrintosServerApplicationTests {

    @Test
    void contextLoads() {
    }

}

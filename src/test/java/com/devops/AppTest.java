package com.devops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    public void testMessage() {
        App app = new App();
        assertEquals(
            "Hello from DevOps CI/CD Pipeline!",
            app.getMessage()
        );
    }
}
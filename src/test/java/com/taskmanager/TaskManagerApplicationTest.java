package com.taskmanager;

import org.junit.jupiter.api.Test;

class TaskManagerApplicationTest {

    @Test
    void applicationTest() {
        // Simple placeholder test
        assertTrue(true);
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Test failed");
        }
    }
}

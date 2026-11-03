package com.taskmanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Task Manager API");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("message", "Welcome to Task Manager API");
        response.put("endpoints", Map.of(
            "health", "/health",
            "tasks", "/api/tasks",
            "create_task", "POST /api/tasks",
            "get_task", "GET /api/tasks/{id}",
            "update_task", "PUT /api/tasks/{id}",
            "delete_task", "DELETE /api/tasks/{id}",
            "filter_by_status", "GET /api/tasks/status/{status}",
            "filter_by_priority", "GET /api/tasks/priority/{priority}",
            "h2_console", "/h2-console"
        ));
        return response;
    }
}

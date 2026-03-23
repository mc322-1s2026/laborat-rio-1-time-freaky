package com.nexus.model;
import java.util.List;
import com.nexus.model.TaskStatus;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("E-mail com formato inválido.");
        }
        this.username = username;
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload(List<Task> allTasks) {
        if (allTasks == null) {
            return 0;
        }
        return allTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .filter(task -> this.equals(task.getOwner()))
            .count();
    }
}
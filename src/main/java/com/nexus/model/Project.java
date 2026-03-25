package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private final String name;
    private final List<Task> tasks;
    private final int totalBudget;

    public Project(String name, int totalBudget) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome do projeto não pode ser vazio.");
        }
        if (totalBudget <= 0) {
            throw new IllegalArgumentException("O orçamento não pode ser igual ou inferior a zero.");
        }
        this.name = name;
        this.tasks = new ArrayList<>();
        this.totalBudget = totalBudget;
    }

    public void addTask(Task t) {
        if (t == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula.");
        }

        int sumHours = tasks.stream()
                        .mapToInt(Task::getEstimatedEffort)
                        .sum();

        if (sumHours + t.getEstimatedEffort() > totalBudget) {
            Task.totalValidationErrors++;
            throw new NexusValidationException("O tempo excede o disponível.");
        }

        this.tasks.add(t);

    }

    public String getName() { return name; }
    public int getTotalBudget() { return totalBudget; }
    public List<Task> getTasks() { return List.copyOf(tasks); }

}
package com.nexus.model;

import com.nexus.exception.NexusValidationException;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private final String name;
    private final List<Task> tasks;
    private final int totalBudget;

    public Project(String name, List<Task> tasks, int totalBudget) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome do projeto não pode ser vazio.");
        }
        if (totalBudget <= 0) {
            throw new IllegalArgumentException("O orçamento não pode ser igual ou inferior a zero.");
        }
    }
}

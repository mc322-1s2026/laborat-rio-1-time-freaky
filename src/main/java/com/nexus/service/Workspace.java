package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contêiner principal gerenciando análises em tempo real com a API de Streams.
 */
public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) { 
        tasks.add(task); 
    }
    
    public List<Task> getTasks() { 
        // Retorna visão não modificável garantindo que a lista original não vaze
        return Collections.unmodifiableList(tasks); 
    }

    public Task getTaskById(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    /**
     * Top Performers: Retorna os 3 usuários com mais tarefas em DONE.
     */
    public List<User> getTopPerformers() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE && t.getOwner() != null)
                .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Overloaded Users: Retorna usuários com mais de 10 tarefas IN_PROGRESS.
     */
    public List<User> getOverloadedUsers() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS && t.getOwner() != null)
                .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Global Bottleneck: Identifica qual o status com mais tarefas paradas (exceto DONE).
     */
    public TaskStatus getGlobalBottleneck() {
        return tasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
package com.nexus.model;

import java.time.LocalDate;
import com.nexus.exception.NexusValidationException;

/**
 * Representa uma tarefa no sistema operando como uma máquina de estados.
 */
public class Task {
    // Métricas Globais 
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    // Encapsulamento com 'final' garante que nunca serão modificados após a criação (sem setId)
    private final int id;
    private final LocalDate deadline;
    private final int estimatedEffort;
    
    private String title;
    private TaskStatus status;
    private User owner;

    /* Construtor auxiliar para o log_v1 funcionar */
    public Task(String title, LocalDate deadline) {
        // Chama o construtor de baixo passando 0 como esforço padrão
        this(title, deadline, 0); 
    }

    /* Construtor final */
    public Task(String title, LocalDate deadline, int estimatedEffort) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio.");
        }
        if (deadline == null) {
            throw new IllegalArgumentException("O deadline não pode ser nulo.");
        }
        if (estimatedEffort < 0) {
            throw new IllegalArgumentException("O esforço estimado não pode ser negativo.");
        }
        
        this.id = nextId++;
        this.deadline = deadline;
        this.estimatedEffort = estimatedEffort;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        
        totalTasksCreated++; 
    }

    /**
     * Atribui um responsável (owner) à tarefa.
     */
    public void assignOwner(User user) {
        if (user == null) {
            totalValidationErrors++;
            throw new NexusValidationException("O owner atribuído não pode ser nulo.");
        }
        this.owner = user;
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído correspondente e a tarefa não estiver BLOCKED ou DONE.
     */
    public void moveToInProgress(User user) throws NexusValidationException {
        if (owner == null || !owner.equals(user)) {
            totalValidationErrors++;
            throw new NexusValidationException("A tarefa precisa estar atribuída a este usuário para ir para IN_PROGRESS.");
        }
        if (status == TaskStatus.BLOCKED || status == TaskStatus.DONE) {
            totalValidationErrors++;
            throw new NexusValidationException("A tarefa não pode estar em BLOCKED ou DONE para iniciar.");
        }
        
        if (status != TaskStatus.IN_PROGRESS) {
            status = TaskStatus.IN_PROGRESS;
            activeWorkload++;
        }
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() throws NexusValidationException {
        if (status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Uma tarefa BLOCKED não pode ser movida para DONE.");
        }
        if (status == TaskStatus.IN_PROGRESS) {
            activeWorkload--; // Decrementa a carga se a tarefa estava ativa
        }
        status = TaskStatus.DONE;
    }

    /**
     * Define o bloqueio da tarefa.
     * Regra: Não se pode bloquear uma tarefa já finalizada (DONE).
     */
    public void setBlocked(boolean blocked) throws NexusValidationException {
        if (blocked) {
            if (status == TaskStatus.DONE) {
                totalValidationErrors++;
                throw new NexusValidationException("Uma tarefa já concluída (DONE) não pode ser bloqueada.");
            }
            if (status == TaskStatus.IN_PROGRESS) {
                activeWorkload--; // Alivia carga de trabalho pois a tarefa pausou
            }
            this.status = TaskStatus.BLOCKED;
        } else {
            if (status == TaskStatus.BLOCKED) {
                this.status = TaskStatus.TO_DO;
            }
        }
    }

    // Apenas Getters para preservar a segurança da identidade e dados
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort; }
}
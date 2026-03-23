package com.nexus.model;
import java.util.List;
import java.util.Objects;

public class User {
    private final String username;
    private final String email;

    /**
     * Construtor da classe User.
     * @param username O nome de usuário (não pode ser nulo ou vazio).
     * @param email O e-mail do usuário (deve ter um formato válido).
     * @throws IllegalArgumentException se o username ou email forem inválidos.
     */
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

    /**
     * Retorna o e-mail do usuário.
     * @return email do usuário.
     */
    public String consultEmail() {
        return email;
    }

    /**
     * Retorna o nome de usuário.
     * @return username do usuário.
     */
    public String consultUsername() {
        return username;
    }

    /**
     * Calcula a carga de trabalho atual do usuário baseada em tarefas ativas.
     * @param allTasks A lista global de tarefas do Workspace.
     * @return O número de tarefas que pertencem a este usuário e estão IN_PROGRESS.
     */
    public long calculateWorkload(List<Task> allTasks) {
        if (allTasks == null) {
            return 0;
        }
        return allTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .filter(task -> this.equals(task.getOwner()))
            .count();
    }

    /* Está sendo considerado que usuários com e-mails iguais são o mesmo usuário */
    @Override
    public boolean equals(Object o) {   
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.email);
    }

}
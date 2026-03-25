package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Motor de processamento de comandos em lote (Log Reader).
 */
public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                Project proj = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(proj);
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> {
                                // CREATE_TASK;taskName;deadline;effort;projectName
                                int effort = Integer.parseInt(p[3]);
                                Task t = new Task(p[1], LocalDate.parse(p[2]), effort);
                                
                                workspace.addTask(t);
                                
                                Project proj = workspace.getProjectByName(p[4]);
                                if (proj == null) {
                                    throw new NexusValidationException("Projeto não encontrado para vincular a task: " + p[4]);
                                }
                                proj.addTask(t); 
                                System.out.println("[LOG] Tarefa criada e vinculada ao projeto " + p[4] + ": " + p[1]);
                            }

                            case "ASSIGN_USER" -> {
                                Task t = workspace.getTaskById(Integer.parseInt(p[1]));
                                User u = users.stream().filter(user -> user.consultUsername().equals(p[2])).findFirst().orElse(null);
                                if (t == null || u == null) throw new NexusValidationException("Task ou User não encontrado.");
                                t.assignOwner(u);
                                System.out.println("[LOG] Usuário " + p[2] + " atribuído à task " + p[1]);
                            }
                            case "CHANGE_STATUS" -> {
                                Task t = workspace.getTaskById(Integer.parseInt(p[1]));
                                if (t == null) throw new NexusValidationException("Task não encontrada: " + p[1]);
                                
                                String newStatus = p[2];
                                switch (newStatus) {
                                    case "IN_PROGRESS" -> {
                                        if (t.getOwner() == null) throw new NexusValidationException("A tarefa precisa de um dono antes de iniciar.");
                                        t.moveToInProgress(t.getOwner());
                                    }
                                    case "DONE" -> t.markAsDone();
                                    case "BLOCKED" -> t.setBlocked(true);
                                    case "TO_DO" -> t.setBlocked(false);
                                    default -> throw new NexusValidationException("Status desconhecido: " + newStatus);
                                }
                                System.out.println("[LOG] Status da task " + p[1] + " alterado para " + newStatus);
                            }
                            case "REPORT_STATUS" -> {
                                System.out.println("\n--- RELATÓRIO EXECUTIVO NEXUS ---");
                                System.out.println("Top Performers: " + workspace.getTopPerformers().stream().map(User::consultUsername).toList());
                                System.out.println("Overloaded Users: " + workspace.getOverloadedUsers().stream().map(User::consultUsername).toList());
                                System.out.println("Gargalo Global: " + workspace.getGlobalBottleneck());
                                System.out.println("Erros de Validação Evitados: " + Task.totalValidationErrors);
                                System.out.println("---------------------------------\n");
                            }
                            default -> System.err.println("[WARN] Ação desconhecida ou recurso não habilitado ainda: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[DADOS INVÁLIDOS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}
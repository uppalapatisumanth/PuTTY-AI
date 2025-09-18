package com.puttyai;

import com.puttyai.capture.PuttyMonitorListener;
import com.puttyai.capture.PuttyProcessMonitor;
import com.puttyai.capture.PuttySession;
import com.puttyai.model.AIAnalyzer;

import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the PuTTY AI Assistant (Console Version)
 */
public class PuttyAIAssistant implements PuttyMonitorListener {
    private PuttyProcessMonitor processMonitor;
    private AIAnalyzer aiAnalyzer;
    private boolean running = true;

    public PuttyAIAssistant() {
        processMonitor = new PuttyProcessMonitor();
        processMonitor.addListener(this);
        aiAnalyzer = new AIAnalyzer();
    }

    public void start() {
        System.out.println("Starting PuTTY AI Assistant...");
        processMonitor.startMonitoring();
        
        Scanner scanner = new Scanner(System.in);
        
        while (running) {
            displayMenu();
            String input = scanner.nextLine().trim();
            
            if (input.equals("1")) {
                listPuttySessions();
            } else if (input.equals("2")) {
                askQuestion(scanner);
            } else if (input.equals("3")) {
                analyzeCurrentSessions();
            } else if (input.equals("4")) {
                running = false;
            }
        }
        
        processMonitor.stopMonitoring();
        System.out.println("PuTTY AI Assistant stopped.");
    }
    
    private void displayMenu() {
        System.out.println("\n=== PuTTY AI Assistant ===");
        System.out.println("1. List active PuTTY sessions");
        System.out.println("2. Ask a question about a session");
        System.out.println("3. Analyze current sessions for issues");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void listPuttySessions() {
        List<PuttySession> sessions = processMonitor.getActiveSessions();
        
        if (sessions.isEmpty()) {
            System.out.println("No active PuTTY sessions found.");
            return;
        }
        
        System.out.println("\nActive PuTTY Sessions:");
        for (int i = 0; i < sessions.size(); i++) {
            PuttySession session = sessions.get(i);
            System.out.println((i + 1) + ". " + session.getSessionName() + " (PID: " + session.getProcessId() + ")");
        }
    }
    
    private void askQuestion(Scanner scanner) {
        List<PuttySession> sessions = processMonitor.getActiveSessions();
        
        if (sessions.isEmpty()) {
            System.out.println("No active PuTTY sessions found.");
            return;
        }
        
        listPuttySessions();
        
        System.out.print("Select a session number: ");
        int sessionIndex;
        try {
            sessionIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (sessionIndex < 0 || sessionIndex >= sessions.size()) {
                System.out.println("Invalid session number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        PuttySession selectedSession = sessions.get(sessionIndex);
        
        System.out.print("Enter your question: ");
        String question = scanner.nextLine().trim();
        
        if (question.isEmpty()) {
            System.out.println("Question cannot be empty.");
            return;
        }
        
        String answer = aiAnalyzer.answerQuestion(selectedSession, question);
        System.out.println("\nAI Assistant Answer:");
        System.out.println(answer);
    }
    
    private void analyzeCurrentSessions() {
        List<PuttySession> sessions = processMonitor.getActiveSessions();
        
        if (sessions.isEmpty()) {
            System.out.println("No active PuTTY sessions found.");
            return;
        }
        
        System.out.println("\nAnalyzing all active PuTTY sessions...");
        
        boolean issuesFound = false;
        
        for (PuttySession session : sessions) {
            AIAnalyzer.AnalysisResult result = aiAnalyzer.analyzeSession(session);
            
            if (result.hasIssues()) {
                issuesFound = true;
                System.out.println("\nIssues found in session: " + session.getSessionName());
                
                for (java.util.Map.Entry<String, String> issue : result.getIssues().entrySet()) {
                    System.out.println("Issue: " + issue.getKey());
                    System.out.println("Solution: " + issue.getValue());
                    System.out.println();
                }
            }
        }
        
        if (!issuesFound) {
            System.out.println("No issues found in any active sessions.");
        }
    }
    
    @Override
    public void onSessionStarted(PuttySession session) {
        System.out.println("\nNew PuTTY session detected: " + session.getSessionName() + " (PID: " + session.getProcessId() + ")");
    }
    
    @Override
    public void onSessionEnded(PuttySession session) {
        System.out.println("\nPuTTY session ended: " + session.getSessionName() + " (PID: " + session.getProcessId() + ")");
    }
    
    public static void main(String[] args) {
        new PuttyAIAssistant().start();
    }
}
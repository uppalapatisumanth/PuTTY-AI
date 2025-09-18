package com.puttyai.capture;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Monitors running PuTTY processes and captures their output
 */
public class PuttyProcessMonitor {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<PuttySession> activeSessions = new ArrayList<>();
    private final List<PuttyMonitorListener> listeners = new ArrayList<>();
    
    /**
     * Start monitoring for PuTTY processes
     */
    public void startMonitoring() {
        scheduler.scheduleAtFixedRate(this::checkForPuttyProcesses, 0, 5, TimeUnit.SECONDS);
    }
    
    /**
     * Stop monitoring
     */
    public void stopMonitoring() {
        scheduler.shutdown();
    }
    
    /**
     * Check for running PuTTY processes
     */
    private void checkForPuttyProcesses() {
        try {
            // Use Windows command to find PuTTY processes
            Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq putty.exe\" /FO CSV /NH");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            List<String> processIds = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("\"putty.exe\"")) {
                    // Extract PID from CSV format: "putty.exe","1234",etc...
                    String[] parts = line.split("\",\"");
                    if (parts.length >= 2) {
                        String pid = parts[1];
                        processIds.add(pid);
                        
                        // Check if this is a new session
                        if (!isSessionActive(pid)) {
                            PuttySession session = new PuttySession(pid);
                            activeSessions.add(session);
                            notifySessionStarted(session);
                        }
                    }
                }
            }
            
            // Remove sessions that are no longer active
            List<PuttySession> sessionsToRemove = new ArrayList<>();
            for (PuttySession session : activeSessions) {
                if (!processIds.contains(session.getProcessId())) {
                    sessionsToRemove.add(session);
                    notifySessionEnded(session);
                }
            }
            activeSessions.removeAll(sessionsToRemove);
            
        } catch (Exception e) {
            System.err.println("Error checking for PuTTY processes: " + e.getMessage());
        }
    }
    
    /**
     * Check if a session with the given process ID is already being monitored
     */
    private boolean isSessionActive(String processId) {
        for (PuttySession session : activeSessions) {
            if (session.getProcessId().equals(processId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add a listener for PuTTY session events
     */
    public void addListener(PuttyMonitorListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener
     */
    public void removeListener(PuttyMonitorListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify listeners that a new PuTTY session has started
     */
    private void notifySessionStarted(PuttySession session) {
        for (PuttyMonitorListener listener : listeners) {
            listener.onSessionStarted(session);
        }
    }
    
    /**
     * Notify listeners that a PuTTY session has ended
     */
    private void notifySessionEnded(PuttySession session) {
        for (PuttyMonitorListener listener : listeners) {
            listener.onSessionEnded(session);
        }
    }
    
    /**
     * Get the list of currently active PuTTY sessions
     */
    public List<PuttySession> getActiveSessions() {
        return new ArrayList<>(activeSessions);
    }
}
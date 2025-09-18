package com.puttyai.capture;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a PuTTY session with its captured data
 */
public class PuttySession {
    private final String processId;
    private final LocalDateTime startTime;
    private final List<String> capturedOutput;
    private String sessionName;
    
    public PuttySession(String processId) {
        this.processId = processId;
        this.startTime = LocalDateTime.now();
        this.capturedOutput = new ArrayList<>();
        this.sessionName = "PuTTY Session " + processId;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public List<String> getCapturedOutput() {
        return new ArrayList<>(capturedOutput);
    }
    
    public void addCapturedOutput(String output) {
        capturedOutput.add(output);
    }
    
    public String getSessionName() {
        return sessionName;
    }
    
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
    
    @Override
    public String toString() {
        return sessionName;
    }
}
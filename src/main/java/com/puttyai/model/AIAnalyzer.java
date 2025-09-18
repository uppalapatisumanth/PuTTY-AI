package com.puttyai.model;

import com.puttyai.capture.PuttySession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides AI analysis of PuTTY session data
 */
public class AIAnalyzer {
    private static final Map<String, String> ERROR_SOLUTIONS = new HashMap<>();
    
    static {
        // Common SSH/Network errors and solutions
        ERROR_SOLUTIONS.put("connection refused", "Check if the SSH server is running and that the port is correct and not blocked by a firewall.");
        ERROR_SOLUTIONS.put("connection timed out", "The server may be down or there might be network connectivity issues. Check your network connection and server status.");
        ERROR_SOLUTIONS.put("network error: software caused connection abort", "This usually indicates a network problem or the server closed the connection unexpectedly. Try reconnecting.");
        ERROR_SOLUTIONS.put("access denied", "Your username or password is incorrect. Verify your credentials and try again.");
        ERROR_SOLUTIONS.put("host key verification failed", "The server's host key has changed or is not in your known_hosts file. Use 'ssh-keygen -R hostname' to remove the old key.");
        ERROR_SOLUTIONS.put("permission denied", "You don't have permission to access the requested resource. Check file permissions or user privileges.");
        ERROR_SOLUTIONS.put("no route to host", "There's a network routing problem. Check your network configuration and ensure the host is reachable.");
    }
    
    /**
     * Analyze session data to identify issues and provide solutions
     * 
     * @param session The PuTTY session to analyze
     * @return Analysis results with identified issues and solutions
     */
    public AnalysisResult analyzeSession(PuttySession session) {
        AnalysisResult result = new AnalysisResult();
        List<String> output = session.getCapturedOutput();
        
        // Look for known error patterns
        for (String line : output) {
            for (Map.Entry<String, String> entry : ERROR_SOLUTIONS.entrySet()) {
                if (line.toLowerCase().contains(entry.getKey())) {
                    result.addIssue(line, entry.getValue());
                }
            }
        }
        
        // Look for common command errors
        analyzeCommandErrors(output, result);
        
        return result;
    }
    
    /**
     * Analyze a specific question against the session data
     * 
     * @param session The PuTTY session to analyze
     * @param question The user's question
     * @return An answer based on the session data
     */
    public String answerQuestion(PuttySession session, String question) {
        List<String> output = session.getCapturedOutput();
        String lowerQuestion = question.toLowerCase();
        
        // Check for specific question types
        if (lowerQuestion.contains("error") || lowerQuestion.contains("issue")) {
            AnalysisResult result = analyzeSession(session);
            if (!result.getIssues().isEmpty()) {
                StringBuilder answer = new StringBuilder("I found the following issues:\n\n");
                for (Map.Entry<String, String> issue : result.getIssues().entrySet()) {
                    answer.append("Issue: ").append(issue.getKey()).append("\n");
                    answer.append("Solution: ").append(issue.getValue()).append("\n\n");
                }
                return answer.toString();
            } else {
                return "I didn't find any common errors in the session data.";
            }
        } else if (lowerQuestion.contains("command") || lowerQuestion.contains("run")) {
            // Extract recent commands from the session
            StringBuilder answer = new StringBuilder("Recent commands executed in this session:\n\n");
            Pattern commandPattern = Pattern.compile("^[^\\s>]+@[^\\s>]+:[^>]*>\\s*(.+)$");
            
            int commandCount = 0;
            for (String line : output) {
                Matcher matcher = commandPattern.matcher(line);
                if (matcher.find()) {
                    answer.append("- ").append(matcher.group(1)).append("\n");
                    commandCount++;
                }
            }
            
            if (commandCount > 0) {
                return answer.toString();
            } else {
                return "I couldn't identify any clear commands in the session data.";
            }
        }
        
        // Generic response for other questions
        return "I'll need to analyze the session data to answer that question. Please be more specific about what you're looking for.";
    }
    
    /**
     * Analyze command errors in the output
     */
    private void analyzeCommandErrors(List<String> output, AnalysisResult result) {
        for (int i = 0; i < output.size(); i++) {
            String line = output.get(i).toLowerCase();
            
            // Common command not found errors
            if (line.contains("command not found") || line.contains("is not recognized")) {
                String errorLine = output.get(i);
                Pattern cmdPattern = Pattern.compile("'([^']+)'|\"([^\"]+)\"|([^\\s:]+)");
                Matcher matcher = cmdPattern.matcher(errorLine);
                
                if (matcher.find()) {
                    String command = matcher.group(1) != null ? matcher.group(1) : 
                                    (matcher.group(2) != null ? matcher.group(2) : matcher.group(3));
                    
                    result.addIssue(errorLine, 
                        "The command '" + command + "' was not found. Check if it's installed " +
                        "or if you need to update your PATH environment variable.");
                }
            }
            
            // Permission denied errors
            if (line.contains("permission denied")) {
                result.addIssue(output.get(i), 
                    "You don't have sufficient permissions. Try using 'sudo' before the command " +
                    "or check the file/directory permissions.");
            }
        }
    }
    
    /**
     * Class to hold analysis results
     */
    public static class AnalysisResult {
        private final Map<String, String> issues = new HashMap<>();
        
        public void addIssue(String issue, String solution) {
            issues.put(issue, solution);
        }
        
        public Map<String, String> getIssues() {
            return issues;
        }
        
        public boolean hasIssues() {
            return !issues.isEmpty();
        }
    }
}
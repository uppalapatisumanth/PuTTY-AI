package com.puttyai.capture;

/**
 * Interface for listeners that want to be notified of PuTTY session events
 */
public interface PuttyMonitorListener {
    /**
     * Called when a new PuTTY session is detected
     */
    void onSessionStarted(PuttySession session);
    
    /**
     * Called when a PuTTY session ends
     */
    void onSessionEnded(PuttySession session);
}
package com.project.userservice.security;

import java.util.HashMap;
import java.util.Map;

public class RedisSessionManager {
    private static RedisSessionManager instatnce;
    private Map<String, String> sessions;

    private RedisSessionManager() {
        sessions = new HashMap<>();
    }

    public static synchronized RedisSessionManager getInstance() {
        if (instatnce == null) {
            instatnce = new RedisSessionManager();
        }
        return instatnce;
    }

    public synchronized void addSession(String userId, String sessionId) {
        sessions.put(userId, sessionId);
    }

    public synchronized void removeSession(String userId) {
        sessions.remove(userId);
    }

    public synchronized boolean isSessionActive(String userId, String sessionId) {
        String activeSessionId = sessions.get(userId);
        return activeSessionId != null && activeSessionId.equals(sessionId);
    }
 
}

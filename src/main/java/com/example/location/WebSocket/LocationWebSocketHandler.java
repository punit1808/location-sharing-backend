package com.example.location.WebSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.example.location.entity.LocationEntity;

@Component
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // groupId -> sessions
    private final Map<String, Set<WebSocketSession>> groupSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String groupId = Objects.requireNonNull(session.getUri()).getQuery().split("=")[1];
        groupSessions.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(session);
        System.out.println("WS connected: " + groupId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        groupSessions.values().forEach(sessions -> sessions.remove(session));
    }

    // Method to broadcast location update to all members in group
    public void broadcastLocation(String groupId, LocationEntity loc) {
        try {
            Map<String, Object> payloadMap = Map.of(
                "email", loc.getUser().getEmail(),
                "latitude", loc.getLatitude(),
                "longitude", loc.getLongitude(),
                "timestamp", loc.getLastUpdate().toEpochMilli()
            );
            String payload = objectMapper.writeValueAsString(payloadMap);

            Set<WebSocketSession> sessions = groupSessions.getOrDefault(groupId, Collections.emptySet());
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

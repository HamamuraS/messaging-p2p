package com.santi.messagesp2p.controller;


import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

  private final List<WebSocketSession> sessions = new ArrayList<>();

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    sessions.add(session);
  }

  @Override
  protected void handleTextMessage(
      @NonNull WebSocketSession session,
      @NonNull TextMessage message
  ) throws Exception {
    // This example simply broadcasts the received message to all connected clients
    for (WebSocketSession webSocketSession : sessions) {
      webSocketSession.sendMessage(message);
    }
  }

  @Override
  public void afterConnectionClosed(
      @NonNull WebSocketSession session,
      @NonNull CloseStatus status
  ) {
    sessions.remove(session);
  }
}


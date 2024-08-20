package com.santi.messagesp2p.controller;


import com.santi.messagesp2p.model.notification.FriendshipRequest;
import com.santi.messagesp2p.service.UserChannelService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

  // A Long: channelId will be associated with subscribers.
  private final Map<Long, Set<WebSocketSession>> channelSessions = new ConcurrentHashMap<>();
  // a session per connected user will be also kept
  private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

  Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

  @Autowired
  private UserChannelService userChannelService;

  public WebSocketHandler(UserChannelService userChannelService) {
    this.userChannelService = userChannelService;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    Long userId = Long.parseLong(extractUserIdFromQuery(session));
    logger.info("Received socket petition of user {}", userId);

    session.getAttributes().put("userId", userId);
    session.getAttributes().put("removable", false);

    if (userSessions.containsKey(userId)) {
      try {
        session.getAttributes().put("removable", true);
        session.close();
      } catch (IOException e) {
        logger.error("Failed to close the session for user {}", userId, e);
      }
      return;
    }

    userSessions.put(userId, session);

    userChannelService.getChannelsByUser(userId).forEach(channel -> {
      this.addSessionToChannel(channel.getId(), session);
    });
    logger.info("User {} connected", userId);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull org.springframework.web.socket.CloseStatus status) {
    if ((boolean)session.getAttributes().get("removable")) {
      logger.info("User {} was already connected, rejecting new connection", getUserIdFromSession(session));
      return;
    }

    Long userId = getUserIdFromSession(session);

    userChannelService.getChannelsByUser(userId).forEach(channel -> {
      this.removeSessionFromChannel(channel.getId(), session);
    });
    userSessions.remove(userId);

    logger.info("User {} disconnected", userId);
  }

  public void shareNewMessage(Long channelId, String message) throws IOException {

    Set<WebSocketSession> subscribedSessions;
    subscribedSessions = channelSessions.get(channelId);

    logger.info("Sending message to {} subscribers", subscribedSessions.size());
    logger.info(message);

    for (WebSocketSession session : subscribedSessions) {
      if (session.isOpen()) {
        session.sendMessage(new TextMessage(message));
      }
    }

  }

  public void sendMessage(Long userId, String message) throws IOException {
    WebSocketSession session = userSessions.get(userId);
    if (session != null) {
      session.sendMessage(new TextMessage(message));
    }
  }

  public void handleUserInNewChannel(Long userId, Long channelId) {
    WebSocketSession session = userSessions.get(userId);
    if (session != null) {
      addSessionToChannel(channelId, session);
      //TODO: user should be notified of his new channel.
    }
  }


  // when a user adds or exits a channel.
  public void addSessionToChannel(Long channelId, WebSocketSession session) {
    channelSessions.computeIfAbsent(channelId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
  }

  public void removeSessionFromChannel(Long channelId, WebSocketSession session) {
    Set<WebSocketSession> sessions = channelSessions.get(channelId);
    if (sessions != null) {
      sessions.remove(session);
      if (sessions.isEmpty()) {
        channelSessions.remove(channelId);
      }
    }
  }


  private Long getUserIdFromSession(WebSocketSession session) {
    return (Long) session.getAttributes().get("userId");
  }


  private String extractUserIdFromQuery(WebSocketSession session) {
    // URI example ws://localhost:8080/ws?userId=${userId}
    String query = Objects.requireNonNull(session.getUri()).getQuery();
    Map<String, String> queryParams = splitQuery(query);
    return queryParams.get("userId");
  }

  private Map<String, String> splitQuery(String query) {
    return Arrays.stream(query.split("&"))
        .map(param -> param.split("="))
        .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
  }


}


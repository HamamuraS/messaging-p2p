package com.santi.messagesp2p.service;

import com.santi.messagesp2p.controller.WebSocketHandler;
import com.santi.messagesp2p.dto.ChannelDTO;
import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.exception.NotFoundException;
import com.santi.messagesp2p.exception.UnauthorizedException;
import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.Message;
import com.santi.messagesp2p.model.Role;
import com.santi.messagesp2p.model.Type;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import com.santi.messagesp2p.repository.ChannelRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

  @Autowired
  private final ChannelRepository channelRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private RoleService roleService;
  @Autowired
  private UserChannelService userChannelService;
  @Autowired
  private MessageService messageService;
  @Autowired
  private WebSocketHandler webSocketHandler;

  public ChannelService(
      ChannelRepository channelRepository,
      WebSocketHandler webSocketHandler
  ) {
    this.channelRepository = channelRepository;
    this.webSocketHandler = webSocketHandler;
  }

  @Transactional
  public Channel  initializeChannel(ChannelDTO channelDTO) {

    Channel newChannel = new Channel();
    newChannel.setType(Type.valueOf(channelDTO.getType()));
    channelRepository.save(newChannel);

    try {
      Type type = Type.valueOf(channelDTO.getType());
      if (type == Type.GROUP) {
        this.initializeGroup(channelDTO, newChannel);
      }
      else if (type == Type.CONTACT) {
        this.initializeContact(channelDTO, newChannel);
      }
    }
    catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid channel type");
    }

    return newChannel;
  }

  private void initializeGroup(ChannelDTO channelDTO, Channel newChannel) {

    User creator = userService.getUserFromContext();

    if (channelDTO.getName() == null || channelDTO.getName().isEmpty()) {
      throw new BadRequestException("Group name cannot be null or empty");
    }
    newChannel.setName(channelDTO.getName());

    this.registerParticipant(newChannel, creator, roleService.getAdminRole());

    if (channelDTO.getUsers().isEmpty()) {
      throw new BadRequestException("A group must have at least 2 users");
    }
    Set<User> participants = userService.findAllById(channelDTO.getUsers());
    if (participants.isEmpty()) {
      throw new BadRequestException("No users found with the provided ids");
    }

    Role defaultRole = roleService.getDefaultRole();
    this.addParticipants(participants, defaultRole, newChannel);

  }

  private void initializeContact(ChannelDTO channelDTO, Channel newChannel) {

    User creator = userService.getUserFromContext();

    if (channelDTO.getUsers().size() != 1) {
      throw new BadRequestException("A contact must have exactly 1 user");
    }

    Long contactId = channelDTO.getUsers().stream().findFirst().get();

    User contact = userService.findById(contactId).orElseThrow(
        () -> new NotFoundException("Contact not found")
    );

    this.addParticipants(Set.of(creator, contact), roleService.getAdminRole(), newChannel);

  }

  @Transactional
  public void deleteChannel(Long channelId) {
    Channel channel = this.getChannel(channelId);
    this.validateAdminPermissions(channel);
    channelRepository.delete(channel);
  }

  private void registerParticipant(Channel channel, User user, Role role) {
    UserChannel userChannel = userChannelService.createUserChannel(channel, user, role);
    user.addChannel(userChannel);
    channel.addParticipant(userChannel);
    webSocketHandler.handleUserInNewChannel(user.getId(), channel.getId());
  }

  private void addParticipants(Set<User> users, Role defaultRole, Channel channel) {
    users.forEach(user -> this.registerParticipant(channel, user, defaultRole));
  }

  @Transactional
  public Channel removeUserFromChannel(Long channelId, Long userId) {
    Channel channel = this.getChannel(channelId);
    if (channel.getType() == Type.CONTACT) {
      throw new BadRequestException("Cannot remove users from contact channels");
    }
    this.validateAdminPermissions(channel);
    // finding user to be deleted
    User user = userService.findById(userId).orElseThrow(
        () -> new NotFoundException("User not found")
    );
    // finding participation and deleting it
    UserChannel userChannel = userChannelService.getUserChannel(channel, user);
    if (userChannel == null) {
      throw new BadRequestException("User is not part of the channel");
    }
    user.removeChannel(userChannel);
    channel.removeParticipant(userChannel);
    return channel;
  }

  @Transactional
  public Channel addUserToChannel(Long channelId, Long userId) {
    Channel channel = this.getChannel(channelId);
    this.validateAdminPermissions(channel);
    if (userChannelService.isUserInChannel(channelId, userId)) {
      throw new BadRequestException("User is already part of the channel");
    }
    User user = userService.findById(userId).orElseThrow(
        () -> new NotFoundException("User not found")
    );

    Role defaultRole = roleService.getDefaultRole();
    this.registerParticipant(channel, user, defaultRole);
    return channel;
  }

  @Transactional
  public UserChannel updateUserRole(Long channelId, Long userId, String roleName) {
    Channel channel = this.getChannel(channelId);
    this.validateAdminPermissions(channel);
    User user = userService.findById(userId).orElseThrow(
        () -> new NotFoundException("User not found")
    );
    UserChannel userChannel = userChannelService.getUserChannel(channel, user);
    if (userChannel == null) {
      throw new BadRequestException("User is not part of the channel");
    }
    Role role = roleService.getRoleByName(roleName);
    userChannel.setRole(role);
    return userChannel;
  }

  public Page<Message> getMessagesByChannel(Long channelId, LocalDateTime lastLoadedTimestamp, Pageable pageable) {
    return messageService.getMessages(channelId, lastLoadedTimestamp, pageable);
  }

  public Page<Message> getMessagesByChannel(Long channelId, Pageable pageable) {
    return messageService.getMessages(channelId, pageable);
  }

  public Channel getChannel(Long channelId) throws NotFoundException {
    return channelRepository.findById(channelId).orElseThrow(
        () -> new NotFoundException("Channel not found")
    );
  }

  private void validateAdminPermissions(Channel channel) {
    User requester = userService.getUserFromContext();
    String requesterRole = userChannelService.getRoleForUserInChannel(requester, channel);
    if (!requesterRole.equals(roleService.getAdminRole().getName())) {
      throw new UnauthorizedException("Must be admin to perform this action");
    }
  }

}

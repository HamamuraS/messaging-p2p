package com.santi.messagesp2p.service;

import com.santi.messagesp2p.exception.NotFoundException;
import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.Role;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import com.santi.messagesp2p.model.user_channel.UserChannelId;
import com.santi.messagesp2p.repository.UserChannelRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserChannelService {

  @Autowired
  private final UserChannelRepository userChannelRepository;

  public UserChannelService(
      UserChannelRepository userChannelRepository
  ) {
    this.userChannelRepository = userChannelRepository;
  }

  public UserChannel createUserChannel(Channel channel, User user, Role role) {
    return new UserChannel(user, channel, role);
  }

  public UserChannel getUserChannel(Channel channel, User user) {
    return userChannelRepository.findByChannelAndUser(channel, user).orElse(null);
  }

  public String getRoleForUserInChannel(Long userId, Long channelId) {
    UserChannel userChannel = userChannelRepository
        .findByChannelIdAndUserId(channelId, userId)
        .orElseThrow(() -> new NotFoundException("User not found in channel provided."));
    return userChannel.getRole().getName();
  }

  public String getRoleForUserInChannel(User user, Channel channel) {
    return getRoleForUserInChannel(user.getId(), channel.getId());
  }

  public boolean isUserInChannel(Long channelId, Long userId) {
    return userChannelRepository.findByChannelIdAndUserId(channelId, userId).isPresent();
  }

  public Page<Channel> getChannelsByUser(Long userId, Pageable pageable) {
    return userChannelRepository.findByUserId(userId, pageable).map(UserChannel::getChannel);
  }


}

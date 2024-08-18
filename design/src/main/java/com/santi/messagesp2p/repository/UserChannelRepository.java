package com.santi.messagesp2p.repository;

import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import com.santi.messagesp2p.model.user_channel.UserChannelId;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserChannelRepository extends JpaRepository<UserChannel, UserChannelId> {

  @Query("SELECT uc FROM UserChannel uc WHERE uc.channel = :channel AND uc.user = :user")
  Optional<UserChannel> findByChannelAndUser(@Param("channel") Channel channel, @Param("user") User user);

  Optional<UserChannel> findByChannelIdAndUserId(Long channelId, Long userId);

  Page<UserChannel> findByUserId(Long userId, Pageable pageable);

  Set<UserChannel> findAllByUserId(Long userId);

}

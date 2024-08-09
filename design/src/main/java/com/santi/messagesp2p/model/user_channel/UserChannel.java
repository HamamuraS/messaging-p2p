package com.santi.messagesp2p.model.user_channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.Role;
import com.santi.messagesp2p.model.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users_channels")
public class UserChannel {

  @EmbeddedId
  @JsonIgnore
  private UserChannelId id;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @MapsId("channelId")
  @JoinColumn(name = "chan_id", nullable = false)
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  public UserChannel() {
  }

  public UserChannel(User user, Channel channel, Role role) {
    this.user = user;
    this.channel = channel;
    this.role = role;
    this.id = new UserChannelId(user.getId(), channel.getId());
  }

}

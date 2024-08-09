package com.santi.messagesp2p.model.user_channel;

import jakarta.persistence.Embeddable;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class UserChannelId implements Serializable {

  private Long userId;

  private Long channelId;

  public UserChannelId(Long userId, Long channelId) {
    this.userId = userId;
    this.channelId = channelId;
  }

  public UserChannelId() {
  }

  // Override equals and hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserChannelId that = (UserChannelId) o;
    return Objects.equals(userId, that.userId) &&
        Objects.equals(channelId, that.channelId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, channelId);
  }

}

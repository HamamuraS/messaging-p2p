package com.santi.messagesp2p.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santi.messagesp2p.dto.ChannelDTO;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "channels")
@Getter
public class Channel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chan_id")
  private Long id;

  @Column(
      name = "chan_name",
      nullable = false
  )
  @Setter
  private String name;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private Set<UserChannel> userChannels = new HashSet<>();


  @OneToMany(mappedBy = "channel")
  @JsonIgnore
  private final Set<Message> messages = new HashSet<>();

  public boolean contains(User user) {
    return this.userChannels.stream().anyMatch(
        userChannel -> userChannel.getUser().equals(user)
    );
  }

  public Channel addMessage(Message message) {
    this.messages.add(message);
    return this;
  }

  public Channel removeMessage(Message message) {
    this.messages.remove(message);
    return this;
  }

  public Set<User> getUsers() {
    return userChannels.stream().map(UserChannel::getUser).collect(Collectors.toSet());
  }

  public UserChannel addParticipant(UserChannel userChannel) {
    this.userChannels.add(userChannel);
    return userChannel;
  }

  public void removeParticipant(UserChannel userChannel) {
    this.userChannels.remove(userChannel);
  }

  public ChannelDTO toDTO() {
    ChannelDTO channelDTO = new ChannelDTO();
    channelDTO.setId(this.id);
    channelDTO.setName(this.name);
    Set<Long> usersIds = this.userChannels.stream()
        .map(userChannel -> userChannel.getUser().getId())
        .collect(Collectors.toSet());
    channelDTO.setUsers(usersIds);
    return channelDTO;
  }

}

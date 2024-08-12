package com.santi.messagesp2p.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santi.messagesp2p.dto.ChannelDTO;
import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Column(name = "chan_name")
  @Setter
  private String name;

  @Column(
      name = "chan_type",
      nullable = false
  )
  @Setter
  @Enumerated(EnumType.STRING)
  private Type type;

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

  public Set<User> getUsers() {
    return userChannels.stream().map(UserChannel::getUser).collect(Collectors.toSet());
  }

  public void addParticipant(UserChannel userChannel) {
    if (this.type == Type.CONTACT && this.userChannels.size() == 2) {
      throw new BadRequestException("Contact channels can only have 2 participants");
    }
    this.userChannels.add(userChannel);
  }

  public void removeParticipant(UserChannel userChannel) {
    if (this.type == Type.CONTACT) {
      throw new BadRequestException("Contact channels cant delete participants");
    }
    this.userChannels.remove(userChannel);
  }

  public ChannelDTO toDTO() {
    return this.toDTO(null);
  }

  public ChannelDTO toDTO(User petitioner) {

    if (this.type==Type.CONTACT && petitioner==null) {
      throw new RuntimeException("must provide HTTP petitioner for contact->dto casting");
    }

    return switch (this.type) {
      case GROUP -> buildGroupDTO();
      case CONTACT -> buildContactDTO(petitioner);
      default -> throw new RuntimeException("Channel type not supported");
    };

  }

  private ChannelDTO buildGroupDTO() {
    ChannelDTO channelDTO = new ChannelDTO();
    channelDTO.setId(this.id);
    channelDTO.setName(this.name);
    channelDTO.setType(Type.GROUP.name());
    Set<Long> usersIds = this.userChannels.stream()
        .map(userChannel -> userChannel.getUser().getId())
        .collect(Collectors.toSet());
    channelDTO.setUsers(usersIds);
    return channelDTO;
  }

  private ChannelDTO buildContactDTO(User petitioner) {
    ChannelDTO channelDTO = new ChannelDTO();
    channelDTO.setId(this.id);
    User otherUser = null;

    if (!this.contains(petitioner)) {
      throw new RuntimeException("petitioner is not part of this contact channel");
    }

    try{
      otherUser = this.userChannels.stream()
          .filter(userChannel -> !userChannel.getUser().equals(petitioner))
          .findFirst()
          .orElseThrow().getUser();
    }
    catch (Exception e) {
      throw new RuntimeException("petitioner is the only participant in this contact channel");
    }

    channelDTO.setName(otherUser.getUsername());
    channelDTO.setUsers(Set.of(otherUser.getId(), petitioner.getId()));
    channelDTO.setType(Type.CONTACT.name());

    return channelDTO;
  }

}

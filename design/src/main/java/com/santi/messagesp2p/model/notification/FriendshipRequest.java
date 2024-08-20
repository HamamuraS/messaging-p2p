package com.santi.messagesp2p.model.notification;

import com.santi.messagesp2p.dto.FriendshipRequestDTO;
import com.santi.messagesp2p.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Different kind of notifications were not considered in this design.
 */

@Entity
@Getter
@Setter
@Table(name = "friendship_requests")
public class FriendshipRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fr_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "fr_sender")
  private User sender;

  @ManyToOne
  @JoinColumn(name = "fr_receiver")
  private User receiver;

  @Column(name = "fr_accepted")
  private Boolean accepted;

  @Column(name = "fr_timestamp")
  private LocalDateTime timestamp;

  public FriendshipRequest() {
  }

  public FriendshipRequestDTO toDTO() {
    FriendshipRequestDTO dto = new FriendshipRequestDTO();
    dto.setId(this.id);
    dto.setSenderId(this.sender.getId());
    dto.setSenderName(this.sender.getUsername());
    dto.setReceiverId(this.receiver.getId());
    dto.setReceiverName(this.receiver.getUsername());
    dto.setAccepted(this.accepted);
    dto.setTimestamp(this.timestamp);
    return dto;
  }

}

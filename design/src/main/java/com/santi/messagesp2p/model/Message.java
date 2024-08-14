package com.santi.messagesp2p.model;

import com.santi.messagesp2p.dto.MessageDTO;
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

@Entity
@Getter
@Setter
@Table(name = "messages")
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "msg_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "msg_sender")
  private User sender;

  @ManyToOne
  @JoinColumn(name = "msg_channel")
  private Channel channel;

  @Column(name = "msg_content")
  private String content;

  @Column(name = "msg_timestamp")
  private LocalDateTime timestamp;

  public MessageDTO toDTO() {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setSender(this.sender.getUsername());
    messageDTO.setChannel(this.channel.getId());
    messageDTO.setContent(this.content);
    messageDTO.setTimestamp(this.timestamp);
    messageDTO.setSenderId(this.sender.getId());
    messageDTO.setMessageId(this.id);
    return messageDTO;
  }

}

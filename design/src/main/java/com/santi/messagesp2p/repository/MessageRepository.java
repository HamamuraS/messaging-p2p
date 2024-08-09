package com.santi.messagesp2p.repository;

import com.santi.messagesp2p.model.Message;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  List<Message> findByChannelId(Long channelId);

  Page<Message> findByChannelId(Long channelId, Pageable pageable);

}

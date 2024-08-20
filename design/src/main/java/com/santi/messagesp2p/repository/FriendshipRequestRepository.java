package com.santi.messagesp2p.repository;

import com.santi.messagesp2p.model.notification.FriendshipRequest;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long> {

  @Query("SELECT f FROM FriendshipRequest f WHERE f.receiver.id = ?1 AND f.accepted IS NULL")
  Set<FriendshipRequest> findByReceiverId(Long receiverId);

}

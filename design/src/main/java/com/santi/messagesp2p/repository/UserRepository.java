package com.santi.messagesp2p.repository;

import com.santi.messagesp2p.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsernameOrEmail(String username, String email);

  //List<User> findAllById(Set<Long> ids);

}

package com.santi.messagesp2p.repository;

import com.santi.messagesp2p.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

  public Optional<Role> findByName(String name);

}

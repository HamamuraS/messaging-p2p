package com.santi.messagesp2p.service;

import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.model.Role;
import com.santi.messagesp2p.repository.RoleRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public RoleService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  public Optional<Role> findByName(String name) {
    return roleRepository.findByName(name);
  }

  public Role getDefaultRole() {
    return roleRepository.findByName("USER").orElseThrow(
        () -> new BadRequestException("Default role not found"));
  }

  public Role getAdminRole() {
    return roleRepository.findByName("ADMIN").orElseThrow(
        () -> new BadRequestException("Admin role not found"));
  }

  public Role getRoleByName(String name) {
    return roleRepository.findByName(name).orElseThrow(
        () -> new BadRequestException("Role not found"));
  }

}

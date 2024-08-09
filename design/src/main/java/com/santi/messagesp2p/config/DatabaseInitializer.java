package com.santi.messagesp2p.config;

import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    // Verifica si el usuario admin existe
    if (!userRepository.existsByUsername("admin")) {
      // Si no existe, crea el usuario admin
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword(passwordEncoder.encode("admin")); // Asegúrate de que la contraseña esté cifrada
      admin.setEmail("admin@example.com"); // Agrega un correo electrónico si es necesario
      userRepository.save(admin);
    }
  }
}

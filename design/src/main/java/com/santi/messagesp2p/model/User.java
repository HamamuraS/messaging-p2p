package com.santi.messagesp2p.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;


  @Column(
      name = "user_username",
      nullable = false,
      unique = true
  )
  @Setter
  private String username;

  @Column(name = "user_email")
  @Setter
  private String email;

  @Column(
      name = "user_password",
      nullable = false
  )
  @Setter
  private String password;

  @OneToMany(mappedBy = "sender")
  @JsonIgnore
  private final Set<Message> messages = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private final Set<UserChannel> userChannels = new HashSet<>();

  public User() {
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public void addChannel(UserChannel userChannel) {
    this.userChannels.add(userChannel);
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  public void removeChannel(UserChannel userChannel) {
    this.userChannels.remove(userChannel);
  }
}

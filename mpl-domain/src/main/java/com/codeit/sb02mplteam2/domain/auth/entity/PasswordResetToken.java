package com.codeit.sb02mplteam2.domain.auth.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PasswordResetToken {

  private static final int EXPIRATION_MINUTES = 5; // 토큰 만료 시간 (5분)

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  @Column(nullable = false)
  private LocalDateTime expiryDate;

  public PasswordResetToken(String token, User user) {
    this.token = token;
    this.user = user;
    this.expiryDate = calculateExpiryDate();
  }

  private LocalDateTime calculateExpiryDate() {
    return LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(this.expiryDate);
  }
}

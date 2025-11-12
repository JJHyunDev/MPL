package com.codeit.sb02mplteam2.domain.auth.service;

import com.codeit.sb02mplteam2.domain.auth.entity.PasswordResetToken;
import com.codeit.sb02mplteam2.domain.auth.repository.PasswordResetTokenRepository;
import com.codeit.sb02mplteam2.domain.mail.PasswordResetEvent;
import com.codeit.sb02mplteam2.domain.mail.service.EmailService;
import com.codeit.sb02mplteam2.domain.setting.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.setting.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  @Value("${mpl.admin.username}")
  private String username;
  @Value("${mpl.admin.email}")
  private String email;
  @Value("${mpl.admin.password}")
  private String password;
  // 프론트엔드의 비밀번호 재설정 페이지 URL (토큰 파라미터 제외)
  @Value("${mpl.reset-password.url}")
  private String resetPasswordUrl;

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AlarmSettingRepository alarmSettingRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final EmailService emailService;
  private final ApplicationEventPublisher eventPublisher;

  // 서버가 실행되면 initAdmin() 실행 -> admin이 없는 경우 admin 등록
  @Transactional
  @Override
  public UserDto initAdmin() {
    if(userRepository.existsByEmail(email) || userRepository.existsByUsername(username)){
      log.warn("이미 어드민이 존재합니다.");
      return null;
    }

    String encodedPassword = passwordEncoder.encode(password);
    User admin = new User(username, email, encodedPassword, null);
    admin.updateRole(Role.ADMIN);
    userRepository.save(admin);
    alarmSettingRepository.save(new AlarmSetting(admin));
    UserDto adminDto = userMapper.toDto(admin);
    log.info("어드민이 초기화되었습니다. username={}", adminDto.username());
    log.debug("어드민 초기화 상세 정보: {}", adminDto);
    return adminDto;
  }

  @Override
  @Transactional
  public void createPasswordResetTokenForUser(String userEmail) {
    log.info("비밀번호 재설정 토큰 생성 요청: {}", userEmail);

    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new MplException(ErrorCode.USER_NOT_FOUND));

    // 해당 사용자에게 이미 발급된 토큰이 있다면 삭제하여 이전 요청 무효화
    passwordResetTokenRepository.deleteAllByUserId(user.getId());

    // 토큰 생성(JWT 아님 그냥 UUID)
    String token = UUID.randomUUID().toString();
    PasswordResetToken myToken = new PasswordResetToken(token, user);
    passwordResetTokenRepository.save(myToken);

    String resetLink = resetPasswordUrl + token;
    log.info("이메일 발송 시작 시간: {}", LocalDateTime.now());

    eventPublisher.publishEvent(new PasswordResetEvent(userEmail, resetLink));
//    emailService.sendEmail(user.getEmail(), "[모두의 플리] 비밀번호 재설정 링크",
//        "아래 링크를 클릭하여 비밀번호를 재설정하세요: " + resetLink);
    log.info("이메일 발송 완료 시간: {}", LocalDateTime.now());
  }

  @Override
  @Transactional
  public void resetPassword(String token, String newPassword) {
    log.info("비밀번호 재설정 요청: 토큰(앞 6자리) = {}, 새 비밀번호 길이 = {}", token.substring(0, Math.min(token.length(), 6)), newPassword.length());

    PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new MplException(ErrorCode.INVALID_TOKEN));

    if (resetToken.isExpired()) {
      passwordResetTokenRepository.delete(resetToken);
      throw new MplException(ErrorCode.EXPIRED_TOKEN);
    }

    User user = resetToken.getUser();
    String encodedPassword = passwordEncoder.encode(newPassword);
    user.updatePassword(encodedPassword);

    passwordResetTokenRepository.delete(resetToken);
  }
}

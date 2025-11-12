package com.codeit.sb02mplteam2.domain.admin.service;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAdminService implements AdminService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtService jwtService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public UserDto updateUserRole(Long userId, RoleUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.updateRole(request.role());

    // 현재 인증된 관리자의 ID를 가져옴. (Thread Local 에 저장된 값)
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if(!(principal instanceof MplUserDetails adminPrincipal)){
      throw new MplException(ErrorCode.ADMIN_USER_NOT_FOUND);
    }
    Long adminId = adminPrincipal.getId();

    eventPublisher.publishEvent(new NotificationEvent(
        this,
        userId, // 받는 사람, 권한 변경 된 유저
        NotificationType.ROLE_CHANGED,
        null,
        adminId // 이벤트 발생시킨 사람, 관리자
    ));

    // Role update된 user, userId 로 token 무효화 시키기
    jwtService.invalidateJwtSession(userId);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void lockUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.lock();

    // userId 로 token 무효화 시키기
    jwtService.invalidateJwtSession(userId);
  }

  @Transactional
  @Override
  public void unlockUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.unlock();
  }
}

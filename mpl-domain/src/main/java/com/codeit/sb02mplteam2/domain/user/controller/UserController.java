package com.codeit.sb02mplteam2.domain.user.controller;

import com.codeit.sb02mplteam2.domain.user.dto.*;
import com.codeit.sb02mplteam2.domain.user.service.UserService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.security.jwt.CheckJwtBlacklist;
import com.codeit.sb02mplteam2.swagger.UserApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;

  @CheckJwtBlacklist
  @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
  @PutMapping(value ="/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") Long userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("사용자 수정 요청: {}, {}", userId, userUpdateRequest);
    UserDto userDto = userService.update(userId, userUpdateRequest, Optional.ofNullable(profile));
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable Long userId) {
    log.info("유저 조회 요청: {}", userId);
    UserDto user = userService.findById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserDto>> findAll() {
    log.info("유저 목록 조회 요청");
    List<UserDto> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @GetMapping("/users/search")
  public ResponseEntity<UserCursorPageResponse<UserSearchDto>> searchUsers(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "ALL") UserSearchFilter filter,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal MplUserDetails userDetails
  ) {
    log.info("유저 검색 요청: keyword={}, filter={}, cursorId={}, size={}", keyword, filter, cursorId, size);
    UserSearchRequest request = new UserSearchRequest(keyword, filter, cursorId, size);
    UserCursorPageResponse<UserSearchDto> response =
        userService.searchUsers(userDetails.getId(), request);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> delete(@PathVariable Long userId) {
    log.info("사용자 삭제 요청: {}", userId);
    userService.softDelete(userId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/users/me")
  public ResponseEntity<UserDto> getMyInfo(@AuthenticationPrincipal MplUserDetails userDetails) {
    Long myId = userDetails.getId();
    log.info("내 정보 조회 요청: {}", myId);
    UserDto userDto = userService.findById(myId);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @CheckJwtBlacklist
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/users/me")
  public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal MplUserDetails userDetails) {
    Long myId = userDetails.getId();
    log.info("내 계정 삭제 요청: {}", myId);
    userService.softDelete(myId);
    return ResponseEntity.noContent().build();
  }
}

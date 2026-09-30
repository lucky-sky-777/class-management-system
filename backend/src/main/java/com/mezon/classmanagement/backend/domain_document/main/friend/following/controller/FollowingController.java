package com.mezon.classmanagement.backend.domain_document.main.friend.following.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.dto.FollowingResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.service.FollowingService;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatusResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/followings")
@RestController
public class FollowingController {

    FollowingService followingService;
    AuthService authService;
    JwtService jwtService;

    @PostMapping("/{targetUserId}")
    public ResponseDTO<FriendshipStatusResponseDto> follow(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        FriendshipStatusResponseDto response = followingService.follow(clientUserId, targetUserId);

        return ResponseDTO.ok("Followed successfully", response);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseDTO<Void> unfollow(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        followingService.unfollow(clientUserId, targetUserId);

        return ResponseDTO.ok("Unfollowed successfully");
    }

    @GetMapping
    public ResponseDTO<Page<FollowingResponseDto>> getMyFollowingList(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long clientUserId = getClientUserId();
        Page<FollowingResponseDto> response = followingService.getFollowingList(clientUserId, pageable);

        return ResponseDTO.ok("Fetch following list successfully", response);
    }

    @GetMapping("/users/{userId}")
    public ResponseDTO<Page<FollowingResponseDto>> getUserFollowingList(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<FollowingResponseDto> response = followingService.getFollowingList(userId, pageable);

        return ResponseDTO.ok("Fetch user following list successfully", response);
    }

    @GetMapping("/count")
    public ResponseDTO<Long> countMyFollowing() {
        Long clientUserId = getClientUserId();
        Long count = followingService.countFollowing(clientUserId);

        return ResponseDTO.ok("Fetch following count successfully", count);
    }

    private Long getClientUserId() {
        Authentication authentication = authService.getAuthentication();
        return jwtService.extractUserIdFromAuthentication(authentication);
    }

}

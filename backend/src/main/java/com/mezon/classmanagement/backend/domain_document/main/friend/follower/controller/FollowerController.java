package com.mezon.classmanagement.backend.domain_document.main.friend.follower.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.dto.FollowerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.service.FollowerService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/followers")
@RestController
public class FollowerController {

    FollowerService followerService;
    AuthService authService;
    JwtService jwtService;

    @DeleteMapping("/{followerUserId}")
    public ResponseDTO<Void> removeFollower(
            @PathVariable Long followerUserId
    ) {
        Long clientUserId = getClientUserId();
        followerService.removeFollower(clientUserId, followerUserId);

        return ResponseDTO.ok("Follower removed successfully");
    }

    @GetMapping
    public ResponseDTO<Page<FollowerResponseDto>> getMyFollowerList(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long clientUserId = getClientUserId();
        Page<FollowerResponseDto> response = followerService.getFollowerList(clientUserId, pageable);

        return ResponseDTO.ok("Fetch follower list successfully", response);
    }

    @GetMapping("/users/{userId}")
    public ResponseDTO<Page<FollowerResponseDto>> getUserFollowerList(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<FollowerResponseDto> response = followerService.getFollowerList(userId, pageable);

        return ResponseDTO.ok("Fetch user follower list successfully", response);
    }

    @GetMapping("/count")
    public ResponseDTO<Long> countMyFollower() {
        Long clientUserId = getClientUserId();
        Long count = followerService.countFollower(clientUserId);

        return ResponseDTO.ok("Fetch follower count successfully", count);
    }

    private Long getClientUserId() {
        Authentication authentication = authService.getAuthentication();
        return jwtService.extractUserIdFromAuthentication(authentication);
    }

}

package com.mezon.classmanagement.backend.domain_document.main.friend.friend.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendSummaryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatusResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.MutualFriendResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.service.FriendService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/friends")
@RestController
public class FriendController {

    FriendService friendService;
    AuthService authService;
    JwtService jwtService;

    @GetMapping
    public ResponseDTO<Page<FriendResponseDto>> getMyFriendList(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long clientUserId = getClientUserId();
        Page<FriendResponseDto> response = friendService.getFriendList(clientUserId, query, pageable);

        return ResponseDTO.ok("Fetch friend list successfully", response);
    }

    @GetMapping("/users/{userId}")
    public ResponseDTO<Page<FriendResponseDto>> getUserFriendList(
            @PathVariable Long userId,
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<FriendResponseDto> response = friendService.getFriendList(userId, query, pageable);

        return ResponseDTO.ok("Fetch user friend list successfully", response);
    }

    @GetMapping("/status/{targetUserId}")
    public ResponseDTO<FriendshipStatusResponseDto> getFriendshipStatus(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        FriendshipStatusResponseDto response = friendService.getFriendshipStatus(clientUserId, targetUserId);

        return ResponseDTO.ok("Fetch friendship status successfully", response);
    }

    @GetMapping("/summary")
    public ResponseDTO<FriendSummaryResponseDto> getSummary() {
        Long clientUserId = getClientUserId();
        FriendSummaryResponseDto response = friendService.getSummary(clientUserId);

        return ResponseDTO.ok("Fetch friend summary successfully", response);
    }

    @GetMapping("/mutual/{targetUserId}")
    public ResponseDTO<MutualFriendResponseDto> getMutualFriends(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        MutualFriendResponseDto response = friendService.getMutualFriends(clientUserId, targetUserId);

        return ResponseDTO.ok("Fetch mutual friends successfully", response);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseDTO<Void> unfriend(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        friendService.unfriend(clientUserId, targetUserId);

        return ResponseDTO.ok("Unfriended successfully");
    }

    private Long getClientUserId() {
        Authentication authentication = authService.getAuthentication();
        return jwtService.extractUserIdFromAuthentication(authentication);
    }

}

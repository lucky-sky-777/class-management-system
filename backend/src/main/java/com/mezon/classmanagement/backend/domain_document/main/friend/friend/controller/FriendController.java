package com.mezon.classmanagement.backend.domain_document.main.friend.friend.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.dto.FollowerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.dto.FollowingResponseDto;
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
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/requests/{targetUserId}")
    public ResponseDTO<FriendshipStatusResponseDto> sendFriendRequest(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        FriendshipStatusResponseDto response = friendService.sendFriendRequest(clientUserId, targetUserId);

        return ResponseDTO.ok("Friend request processed successfully", response);
    }

    @PostMapping("/requests/{requesterUserId}/accept")
    public ResponseDTO<FriendResponseDto> acceptFriendRequest(
            @PathVariable Long requesterUserId
    ) {
        Long clientUserId = getClientUserId();
        FriendResponseDto response = friendService.acceptFriendRequest(clientUserId, requesterUserId);

        return ResponseDTO.ok("Friend request accepted successfully", response);
    }

    @DeleteMapping("/requests/{requesterUserId}/reject")
    public ResponseDTO<Void> rejectFriendRequest(
            @PathVariable Long requesterUserId
    ) {
        Long clientUserId = getClientUserId();
        friendService.rejectFriendRequest(clientUserId, requesterUserId);

        return ResponseDTO.ok("Friend request rejected successfully");
    }

    @DeleteMapping("/requests/{targetUserId}/cancel")
    public ResponseDTO<Void> cancelFriendRequest(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        friendService.cancelFriendRequest(clientUserId, targetUserId);

        return ResponseDTO.ok("Sent friend request cancelled successfully");
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseDTO<Void> unfriend(
            @PathVariable Long targetUserId
    ) {
        Long clientUserId = getClientUserId();
        friendService.unfriend(clientUserId, targetUserId);

        return ResponseDTO.ok("Unfriended successfully");
    }

    @GetMapping
    public ResponseDTO<Page<FriendResponseDto>> getFriendList(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long clientUserId = getClientUserId();
        Page<FriendResponseDto> response = friendService.getFriendList(clientUserId, query, pageable);

        return ResponseDTO.ok("Fetch friend list successfully", response);
    }

    @GetMapping("/requests/received")
    public ResponseDTO<Page<FollowerResponseDto>> getReceivedRequests(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long clientUserId = getClientUserId();
        Page<FollowerResponseDto> response = friendService.getReceivedRequests(clientUserId, pageable);

        return ResponseDTO.ok("Fetch received friend requests successfully", response);
    }

    @GetMapping("/requests/sent")
    public ResponseDTO<Page<FollowingResponseDto>> getSentRequests(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long clientUserId = getClientUserId();
        Page<FollowingResponseDto> response = friendService.getSentRequests(clientUserId, pageable);

        return ResponseDTO.ok("Fetch sent friend requests successfully", response);
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

    private Long getClientUserId() {
        Authentication authentication = authService.getAuthentication();
        return jwtService.extractUserIdFromAuthentication(authentication);
    }

}

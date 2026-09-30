package com.mezon.classmanagement.backend.domain_document.main.friend.friend.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.repository.FollowingRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendSummaryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatus;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatusResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.MutualFriendResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity.Friend;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.mapper.FriendMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.repository.FriendRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FriendService {

    FriendRepository friendRepository;
    FollowerRepository followerRepository;
    FollowingRepository followingRepository;

    UserService userService;
    FriendMapper friendMapper;

    @Transactional(readOnly = true)
    public Page<FriendResponseDto> getFriendList(Long userId, String query, Pageable pageable) {
        Page<Friend> page;
        if (query != null && !query.trim().isEmpty()) {
            page = friendRepository.searchFriends(userId, query.trim(), pageable);
        } else {
            page = friendRepository.findByUser1_IdOrUser2_Id(userId, userId, pageable);
        }

        return page.map(friend -> friendMapper.toFriendResponseDto(friend, userId));
    }

    @Transactional(readOnly = true)
    public FriendshipStatusResponseDto getFriendshipStatus(Long clientUserId, Long targetUserId) {
        if (clientUserId.equals(targetUserId)) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.SELF)
                    .build();
        }

        Long minId = Math.min(clientUserId, targetUserId);
        Long maxId = Math.max(clientUserId, targetUserId);

        // Check if mutual friends
        if (friendRepository.existsByUser1_IdAndUser2_Id(minId, maxId)) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.FRIEND)
                    .build();
        }

        boolean clientFollowsTarget = followingRepository.existsByUser_IdAndFollowing_Id(clientUserId, targetUserId);
        boolean targetFollowsClient = followingRepository.existsByUser_IdAndFollowing_Id(targetUserId, clientUserId);

        if (clientFollowsTarget) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.FOLLOWING)
                    .build();
        }

        if (targetFollowsClient) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.FOLLOWER)
                    .build();
        }

        return FriendshipStatusResponseDto.builder()
                .targetUserId(targetUserId)
                .status(FriendshipStatus.NONE)
                .build();
    }

    @Transactional(readOnly = true)
    public FriendSummaryResponseDto getSummary(Long clientUserId) {
        Long friendCount = friendRepository.countByUser1_IdOrUser2_Id(clientUserId, clientUserId);
        Long followerCount = followerRepository.countByUser_Id(clientUserId);
        Long followingCount = followingRepository.countByUser_Id(clientUserId);

        return FriendSummaryResponseDto.builder()
                .friendCount(friendCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    @Transactional(readOnly = true)
    public MutualFriendResponseDto getMutualFriends(Long clientUserId, Long targetUserId) {
        userService.throwIfNotExistsById(targetUserId);

        List<User> mutualUsers = friendRepository.findMutualFriends(clientUserId, targetUserId);
        List<UserResponseDto> dtoList = mutualUsers.stream()
                .map(friendMapper::toUserResponseDto)
                .toList();

        return MutualFriendResponseDto.builder()
                .mutualCount((long) dtoList.size())
                .mutualFriends(dtoList)
                .build();
    }

    @Transactional
    public void unfriend(Long clientUserId, Long friendUserId) {
        Long minId = Math.min(clientUserId, friendUserId);
        Long maxId = Math.max(clientUserId, friendUserId);

        Friend friend = friendRepository.findByUser1_IdAndUser2_Id(minId, maxId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Friendship not found"));

        friendRepository.delete(friend);

        // Also remove client user's follow of friend user to break mutual follow
        followingRepository.findByUser_IdAndFollowing_Id(clientUserId, friendUserId)
                .ifPresent(followingRepository::delete);
        followerRepository.findByUser_IdAndFollower_Id(friendUserId, clientUserId)
                .ifPresent(followerRepository::delete);
    }

}

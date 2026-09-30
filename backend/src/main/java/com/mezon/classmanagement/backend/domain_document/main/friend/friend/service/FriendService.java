package com.mezon.classmanagement.backend.domain_document.main.friend.friend.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.dto.FollowerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.mapper.FollowerMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.dto.FollowingResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.entity.Following;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.mapper.FollowingMapper;
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
    FollowerMapper followerMapper;
    FollowingMapper followingMapper;

    @Transactional
    public FriendshipStatusResponseDto sendFriendRequest(Long clientUserId, Long targetUserId) {
        if (clientUserId.equals(targetUserId)) {
            throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Cannot send friend request to yourself");
        }

        User targetUser = userService.findByUserIdOrThrow(targetUserId);
        User clientUser = userService.findByUserIdOrThrow(clientUserId);

        Long minId = Math.min(clientUserId, targetUserId);
        Long maxId = Math.max(clientUserId, targetUserId);

        if (friendRepository.existsByUser1_IdAndUser2_Id(minId, maxId)) {
            throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Users are already friends");
        }

        if (followingRepository.existsByUser_IdAndFollowing_Id(clientUserId, targetUserId)) {
            throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Friend request already sent");
        }

        // Auto Mutual Accept if the other user has already sent a request to this user
        boolean hasIncomingRequest = followerRepository.existsByUser_IdAndFollower_Id(clientUserId, targetUserId);
        if (hasIncomingRequest) {
            followerRepository.deleteByUser_IdAndFollower_Id(clientUserId, targetUserId);
            followingRepository.deleteByUser_IdAndFollowing_Id(targetUserId, clientUserId);

            User u1 = clientUserId < targetUserId ? clientUser : targetUser;
            User u2 = clientUserId < targetUserId ? targetUser : clientUser;

            Friend friend = Friend.builder()
                    .user1(u1)
                    .user2(u2)
                    .build();
            friendRepository.save(friend);

            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.FRIEND)
                    .build();
        }

        Following following = Following.builder()
                .user(clientUser)
                .following(targetUser)
                .build();
        followingRepository.save(following);

        Follower follower = Follower.builder()
                .user(targetUser)
                .follower(clientUser)
                .build();
        followerRepository.save(follower);

        return FriendshipStatusResponseDto.builder()
                .targetUserId(targetUserId)
                .status(FriendshipStatus.REQUEST_SENT)
                .build();
    }

    @Transactional
    public FriendResponseDto acceptFriendRequest(Long clientUserId, Long requesterUserId) {
        if (clientUserId.equals(requesterUserId)) {
            throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Cannot accept friend request from yourself");
        }

        Follower incomingFollower = followerRepository.findByUser_IdAndFollower_Id(clientUserId, requesterUserId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Friend request not found"));

        followerRepository.delete(incomingFollower);
        followingRepository.deleteByUser_IdAndFollowing_Id(requesterUserId, clientUserId);

        Long minId = Math.min(clientUserId, requesterUserId);
        Long maxId = Math.max(clientUserId, requesterUserId);

        if (friendRepository.existsByUser1_IdAndUser2_Id(minId, maxId)) {
            throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Users are already friends");
        }

        User u1 = clientUserId < requesterUserId ? incomingFollower.getUser() : incomingFollower.getFollower();
        User u2 = clientUserId < requesterUserId ? incomingFollower.getFollower() : incomingFollower.getUser();

        Friend friend = Friend.builder()
                .user1(u1)
                .user2(u2)
                .build();
        Friend savedFriend = friendRepository.save(friend);

        return friendMapper.toFriendResponseDto(savedFriend, clientUserId);
    }

    @Transactional
    public void rejectFriendRequest(Long clientUserId, Long requesterUserId) {
        Follower incomingFollower = followerRepository.findByUser_IdAndFollower_Id(clientUserId, requesterUserId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Friend request not found"));

        followerRepository.delete(incomingFollower);
        followingRepository.deleteByUser_IdAndFollowing_Id(requesterUserId, clientUserId);
    }

    @Transactional
    public void cancelFriendRequest(Long clientUserId, Long targetUserId) {
        Following outgoingFollowing = followingRepository.findByUser_IdAndFollowing_Id(clientUserId, targetUserId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Sent friend request not found"));

        followingRepository.delete(outgoingFollowing);
        followerRepository.deleteByUser_IdAndFollower_Id(targetUserId, clientUserId);
    }

    @Transactional
    public void unfriend(Long clientUserId, Long friendUserId) {
        Long minId = Math.min(clientUserId, friendUserId);
        Long maxId = Math.max(clientUserId, friendUserId);

        Friend friend = friendRepository.findByUser1_IdAndUser2_Id(minId, maxId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Friendship not found"));

        friendRepository.delete(friend);
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

        if (friendRepository.existsByUser1_IdAndUser2_Id(minId, maxId)) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.FRIEND)
                    .build();
        }

        if (followingRepository.existsByUser_IdAndFollowing_Id(clientUserId, targetUserId)) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.REQUEST_SENT)
                    .build();
        }

        if (followerRepository.existsByUser_IdAndFollower_Id(clientUserId, targetUserId)) {
            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.REQUEST_RECEIVED)
                    .build();
        }

        return FriendshipStatusResponseDto.builder()
                .targetUserId(targetUserId)
                .status(FriendshipStatus.NONE)
                .build();
    }

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
    public Page<FollowerResponseDto> getReceivedRequests(Long clientUserId, Pageable pageable) {
        Page<Follower> page = followerRepository.findByUser_Id(clientUserId, pageable);
        return page.map(followerMapper::toFollowerResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<FollowingResponseDto> getSentRequests(Long clientUserId, Pageable pageable) {
        Page<Following> page = followingRepository.findByUser_Id(clientUserId, pageable);
        return page.map(followingMapper::toFollowingResponseDto);
    }

    @Transactional(readOnly = true)
    public FriendSummaryResponseDto getSummary(Long clientUserId) {
        Long friendCount = friendRepository.countByUser1_IdOrUser2_Id(clientUserId, clientUserId);
        Long receivedCount = followerRepository.countByUser_Id(clientUserId);
        Long sentCount = followingRepository.countByUser_Id(clientUserId);

        return FriendSummaryResponseDto.builder()
                .friendCount(friendCount)
                .receivedRequestCount(receivedCount)
                .sentRequestCount(sentCount)
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

}

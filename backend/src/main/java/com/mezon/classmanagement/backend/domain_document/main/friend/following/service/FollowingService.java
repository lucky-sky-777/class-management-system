package com.mezon.classmanagement.backend.domain_document.main.friend.following.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.dto.FollowingResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.entity.Following;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.mapper.FollowingMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.repository.FollowingRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatus;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatusResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity.Friend;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.repository.FriendRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FollowingService {

    FollowingRepository followingRepository;
    FollowerRepository followerRepository;
    FriendRepository friendRepository;

    UserService userService;
    FollowingMapper followingMapper;

    @Transactional
    public FriendshipStatusResponseDto follow(Long clientUserId, Long targetUserId) {
        if (clientUserId.equals(targetUserId)) {
            throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Cannot follow yourself");
        }

        User clientUser = userService.findByUserIdOrThrow(clientUserId);
        User targetUser = userService.findByUserIdOrThrow(targetUserId);

        if (followingRepository.existsByUser_IdAndFollowing_Id(clientUserId, targetUserId)) {
            throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Already following this user");
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

        // Check if targetUser is already following clientUser (Mutual Follow)
        boolean isMutual = followingRepository.existsByUser_IdAndFollowing_Id(targetUserId, clientUserId);
        if (isMutual) {
            Long minId = Math.min(clientUserId, targetUserId);
            Long maxId = Math.max(clientUserId, targetUserId);

            if (!friendRepository.existsByUser1_IdAndUser2_Id(minId, maxId)) {
                User u1 = clientUserId < targetUserId ? clientUser : targetUser;
                User u2 = clientUserId < targetUserId ? targetUser : clientUser;

                Friend friend = Friend.builder()
                        .user1(u1)
                        .user2(u2)
                        .build();
                friendRepository.save(friend);
            }

            return FriendshipStatusResponseDto.builder()
                    .targetUserId(targetUserId)
                    .status(FriendshipStatus.FRIEND)
                    .build();
        }

        return FriendshipStatusResponseDto.builder()
                .targetUserId(targetUserId)
                .status(FriendshipStatus.FOLLOWING)
                .build();
    }

    @Transactional
    public void unfollow(Long clientUserId, Long targetUserId) {
        Following outgoingFollowing = followingRepository.findByUser_IdAndFollowing_Id(clientUserId, targetUserId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Not following this user"));

        followingRepository.delete(outgoingFollowing);
        followerRepository.deleteByUser_IdAndFollower_Id(targetUserId, clientUserId);

        // If they were friends, mutual follow is now broken, so remove friend record
        Long minId = Math.min(clientUserId, targetUserId);
        Long maxId = Math.max(clientUserId, targetUserId);

        friendRepository.findByUser1_IdAndUser2_Id(minId, maxId)
                .ifPresent(friendRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<FollowingResponseDto> getFollowingList(Long userId, Pageable pageable) {
        Page<Following> page = followingRepository.findByUser_Id(userId, pageable);
        return page.map(followingMapper::toFollowingResponseDto);
    }

    @Transactional(readOnly = true)
    public Long countFollowing(Long userId) {
        return followingRepository.countByUser_Id(userId);
    }

}

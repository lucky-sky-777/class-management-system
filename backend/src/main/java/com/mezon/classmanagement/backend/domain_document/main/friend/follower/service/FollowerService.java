package com.mezon.classmanagement.backend.domain_document.main.friend.follower.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.dto.FollowerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.mapper.FollowerMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.repository.FollowingRepository;
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
public class FollowerService {

    FollowerRepository followerRepository;
    FollowingRepository followingRepository;
    FriendRepository friendRepository;

    FollowerMapper followerMapper;

    @Transactional
    public void removeFollower(Long clientUserId, Long followerUserId) {
        Follower follower = followerRepository.findByUser_IdAndFollower_Id(clientUserId, followerUserId)
                .orElseThrow(() -> new GlobalException(GlobalException.Type.NOT_FOUND, "Follower not found"));

        followerRepository.delete(follower);
        followingRepository.deleteByUser_IdAndFollowing_Id(followerUserId, clientUserId);

        // If they were friends, removing follower breaks mutual follow, so remove friend record
        Long minId = Math.min(clientUserId, followerUserId);
        Long maxId = Math.max(clientUserId, followerUserId);

        friendRepository.findByUser1_IdAndUser2_Id(minId, maxId)
                .ifPresent(friendRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<FollowerResponseDto> getFollowerList(Long userId, Pageable pageable) {
        Page<Follower> page = followerRepository.findByUser_Id(userId, pageable);
        return page.map(followerMapper::toFollowerResponseDto);
    }

    @Transactional(readOnly = true)
    public Long countFollower(Long userId) {
        return followerRepository.countByUser_Id(userId);
    }

}

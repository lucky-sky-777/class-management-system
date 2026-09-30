package com.mezon.classmanagement.backend.friend;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.dto.FollowingResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.entity.Following;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.mapper.FollowingMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.repository.FollowingRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.service.FollowingService;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatus;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatusResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity.Friend;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowingServiceTest {

    @Mock
    FollowingRepository followingRepository;

    @Mock
    FollowerRepository followerRepository;

    @Mock
    FriendRepository friendRepository;

    @Mock
    UserService userService;

    @Mock
    FollowingMapper followingMapper;

    @InjectMocks
    FollowingService followingService;

    User user1;
    User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .username("user1")
                .displayName("User One")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("user2")
                .displayName("User Two")
                .build();
    }

    @Test
    @DisplayName("Follow 1-way creates following and follower with status FOLLOWING")
    void testFollow_OneWay_Success() {
        when(userService.findByUserIdOrThrow(1L)).thenReturn(user1);
        when(userService.findByUserIdOrThrow(2L)).thenReturn(user2);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(2L, 1L)).thenReturn(false);

        FriendshipStatusResponseDto result = followingService.follow(1L, 2L);

        assertThat(result.getTargetUserId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(FriendshipStatus.FOLLOWING);
        verify(followingRepository).save(any(Following.class));
        verify(followerRepository).save(any(Follower.class));
    }

    @Test
    @DisplayName("Follow self throws exception")
    void testFollow_Self_ThrowsException() {
        assertThatThrownBy(() -> followingService.follow(1L, 1L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Cannot follow yourself");
    }

    @Test
    @DisplayName("Follow already followed user throws exception")
    void testFollow_AlreadyFollowing_ThrowsException() {
        when(userService.findByUserIdOrThrow(1L)).thenReturn(user1);
        when(userService.findByUserIdOrThrow(2L)).thenReturn(user2);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> followingService.follow(1L, 2L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Already following this user");
    }

    @Test
    @DisplayName("Follow when target is already following client establishes mutual FRIEND")
    void testFollow_Mutual_CreatesFriend() {
        when(userService.findByUserIdOrThrow(1L)).thenReturn(user1);
        when(userService.findByUserIdOrThrow(2L)).thenReturn(user2);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(2L, 1L)).thenReturn(true);
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(false);

        FriendshipStatusResponseDto result = followingService.follow(1L, 2L);

        assertThat(result.getTargetUserId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(FriendshipStatus.FRIEND);
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("Unfollow removes following, follower, and breaks friendship")
    void testUnfollow_Success() {
        Following following = Following.builder()
                .id(10L)
                .user(user1)
                .following(user2)
                .build();

        Friend friend = Friend.builder()
                .id(20L)
                .user1(user1)
                .user2(user2)
                .build();

        when(followingRepository.findByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(Optional.of(following));
        when(friendRepository.findByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(Optional.of(friend));

        followingService.unfollow(1L, 2L);

        verify(followingRepository).delete(following);
        verify(followerRepository).deleteByUser_IdAndFollower_Id(2L, 1L);
        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("Get following list with pagination")
    void testGetFollowingList() {
        Following following = Following.builder()
                .id(10L)
                .user(user1)
                .following(user2)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Following> page = new PageImpl<>(List.of(following));

        when(followingRepository.findByUser_Id(1L, pageable)).thenReturn(page);
        when(followingMapper.toFollowingResponseDto(following)).thenReturn(
                FollowingResponseDto.builder()
                        .id(10L)
                        .targetUser(UserResponseDto.builder().id(2L).username("user2").build())
                        .build()
        );

        Page<FollowingResponseDto> result = followingService.getFollowingList(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTargetUser().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Count following returns count")
    void testCountFollowing() {
        when(followingRepository.countByUser_Id(1L)).thenReturn(15L);

        Long count = followingService.countFollowing(1L);

        assertThat(count).isEqualTo(15L);
    }

}

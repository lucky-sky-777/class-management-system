package com.mezon.classmanagement.backend.friend;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.mapper.FollowerMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.entity.Following;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.mapper.FollowingMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.repository.FollowingRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendSummaryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatus;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendshipStatusResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity.Friend;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.mapper.FriendMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.repository.FriendRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    @Mock
    FriendRepository friendRepository;

    @Mock
    FollowerRepository followerRepository;

    @Mock
    FollowingRepository followingRepository;

    @Mock
    UserService userService;

    @Mock
    FriendMapper friendMapper;

    @Mock
    FollowerMapper followerMapper;

    @Mock
    FollowingMapper followingMapper;

    @InjectMocks
    FriendService friendService;

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
    @DisplayName("Send friend request successfully creates following and follower")
    void testSendFriendRequest_Success() {
        when(userService.findByUserIdOrThrow(1L)).thenReturn(user1);
        when(userService.findByUserIdOrThrow(2L)).thenReturn(user2);
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(false);
        when(followerRepository.existsByUser_IdAndFollower_Id(1L, 2L)).thenReturn(false);

        FriendshipStatusResponseDto result = friendService.sendFriendRequest(1L, 2L);

        assertThat(result.getTargetUserId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(FriendshipStatus.REQUEST_SENT);
        verify(followingRepository).save(any(Following.class));
        verify(followerRepository).save(any(Follower.class));
    }

    @Test
    @DisplayName("Send friend request to oneself throws exception")
    void testSendFriendRequest_ToSelf_ThrowsException() {
        assertThatThrownBy(() -> friendService.sendFriendRequest(1L, 1L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Cannot send friend request to yourself");
    }

    @Test
    @DisplayName("Send friend request when already friends throws exception")
    void testSendFriendRequest_AlreadyFriends_ThrowsException() {
        when(userService.findByUserIdOrThrow(1L)).thenReturn(user1);
        when(userService.findByUserIdOrThrow(2L)).thenReturn(user2);
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> friendService.sendFriendRequest(1L, 2L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Users are already friends");
    }

    @Test
    @DisplayName("Send friend request when reverse request exists auto-accepts friendship")
    void testSendFriendRequest_AutoMutualAccept() {
        when(userService.findByUserIdOrThrow(1L)).thenReturn(user1);
        when(userService.findByUserIdOrThrow(2L)).thenReturn(user2);
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(false);
        when(followerRepository.existsByUser_IdAndFollower_Id(1L, 2L)).thenReturn(true);

        FriendshipStatusResponseDto result = friendService.sendFriendRequest(1L, 2L);

        assertThat(result.getTargetUserId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(FriendshipStatus.FRIEND);
        verify(followerRepository).deleteByUser_IdAndFollower_Id(1L, 2L);
        verify(followingRepository).deleteByUser_IdAndFollowing_Id(2L, 1L);
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("Accept friend request successfully creates Friend and deletes request")
    void testAcceptFriendRequest_Success() {
        Follower follower = Follower.builder()
                .id(100L)
                .user(user1)
                .follower(user2)
                .receivedAt(Instant.now())
                .build();

        Friend savedFriend = Friend.builder()
                .id(200L)
                .user1(user1)
                .user2(user2)
                .friendedAt(Instant.now())
                .build();

        when(followerRepository.findByUser_IdAndFollower_Id(1L, 2L)).thenReturn(Optional.of(follower));
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(false);
        when(friendRepository.save(any(Friend.class))).thenReturn(savedFriend);
        when(friendMapper.toFriendResponseDto(eq(savedFriend), eq(1L))).thenReturn(
                FriendResponseDto.builder()
                        .id(200L)
                        .friend(UserResponseDto.builder().id(2L).username("user2").build())
                        .friendedAt(savedFriend.getFriendedAt())
                        .build()
        );

        FriendResponseDto response = friendService.acceptFriendRequest(1L, 2L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(200L);
        assertThat(response.getFriend().getId()).isEqualTo(2L);
        verify(followerRepository).delete(follower);
        verify(followingRepository).deleteByUser_IdAndFollowing_Id(2L, 1L);
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("Reject friend request deletes follower and following")
    void testRejectFriendRequest_Success() {
        Follower follower = Follower.builder()
                .id(100L)
                .user(user1)
                .follower(user2)
                .build();

        when(followerRepository.findByUser_IdAndFollower_Id(1L, 2L)).thenReturn(Optional.of(follower));

        friendService.rejectFriendRequest(1L, 2L);

        verify(followerRepository).delete(follower);
        verify(followingRepository).deleteByUser_IdAndFollowing_Id(2L, 1L);
    }

    @Test
    @DisplayName("Cancel friend request deletes following and follower")
    void testCancelFriendRequest_Success() {
        Following following = Following.builder()
                .id(101L)
                .user(user1)
                .following(user2)
                .build();

        when(followingRepository.findByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(Optional.of(following));

        friendService.cancelFriendRequest(1L, 2L);

        verify(followingRepository).delete(following);
        verify(followerRepository).deleteByUser_IdAndFollower_Id(2L, 1L);
    }

    @Test
    @DisplayName("Unfriend removes friend record")
    void testUnfriend_Success() {
        Friend friend = Friend.builder()
                .id(201L)
                .user1(user1)
                .user2(user2)
                .build();

        when(friendRepository.findByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(Optional.of(friend));

        friendService.unfriend(1L, 2L);

        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("Get friendship status returns correct enum for all cases")
    void testGetFriendshipStatus() {
        // Self
        assertThat(friendService.getFriendshipStatus(1L, 1L).getStatus()).isEqualTo(FriendshipStatus.SELF);

        // Friend
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(true);
        assertThat(friendService.getFriendshipStatus(1L, 2L).getStatus()).isEqualTo(FriendshipStatus.FRIEND);

        // Sent
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(true);
        assertThat(friendService.getFriendshipStatus(1L, 2L).getStatus()).isEqualTo(FriendshipStatus.REQUEST_SENT);

        // Received
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(false);
        when(followerRepository.existsByUser_IdAndFollower_Id(1L, 2L)).thenReturn(true);
        assertThat(friendService.getFriendshipStatus(1L, 2L).getStatus()).isEqualTo(FriendshipStatus.REQUEST_RECEIVED);

        // None
        when(followerRepository.existsByUser_IdAndFollower_Id(1L, 2L)).thenReturn(false);
        assertThat(friendService.getFriendshipStatus(1L, 2L).getStatus()).isEqualTo(FriendshipStatus.NONE);
    }

    @Test
    @DisplayName("Get summary returns correct counts")
    void testGetSummary() {
        when(friendRepository.countByUser1_IdOrUser2_Id(1L, 1L)).thenReturn(10L);
        when(followerRepository.countByUser_Id(1L)).thenReturn(3L);
        when(followingRepository.countByUser_Id(1L)).thenReturn(5L);

        FriendSummaryResponseDto summary = friendService.getSummary(1L);

        assertThat(summary.getFriendCount()).isEqualTo(10L);
        assertThat(summary.getReceivedRequestCount()).isEqualTo(3L);
        assertThat(summary.getSentRequestCount()).isEqualTo(5L);
    }

}

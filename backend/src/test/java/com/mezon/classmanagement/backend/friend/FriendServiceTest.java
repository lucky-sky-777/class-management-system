package com.mezon.classmanagement.backend.friend;

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
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.service.FriendService;
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

import java.time.Instant;
import java.util.List;
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
    @DisplayName("Get friendship status returns correct enum for all 5 states")
    void testGetFriendshipStatus_AllStates() {
        // 1. Self
        FriendshipStatusResponseDto selfStatus = friendService.getFriendshipStatus(1L, 1L);
        assertThat(selfStatus.getStatus()).isEqualTo(FriendshipStatus.SELF);

        // 2. Friend (mutual follow)
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(true);
        FriendshipStatusResponseDto friendStatus = friendService.getFriendshipStatus(1L, 2L);
        assertThat(friendStatus.getStatus()).isEqualTo(FriendshipStatus.FRIEND);

        // 3. Following (1-way)
        when(friendRepository.existsByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(true);
        when(followingRepository.existsByUser_IdAndFollowing_Id(2L, 1L)).thenReturn(false);
        FriendshipStatusResponseDto followingStatus = friendService.getFriendshipStatus(1L, 2L);
        assertThat(followingStatus.getStatus()).isEqualTo(FriendshipStatus.FOLLOWING);

        // 4. Follower (1-way reverse)
        when(followingRepository.existsByUser_IdAndFollowing_Id(1L, 2L)).thenReturn(false);
        when(followingRepository.existsByUser_IdAndFollowing_Id(2L, 1L)).thenReturn(true);
        FriendshipStatusResponseDto followerStatus = friendService.getFriendshipStatus(1L, 2L);
        assertThat(followerStatus.getStatus()).isEqualTo(FriendshipStatus.FOLLOWER);

        // 5. None
        when(followingRepository.existsByUser_IdAndFollowing_Id(2L, 1L)).thenReturn(false);
        FriendshipStatusResponseDto noneStatus = friendService.getFriendshipStatus(1L, 2L);
        assertThat(noneStatus.getStatus()).isEqualTo(FriendshipStatus.NONE);
    }

    @Test
    @DisplayName("Get summary returns correct counts for friend, follower, and following")
    void testGetSummary() {
        when(friendRepository.countByUser1_IdOrUser2_Id(1L, 1L)).thenReturn(10L);
        when(followerRepository.countByUser_Id(1L)).thenReturn(25L);
        when(followingRepository.countByUser_Id(1L)).thenReturn(18L);

        FriendSummaryResponseDto summary = friendService.getSummary(1L);

        assertThat(summary.getFriendCount()).isEqualTo(10L);
        assertThat(summary.getFollowerCount()).isEqualTo(25L);
        assertThat(summary.getFollowingCount()).isEqualTo(18L);
    }

    @Test
    @DisplayName("Get friend list with pagination and mapping")
    void testGetFriendList() {
        Friend friend = Friend.builder()
                .id(100L)
                .user1(user1)
                .user2(user2)
                .friendedAt(Instant.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Friend> page = new PageImpl<>(List.of(friend));

        when(friendRepository.findByUser1_IdOrUser2_Id(1L, 1L, pageable)).thenReturn(page);
        when(friendMapper.toFriendResponseDto(friend, 1L)).thenReturn(
                FriendResponseDto.builder()
                        .id(100L)
                        .friend(UserResponseDto.builder().id(2L).username("user2").build())
                        .build()
        );

        Page<FriendResponseDto> result = friendService.getFriendList(1L, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFriend().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Get mutual friends returns users in common")
    void testGetMutualFriends() {
        when(friendRepository.findMutualFriends(1L, 2L)).thenReturn(List.of(user1));
        when(friendMapper.toUserResponseDto(user1)).thenReturn(
                UserResponseDto.builder().id(1L).username("user1").build()
        );

        MutualFriendResponseDto result = friendService.getMutualFriends(1L, 2L);

        assertThat(result.getMutualCount()).isEqualTo(1L);
        assertThat(result.getMutualFriends()).hasSize(1);
    }

    @Test
    @DisplayName("Unfriend removes friendship and follow record")
    void testUnfriend_Success() {
        Friend friend = Friend.builder()
                .id(50L)
                .user1(user1)
                .user2(user2)
                .build();

        when(friendRepository.findByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(Optional.of(friend));

        friendService.unfriend(1L, 2L);

        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("Unfriend when friendship not found throws exception")
    void testUnfriend_NotFound_ThrowsException() {
        when(friendRepository.findByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.unfriend(1L, 2L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Friendship not found");
    }

}

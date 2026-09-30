package com.mezon.classmanagement.backend.friend;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.dto.FollowerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.mapper.FollowerMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository.FollowerRepository;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.service.FollowerService;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.repository.FollowingRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTest {

    @Mock
    FollowerRepository followerRepository;

    @Mock
    FollowingRepository followingRepository;

    @Mock
    FriendRepository friendRepository;

    @Mock
    FollowerMapper followerMapper;

    @InjectMocks
    FollowerService followerService;

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
    @DisplayName("Remove follower deletes follower, following, and breaks friendship")
    void testRemoveFollower_Success() {
        Follower follower = Follower.builder()
                .id(15L)
                .user(user1)
                .follower(user2)
                .build();

        Friend friend = Friend.builder()
                .id(25L)
                .user1(user1)
                .user2(user2)
                .build();

        when(followerRepository.findByUser_IdAndFollower_Id(1L, 2L)).thenReturn(Optional.of(follower));
        when(friendRepository.findByUser1_IdAndUser2_Id(1L, 2L)).thenReturn(Optional.of(friend));

        followerService.removeFollower(1L, 2L);

        verify(followerRepository).delete(follower);
        verify(followingRepository).deleteByUser_IdAndFollowing_Id(2L, 1L);
        verify(friendRepository).delete(friend);
    }

    @Test
    @DisplayName("Remove follower not found throws exception")
    void testRemoveFollower_NotFound_ThrowsException() {
        when(followerRepository.findByUser_IdAndFollower_Id(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followerService.removeFollower(1L, 2L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("Follower not found");
    }

    @Test
    @DisplayName("Get follower list with pagination")
    void testGetFollowerList() {
        Follower follower = Follower.builder()
                .id(15L)
                .user(user1)
                .follower(user2)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Follower> page = new PageImpl<>(List.of(follower));

        when(followerRepository.findByUser_Id(1L, pageable)).thenReturn(page);
        when(followerMapper.toFollowerResponseDto(follower)).thenReturn(
                FollowerResponseDto.builder()
                        .id(15L)
                        .requester(UserResponseDto.builder().id(2L).username("user2").build())
                        .build()
        );

        Page<FollowerResponseDto> result = followerService.getFollowerList(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRequester().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Count follower returns count")
    void testCountFollower() {
        when(followerRepository.countByUser_Id(1L)).thenReturn(20L);

        Long count = followerService.countFollower(1L);

        assertThat(count).isEqualTo(20L);
    }

}

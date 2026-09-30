package com.mezon.classmanagement.backend.domain_document.main.friend.following.repository;

import com.mezon.classmanagement.backend.domain_document.main.friend.following.entity.Following;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {

    Optional<Following> findByUser_IdAndFollowing_Id(Long userId, Long followingId);

    boolean existsByUser_IdAndFollowing_Id(Long userId, Long followingId);

    Page<Following> findByUser_Id(Long userId, Pageable pageable);

    List<Following> findByUser_Id(Long userId);

    Long countByUser_Id(Long userId);

    void deleteByUser_IdAndFollowing_Id(Long userId, Long followingId);

}

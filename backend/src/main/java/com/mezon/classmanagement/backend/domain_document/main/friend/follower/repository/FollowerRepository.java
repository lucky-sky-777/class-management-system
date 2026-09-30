package com.mezon.classmanagement.backend.domain_document.main.friend.follower.repository;

import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    Optional<Follower> findByUser_IdAndFollower_Id(Long userId, Long followerId);

    boolean existsByUser_IdAndFollower_Id(Long userId, Long followerId);

    Page<Follower> findByUser_Id(Long userId, Pageable pageable);

    List<Follower> findByUser_Id(Long userId);

    Long countByUser_Id(Long userId);

    void deleteByUser_IdAndFollower_Id(Long userId, Long followerId);

}

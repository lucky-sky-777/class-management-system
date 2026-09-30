package com.mezon.classmanagement.backend.domain_document.main.friend.friend.repository;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    boolean existsByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    void deleteByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    Page<Friend> findByUser1_IdOrUser2_Id(Long userId1, Long userId2, Pageable pageable);

    List<Friend> findByUser1_IdOrUser2_Id(Long userId1, Long userId2);

    Long countByUser1_IdOrUser2_Id(Long userId1, Long userId2);

    @Query("SELECT f FROM Friend f WHERE (f.user1.id = :userId OR f.user2.id = :userId) AND (" +
            "  LOWER(CASE WHEN f.user1.id = :userId THEN f.user2.displayName ELSE f.user1.displayName END) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "  LOWER(CASE WHEN f.user1.id = :userId THEN f.user2.username ELSE f.user1.username END) LIKE LOWER(CONCAT('%', :query, '%'))" +
            ")")
    Page<Friend> searchFriends(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id IN (" +
            "  SELECT CASE WHEN f1.user1.id = :userA THEN f1.user2.id ELSE f1.user1.id END " +
            "  FROM Friend f1 WHERE f1.user1.id = :userA OR f1.user2.id = :userA" +
            ") AND u.id IN (" +
            "  SELECT CASE WHEN f2.user1.id = :userB THEN f2.user2.id ELSE f2.user1.id END " +
            "  FROM Friend f2 WHERE f2.user1.id = :userB OR f2.user2.id = :userB" +
            ")")
    List<User> findMutualFriends(@Param("userA") Long userA, @Param("userB") Long userB);

    @Query("SELECT COUNT(u) FROM User u WHERE u.id IN (" +
            "  SELECT CASE WHEN f1.user1.id = :userA THEN f1.user2.id ELSE f1.user1.id END " +
            "  FROM Friend f1 WHERE f1.user1.id = :userA OR f1.user2.id = :userA" +
            ") AND u.id IN (" +
            "  SELECT CASE WHEN f2.user1.id = :userB THEN f2.user2.id ELSE f2.user1.id END " +
            "  FROM Friend f2 WHERE f2.user1.id = :userB OR f2.user2.id = :userB" +
            ")")
    Long countMutualFriends(@Param("userA") Long userA, @Param("userB") Long userB);

}

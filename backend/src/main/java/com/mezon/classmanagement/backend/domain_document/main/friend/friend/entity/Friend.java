package com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "friends",
		indexes = {
				@Index(
						name = "unique_index_friends_user_1_id_user_2_id",
						columnList = "user_1_id, user_2_id",
						unique = true
				),
				@Index(
						name = "unique_index_friends_user_2_id_user_1_id",
						columnList = "user_2_id, user_1_id",
						unique = true
				)
		}
)
public class Friend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_1_id", nullable = false)
	User user1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_2_id", nullable = false)
	User user2;

	@Column(name = "friended_at", nullable = false, insertable = false, updatable = false)
	Instant friendedAt;

}
package com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity;

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
		name = "followers",
		indexes = {
				@Index(
						name = "unique_index_followers_user_id_follower_user_id",
						columnList = "user_id, follower_user_id",
						unique = true
				)
		}
)
public class Follower {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_user_id", nullable = false)
	User follower;

	@Column(name = "received_at", nullable = false, insertable = false, updatable = false)
	Instant receivedAt;

}
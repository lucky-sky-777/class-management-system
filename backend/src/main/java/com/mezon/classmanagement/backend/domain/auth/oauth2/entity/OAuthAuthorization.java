package com.mezon.classmanagement.backend.domain.auth.oauth2.entity;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "oauth_authorizations")
public class OAuthAuthorization {

	@Id
	@Column(name = "code", nullable = false)
	String code;

	@Column(name = "origin", nullable = false)
	String origin;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	User.Provider provider;

	@Column(name = "access_token", nullable = false)
	String accessToken;

	@Column(name = "refresh_token", nullable = false)
	String refreshToken;

	@Column(name = "expiry_date", nullable = false)
	Instant expiryDate;

	@Column(name = "used", nullable = false)
	Boolean used;

	@PrePersist
	public void prePersist() {
		if (expiryDate == null) {
			expiryDate = Instant.now().plusSeconds(60);
		}
		if (used == null) {
			used = false;
		}
	}

}
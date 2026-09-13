package com.mezon.classmanagement.backend.domain.auth.entity;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl.Vector1536Converter;
import com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl.Vector3072Converter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "users",
		indexes = {
				@Index(
						name = "index_users_username",
						columnList = "username"
				),
				@Index(
						name = "index_users_phone",
						columnList = "phone"
				),
				@Index(
						name = "index_users_email",
						columnList = "email"
				)
		}
)
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "provider", nullable = false)
	Provider provider;

	public enum Provider {
		GOOGLE,
		MEZON,
		INTERNAL
	}

	@Column(name = "provider_id", nullable = true, unique = true)
	String providerId;

	@Column(name = "username", nullable = false, unique = true)
	String username;

	@Column(name = "password_hash", nullable = true)
	String passwordHash;

	@Column(name = "display_name", nullable = true)
	String displayName;

	@Column(name = "avatar_url", nullable = true)
	String avatarUrl;

	@Column(name = "phone", nullable = true)
	String phone;

	@Column(name = "email", nullable = true)
	String email;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "status", nullable = false)
	Status status;

	public enum Status {
		NORMAL,
		WARNING,
		BANNED
	}

	@Column(name = "school", nullable = true)
	String school;

	@Column(name = "major", nullable = true)
	String major;

	@JdbcTypeCode(value = SqlTypes.VECTOR)
	@Convert(converter = Vector1536Converter.class)
	@Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
	Vector1536 embedding;

	@Column(name = "joined_at", nullable = false, insertable = false, updatable = false)
	Instant joinedAt;

	@PrePersist
	public void prePersist() {
		if (provider == null) {
			provider = Provider.INTERNAL;
		}
		if (status == null) {
			status = Status.NORMAL;
		}
		if (embedding == null) {
			embedding = new Vector1536(new float[Vector1536.DIMENSION]);
		}
	}

	@NullMarked
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public @Nullable String getPassword() {
		return this.passwordHash;
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}

	public static User create(Long id) {
		return User.builder()
				.id(id)
				.build();
	}

}
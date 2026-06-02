package com.mezon.classmanagement.backend.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.Instant;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "index_refresh_tokens_jti", columnList = "jti"),
                @Index(name = "index_refresh_tokens_expiry_date", columnList = "expiry_date")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "jti", nullable = false, unique = true)
    String jti;

    @Column(name = "expiry_date", nullable = false)
    Instant expiryDate;

    @Column(name = "revoked", nullable = false)
    Boolean revoked;

    @PrePersist
    public void prePersist() {
        revoked = false;
    }

}
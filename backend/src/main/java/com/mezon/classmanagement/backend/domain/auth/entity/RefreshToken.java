package com.mezon.classmanagement.backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
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
        name = "refresh_token",
        indexes = {
                @Index(name = "index_refresh_tokens_jti", columnList = "jti"),
                @Index(name = "index_refresh_tokens_expiry_date", columnList = "expiry_date")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String jti;

    @Column(name = "expiry_date", nullable = false)
    Instant expiryDate;

    @Column(nullable = false)
    boolean revoked = false;
}
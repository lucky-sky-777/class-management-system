package com.mezon.classmanagement.backend.domain.auth.oauth2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.util.EmailProcessor;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class GoogleUser implements OAuthUser {

	@Override
	public User.Provider getProvider() {
		return User.Provider.GOOGLE;
	}

	@JsonProperty(value = "sub")
	String sub;

	@Override
	public String getCustomUsername() {
		return EmailProcessor.extractAndClean(email) + "-" + User.Provider.GOOGLE + "-" + System.currentTimeMillis();
	}

	@JsonProperty(value = "name")
	String displayName;

	@JsonProperty(value = "picture")
	String avatarUrl;

	@JsonProperty(value = "email")
	String email;

}
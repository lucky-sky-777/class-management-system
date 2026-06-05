package com.mezon.classmanagement.backend.domain.auth.oauth2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.util.EmailProcessor;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class MezonUser implements OAuthUser {

	@JsonProperty("user_id")
	String userId;

	@JsonProperty("mezon_id")
	String mezonId;

	@Override
	public User.Provider getProvider() {
		return User.Provider.MEZON;
	}

	@JsonProperty("sub")
	String sub;

	@JsonProperty("username")
	String username;

	@Override
	public String getCustomUsername() {
		return EmailProcessor.extractAndClean(email) + "-" + User.Provider.MEZON + "-" + System.currentTimeMillis();
	}

	@JsonProperty("display_name")
	String displayName;

	@JsonProperty("avatar")
	String avatarUrl;

	@JsonProperty("email")
	String email;

}
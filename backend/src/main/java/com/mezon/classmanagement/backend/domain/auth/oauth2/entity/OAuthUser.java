package com.mezon.classmanagement.backend.domain.auth.oauth2.entity;

import com.mezon.classmanagement.backend.domain.auth.entity.User;

public sealed interface OAuthUser permits GoogleUser, MezonUser {

	User.Provider getProvider();
	String getSub();
	String getCustomUsername();
	String getDisplayName();
	String getAvatarUrl();
	String getEmail();

}
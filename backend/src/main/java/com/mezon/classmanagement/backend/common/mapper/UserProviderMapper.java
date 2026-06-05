package com.mezon.classmanagement.backend.common.mapper;

import com.mezon.classmanagement.backend.domain.auth.entity.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class UserProviderMapper {

	private static final Map<String, User.Provider> mappings = new HashMap<>() {{
		put(User.Provider.GOOGLE.name().toLowerCase(Locale.ROOT), User.Provider.GOOGLE);
		put(User.Provider.MEZON.name().toLowerCase(Locale.ROOT), User.Provider.MEZON);
		put(User.Provider.INTERNAL.name().toLowerCase(Locale.ROOT), User.Provider.INTERNAL);
	}};

	public static User.Provider toUserProvider(String userProvider) {
		return mappings.get(userProvider.toLowerCase(Locale.ROOT));
	}

}
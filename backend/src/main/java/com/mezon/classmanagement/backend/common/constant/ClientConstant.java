package com.mezon.classmanagement.backend.common.constant;

import java.util.ArrayList;
import java.util.List;

public final class ClientConstant {

	public static final List<String> ALLOWED_ORIGIN_LIST = new ArrayList<>(
			List.of(
					"http://localhost:5173",
					"https://iclass.website",
					"https://class-management-system-frontend.fly.dev"
			)
	);

}
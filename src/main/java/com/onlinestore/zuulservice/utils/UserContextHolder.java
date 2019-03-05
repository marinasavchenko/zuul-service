package com.onlinestore.zuulservice.utils;

import org.springframework.util.Assert;

public class UserContextHolder {
	private static final ThreadLocal<UserContext> threadLocalUserContext = new ThreadLocal<>();

	public static final UserContext getContext() {
		UserContext userContext = threadLocalUserContext.get();

		if (userContext == null) {
			userContext = createEmptyContext();
			threadLocalUserContext.set(userContext);
		}
		return threadLocalUserContext.get();
	}

	public static final void setContext(UserContext userContext) {
		Assert.notNull(userContext, "Only non-null UserContext instances are permitted");
		threadLocalUserContext.set(userContext);
	}

	public static final UserContext createEmptyContext() {
		return new UserContext();
	}
}
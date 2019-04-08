package com.onlinestore.zuulservice.utils;

import org.springframework.util.Assert;

/**
 * Class that used to store UserContext in ThreadLocal class.
 */
public class UserContextHolder {
	/**
	 * Contextual information {@code UserContext} saved in {@code ThreadLocal}.
	 */
	private static final ThreadLocal<UserContext> threadLocalUserContext = new ThreadLocal<>();

	/**
	 * Retrieves UserContext for consumption.
	 *
	 * @return contextual information from threadLocal
	 */
	public static final UserContext getContext() {
		UserContext userContext = threadLocalUserContext.get();

		if (userContext == null) {
			userContext = createEmptyContext();
			threadLocalUserContext.set(userContext);
		}
		return threadLocalUserContext.get();
	}

	/**
	 * Stores a UserContext object in a ThreadLocal variable specific to the thread being run.
	 *
	 * @param userContext contextual information
	 */
	public static final void setContext(UserContext userContext) {
		Assert.notNull(userContext, "Only non-null UserContext instances are permitted");
		threadLocalUserContext.set(userContext);
	}

	/**
	 * Creates empty {@code UserContext}.
	 *
	 * @return empty {@code UserContext}
	 */
	public static final UserContext createEmptyContext() {
		return new UserContext();
	}
}
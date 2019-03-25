package com.onlinestore.zuulservice.utils;

import org.springframework.stereotype.Component;

/**
 * Contextual information scrapped from incoming HTTP request.
 */
@Component
public class UserContext {
	/**
	 * CORRELATION_ID HTTP request header.
	 */
	public static final String CORRELATION_ID = "onlst-correlation-id";
	/**
	 * AUTH_TOKEN HTTP request header.
	 */
	public static final String AUTH_TOKEN = "onlst-auth-token";
	/**
	 * USER_ID HTTP request header.
	 */
	public static final String USER_ID = "onlst-user-id";
	/**
	 * CUSTOMER_ID HTTP request header.
	 */
	public static final String CUSTOMER_ID = "onlst-customer-id";

	/**
	 * Correlation Id used to trace the chain of events.
	 */
	private String correlationId = new String();
	/**
	 * Auth token.
	 */
	private String authToken = new String();
	/**
	 * User id.
	 */
	private String userId = new String();
	/**
	 * Unique id of the customer.
	 */
	private String customerId = new String();

	/**
	 * Constructs new empty {@code UserContext} instance.
	 */
	public UserContext() {
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getUserId() {
		return userId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Override
	public String toString() {
		return "UserContext{" +
				"correlationId='" + correlationId + '\'' +
				", authToken='" + authToken + '\'' +
				", userId='" + userId + '\'' +
				", customerId='" + customerId + '\'' +
				'}';
	}
}
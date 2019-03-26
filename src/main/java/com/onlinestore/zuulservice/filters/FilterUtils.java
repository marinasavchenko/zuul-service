package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

/**
 * {@code FilterUtils} class is used to encapsulate common functionality used by all filters of this service.
 */
//TODO: refactor repeated code.
@Component
public class FilterUtils {
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
	 * Pre filter type.
	 */
	public static final String PRE_FILTER_TYPE = "pre";
	/**
	 * Post filter type.
	 */
	public static final String POST_FILTER_TYPE = "post";
	/**
	 * Route filter type.
	 */
	public static final String ROUTE_FILTER_TYPE = "route";

	/**
	 * Gets correlation id.
	 * <p>
	 * Checks whether correlation id is set on the HTTP Headers for the incoming request.
	 * If it isn’t there, check the ZuulRequestHeaders.
	 *
	 * @return correlation id
	 */
	public String getCorrelationId() {
		RequestContext currentContext = RequestContext.getCurrentContext();

		if (currentContext.getRequest().getHeader(CORRELATION_ID) != null) {
			return currentContext.getRequest().getHeader(CORRELATION_ID);
		} else {
			return currentContext.getZuulRequestHeaders().get(CORRELATION_ID);
		}
	}

	/**
	 * Adds correlation id value to the HTTP request headers.
	 * <p>
	 * {@code addZuulRequestHeader} maintains a separate map of HTTP headers.
	 * The data will be merged, when the target service is invoked by your Zuul server
	 *
	 * @param correlationId
	 */
	public void setCorrelationId(String correlationId) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader(CORRELATION_ID, correlationId);
	}

	/**
	 * Gets customer id.
	 *
	 * @return customer id
	 */
	public final String getCustomerId() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		if (currentContext.getRequest().getHeader(CUSTOMER_ID) != null) {
			return currentContext.getRequest().getHeader(CUSTOMER_ID);
		} else {
			return currentContext.getZuulRequestHeaders().get(CUSTOMER_ID);
		}
	}

	/**
	 * Sets customer id.
	 *
	 * @param customerId
	 */
	public void setCustomerId(String customerId) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader(CUSTOMER_ID, customerId);
	}

	/**
	 * Gets user id.
	 *
	 * @return user id
	 */
	public final String getUserId() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		if (currentContext.getRequest().getHeader(USER_ID) != null) {
			return currentContext.getRequest().getHeader(USER_ID);
		} else {
			return currentContext.getZuulRequestHeaders().get(USER_ID);
		}
	}

	/**
	 * Sets user id.
	 *
	 * @param userId
	 */
	public void setUserId(String userId) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader(USER_ID, userId);
	}

	/**
	 * Gets authentication token.
	 *
	 * @return auth token
	 */
	public final String getAuthToken() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		return currentContext.getRequest().getHeader(AUTH_TOKEN);
	}

	/**
	 * Gets id of a service.
	 *
	 * @return id of a service
	 */
	public String getServiceId() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		if (currentContext.get("serviceId") == null) return "";
		return currentContext.get("serviceId").toString();
	}
}

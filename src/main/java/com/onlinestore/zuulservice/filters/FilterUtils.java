package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

@Component
public class FilterUtils {
	public static final String CORRELATION_ID = "onlst-correlation-id";
	public static final String AUTH_TOKEN = "onlst-auth-token";
	public static final String USER_ID = "onlst-user-id";
	public static final String CUSTOMER_ID = "onlst-customer-id";
	public static final String PRE_FILTER_TYPE = "pre";
	public static final String POST_FILTER_TYPE = "post";
	public static final String ROUTE_FILTER_TYPE = "route";

	public String getCorrelationId() {
		RequestContext currentContext = RequestContext.getCurrentContext();

		if (currentContext.getRequest().getHeader(CORRELATION_ID) != null) {
			return currentContext.getRequest().getHeader(CORRELATION_ID);
		} else {
			return currentContext.getZuulRequestHeaders().get(CORRELATION_ID);
		}
	}

	public void setCorrelationId(String correlationId) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader(CORRELATION_ID, correlationId);
	}

	public final String getCustomerId() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		if (currentContext.getRequest().getHeader(CUSTOMER_ID) != null) {
			return currentContext.getRequest().getHeader(CUSTOMER_ID);
		} else {
			return currentContext.getZuulRequestHeaders().get(CUSTOMER_ID);
		}
	}

	public void setCustomerId(String customerId) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader(CUSTOMER_ID, customerId);
	}

	public final String getUserId() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		if (currentContext.getRequest().getHeader(USER_ID) != null) {
			return currentContext.getRequest().getHeader(USER_ID);
		} else {
			return currentContext.getZuulRequestHeaders().get(USER_ID);
		}
	}

	public void setUserId(String userId) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader(USER_ID, userId);
	}

	public final String getAuthToken() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		return currentContext.getRequest().getHeader(AUTH_TOKEN);
	}

	public String getServiceId() {
		RequestContext currentContext = RequestContext.getCurrentContext();
		if (currentContext.get("serviceId") == null) return "";
		return currentContext.get("serviceId").toString();
	}
}

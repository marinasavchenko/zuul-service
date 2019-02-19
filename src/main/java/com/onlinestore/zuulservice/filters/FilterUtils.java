package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.context.RequestContext;

public class FilterUtils {
	public static final String CORRELATION_ID = "onlst-correlation-id";
	public static final String AUTH_TOKEN = "onlst-auth-token";
	public static final String USER_ID = "onlst-user-id";
	public static final String CUSTOMER_ID = "onlst-customer-id";
	public static final String PRE_FILTER_TYPE = "pre";

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
}

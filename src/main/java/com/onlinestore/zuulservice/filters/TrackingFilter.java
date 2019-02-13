package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class TrackingFilter extends ZuulFilter {
	public static final String PRE_FILTER_TYPE = "pre";
	private static final int FILTER_ORDER = 1;
	private static final boolean SHOULD_FILTER = true;

	@Override
	public String filterType() {
		return PRE_FILTER_TYPE;
	}

	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}

	@Override
	public boolean shouldFilter() {
		return SHOULD_FILTER;
	}

	@Override
	public Object run() throws ZuulException {
		return null;
	}
}

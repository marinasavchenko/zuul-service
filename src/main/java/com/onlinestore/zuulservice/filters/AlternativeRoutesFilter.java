package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class AlternativeRoutesFilter extends ZuulFilter {
	private static final int FILTER_ORDER = 1;
	private static final boolean SHOULD_FILTER = true;

	private FilterUtils filterUtils;
	private RestTemplate restTemplate;

	@Autowired
	public AlternativeRoutesFilter(FilterUtils filterUtils, RestTemplate restTemplate) {
		this.filterUtils = filterUtils;
		this.restTemplate = restTemplate;
	}

	@Override
	public String filterType() {
		return filterUtils.ROUTE_FILTER_TYPE;
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

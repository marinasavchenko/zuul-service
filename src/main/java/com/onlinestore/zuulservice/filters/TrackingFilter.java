package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackingFilter extends ZuulFilter {
	public static final String PRE_FILTER_TYPE = "pre";
	private static final int FILTER_ORDER = 1;
	private static final boolean SHOULD_FILTER = true;

	private FilterUtils filterUtils;

	@Autowired
	public TrackingFilter(FilterUtils filterUtils) {
		this.filterUtils = filterUtils;
	}

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

	private boolean isCorrelationIdPresent(){
		return filterUtils.getCorrelationId()!=null;
	}

	private String generateCorrelationId(){
		return java.util.UUID.randomUUID().toString();
	}

	@Override
	public Object run() throws ZuulException {
		if (!isCorrelationIdPresent()){
			filterUtils.setCorrelationId(generateCorrelationId());
		}
		RequestContext currentContext = RequestContext.getCurrentContext();
		return null;
	}
}

package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Zuul post filter used to inject the correlation ID back into the HTTP response headers
 * being passed back to the caller of the service.
 */
@Component
public class ResponseFilter extends ZuulFilter {
	/**
	 * {@value #FILTER_ORDER} is the default value of filter order.
	 */
	private static final int FILTER_ORDER = 1;
	/**
	 * {@value #SHOULD_FILTER} is the default value of filter activeness.
	 */
	private static final boolean SHOULD_FILTER = true;
	/**
	 * {@code FilterUtils} class that encapsulates common methods used by filters.
	 */
	private FilterUtils filterUtils;

	/**
	 * Constructs new {@code ResponseFilter} instance.
	 *
	 * @param filterUtils
	 */
	@Autowired
	public ResponseFilter(FilterUtils filterUtils) {
		this.filterUtils = filterUtils;
	}

	/**
	 * Tels Zuul what type of filter is used (post).
	 *
	 * @return type of filter
	 */
	@Override
	public String filterType() {
		return FilterUtils.POST_FILTER_TYPE;
	}

	/**
	 * Indicates order of filters Zuul should send requests through.
	 *
	 * @return value of order
	 */
	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}

	/**
	 * Indicates whether or not the filter should be active.
	 *
	 * @return {@code true} when filter should be active
	 */
	@Override
	public boolean shouldFilter() {
		return SHOULD_FILTER;
	}

	/**
	 * Takes the correlation ID that was passed in on the original HTTP request and inject it into the response.
	 *
	 * @return
	 * @throws ZuulException
	 */
	@Override
	public Object run() throws ZuulException {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.getResponse().addHeader(FilterUtils.CORRELATION_ID, filterUtils.getCorrelationId());
		return null;
	}
}

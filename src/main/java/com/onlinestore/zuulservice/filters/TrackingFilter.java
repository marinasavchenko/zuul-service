package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Zuul pre filter used to inspect all incoming requests to the gateway and determine whether there’s an HTTP header
 * correlation id present in the request.
 */
@Component
public class TrackingFilter extends ZuulFilter {
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
	 * Constructs new {@code TrackingFilter} instance.
	 *
	 * @param filterUtils
	 */
	@Autowired
	public TrackingFilter(FilterUtils filterUtils) {
		this.filterUtils = filterUtils;
	}

	/**
	 * Tels Zuul what type of filter is used (pre).
	 *
	 * @return type of filter
	 */
	@Override
	public String filterType() {
		return FilterUtils.PRE_FILTER_TYPE;
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
	 * Checks whether correlation id is present.
	 *
	 * @return {@code true}, if correlation id is present
	 */
	private boolean isCorrelationIdPresent() {
		return filterUtils.getCorrelationId() != null;
	}

	/**
	 * Generates correlation id value.
	 *
	 * @return correlation id value
	 */
	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}

	/**
	 * Executes every time service passes through filter.
	 * Checks whether correlation Id is present, if it is not, generates value and sets it to HTTP header.
	 *
	 * @return
	 * @throws ZuulException
	 */
	@Override
	public Object run() throws ZuulException {
		if (!isCorrelationIdPresent()) {
			filterUtils.setCorrelationId(generateCorrelationId());
		}
		RequestContext currentContext = RequestContext.getCurrentContext();
		return null;
	}
}

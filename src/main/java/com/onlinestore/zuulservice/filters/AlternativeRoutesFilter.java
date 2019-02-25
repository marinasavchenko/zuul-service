package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import com.onlinestore.zuulservice.model.RouteRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class AlternativeRoutesFilter extends ZuulFilter {
	private static final String ROUTE_RECORD_URI = "http://alternativeroutesservice/v1/route/records/{serviceName}";
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

	private RouteRecord getRouteRecordInfo(String serviceName) {
		ResponseEntity<RouteRecord> responseEntity;
		try {
			responseEntity = restTemplate.exchange(ROUTE_RECORD_URI, HttpMethod.GET, null, RouteRecord.class, serviceName);
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) return null;
			throw exception;
		}
		return responseEntity.getBody();
	}
}

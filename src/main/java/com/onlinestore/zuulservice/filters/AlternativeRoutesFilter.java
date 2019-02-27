package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.onlinestore.zuulservice.model.RouteRecord;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
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
		RequestContext currentContext = RequestContext.getCurrentContext();
		RouteRecord routeRecord = getRouteRecordInfo(filterUtils.getServiceId());

		if (routeRecord != null && shouldUseAlternativeRoute(routeRecord)) {
			String route = buildRoute(
					currentContext.getRequest().getRequestURI(),
					routeRecord.getEndpoint(),
					currentContext.get("serviceId").toString());

		}

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

	public boolean shouldUseAlternativeRoute(RouteRecord routeRecord) {
		Random random = new Random();

		if (routeRecord.getActive().equals("NO")) return false;

		int randomTen = random.nextInt((10 - 1) + 1) + 1;

		if (routeRecord.getWeight() < randomTen) return true;

		return false;
	}

	private String buildRoute(String oldEndpoint, String newEndpoint, String serviceName) {
		int index = oldEndpoint.indexOf(serviceName);

		String plainRoute = oldEndpoint.substring(index + serviceName.length());
		return String.format("%s/%s", newEndpoint, plainRoute);
	}

	private ProxyRequestHelper getProxyRequestHelper() {
		return new ProxyRequestHelper();
	}

	private String getRequestMethod(HttpServletRequest request) {
		String requestMethod = request.getMethod();
		return requestMethod.toUpperCase();
	}

	private HttpHost getHttpHost(URL url) {
		HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		return httpHost;
	}


	private Header[] convertToBasicHeaders(MultiValueMap<String, String> headers) {
		List<Header> headerList = new ArrayList<>();
		for (String name : headers.keySet()) {
			for (String value : headers.get(name)) {
				headerList.add(new BasicHeader(name, value));
			}
		}
		return headerList.toArray(new BasicHeader[0]);
	}
}

package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.onlinestore.zuulservice.model.RouteRecord;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
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

	//TODO: refactor
	private HttpResponse invokeAlternativeService(HttpClient httpclient, String verb, String route,
	                                              HttpServletRequest request, MultiValueMap<String, String> headers,
	                                              MultiValueMap<String, String> params, InputStream requestEntity)
			throws Exception {

		URL url = new URL(route);
		HttpHost httpHost = getHttpHost(url);
		HttpRequest httpRequest;
		int contentLength = request.getContentLength();
		ContentType contentType = null;
		if (request.getContentType() != null) {
			contentType = ContentType.create(request.getContentType());
		}

		InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength, contentType);
		switch (verb.toUpperCase()) {
			case "POST":
				HttpPost httpPost = new HttpPost(route);
				httpRequest = httpPost;
				httpPost.setEntity(entity);
				break;
			case "PUT":
				HttpPut httpPut = new HttpPut(route);
				httpRequest = httpPut;
				httpPut.setEntity(entity);
				break;
			case "PATCH":
				HttpPatch httpPatch = new HttpPatch(route);
				httpRequest = httpPatch;
				httpPatch.setEntity(entity);
				break;
			default:
				httpRequest = new BasicHttpRequest(verb, route);

		}
		try {
			httpRequest.setHeaders(convertToBasicHeaders(headers));
			HttpResponse zuulResponse = httpclient.execute(httpHost, httpRequest);

			return zuulResponse;
		} finally {
		}
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

	private MultiValueMap<String, String> convertToStringHeaders(Header[] headers) {
		MultiValueMap<String, String> stringHeaders = new LinkedMultiValueMap<>();
		for (Header header : headers) {
			String name = header.getName();
			if (!stringHeaders.containsKey(name)) {
				stringHeaders.put(name, new ArrayList<>());
			}
			stringHeaders.get(name).add(header.getValue());
		}
		return stringHeaders;
	}

	private void forward(String route) {
		RequestContext currentContext = RequestContext.getCurrentContext();
		HttpServletRequest request = currentContext.getRequest();
		ProxyRequestHelper requestHelper = getProxyRequestHelper();

		MultiValueMap<String, String> headers = requestHelper.buildZuulRequestHeaders(request);
		MultiValueMap<String, String> params = requestHelper.buildZuulRequestQueryParams(request);
		String verb = request.getMethod().toUpperCase();

		InputStream requestEntity = getRequestBody(request);

		requestHelper.addIgnoredHeaders();
		CloseableHttpClient httpClient = null;
		HttpResponse response = null;

		try {
			httpClient = HttpClients.createDefault();
			response = invokeAlternativeService(httpClient, verb, route, request, headers, params, requestEntity);
			setResponse(requestHelper, response);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setResponse(ProxyRequestHelper requestHelper, HttpResponse response) throws IOException {
		int statusCode = response.getStatusLine().getStatusCode();
		InputStream content = null;
		if (response.getEntity() != null) {
			content = response.getEntity().getContent();
		}
		MultiValueMap<String, String> headers = convertToStringHeaders(response.getAllHeaders());
		requestHelper.setResponse(statusCode, content, headers);
	}

	private InputStream getRequestBody(HttpServletRequest request) {
		InputStream requestEntity = null;
		try {
			requestEntity = request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return requestEntity;
	}

}

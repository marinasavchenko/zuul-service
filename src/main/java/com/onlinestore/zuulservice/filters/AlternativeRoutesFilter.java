package com.onlinestore.zuulservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.onlinestore.zuulservice.domain.RouteRecord;
import com.onlinestore.zuulservice.utils.RandomGenerator;
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
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Zuul route filter used to perform dynamic routing based on Eureka service ID to do A/B testing between different
 * versions of the same service.
 * Routes users call to the alternative customer service or to the customer service defined
 * in the Zuul route mappings.
 */
@Component
public class AlternativeRoutesFilter extends ZuulFilter {
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

	private RouteRecordProxy routeRecordProxy;
	/**
	 * Generator of random int.
	 */
	private RandomGenerator randomGenerator;

	/**
	 * Constructs new {@code AlternativeRoutesFilter} instance.
	 *
	 * @param filterUtils
	 * @param routeRecordProxy
	 * @param randomGenerator
	 */
	@Autowired
	public AlternativeRoutesFilter(FilterUtils filterUtils, RouteRecordProxy routeRecordProxy, RandomGenerator randomGenerator) {
		this.filterUtils = filterUtils;
		this.routeRecordProxy = routeRecordProxy;
		this.randomGenerator = randomGenerator;
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

	/**
	 * Invokes alternativeroutesservice, determines whether to use alternative route, if yes forwards the route.
	 *
	 * @return
	 * @throws ZuulException
	 */
	@Override
	public Object run() throws ZuulException {
		RequestContext currentContext = RequestContext.getCurrentContext();
		RouteRecord routeRecord = routeRecordProxy.getRouteRecordInfo(filterUtils.getServiceId()).get();

		if (routeRecord != null && shouldUseAlternativeRoute(routeRecord)) {
			String route = buildRoute(
					currentContext.getRequest().getRequestURI(),
					routeRecord.getEndpoint(),
					currentContext.get("serviceId").toString());
			forward(route);
		}
		return null;
	}

	/**
	 * Determines randomly whether to use alternative service route.
	 *
	 * @param routeRecord
	 * @return true if route record status is active and random number (between 1 and 10) is more than route weight
	 */
	public boolean shouldUseAlternativeRoute(RouteRecord routeRecord) {
		if (routeRecord.getActiveStatus().equals("NO")) return false;
		if (routeRecord.getWeight() < randomGenerator.getRandomInt()) return true;
		return false;
	}

	private String buildRoute(String oldEndpoint, String newEndpoint, String serviceName) {
		int index = oldEndpoint.indexOf(serviceName);

		String plainRoute = oldEndpoint.substring(index + serviceName.length());
		return String.format("%s/%s", newEndpoint, plainRoute);
	}

	/**
	 * Takes the response back from the target service and sets it on the HTTP request context used by Zuul.
	 * The result of {@code invokeAlternativeService} call is saved back to the Zuul server
	 * through the {@code setResponse} helper method.
	 *
	 * @param route
	 */
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

	private ProxyRequestHelper getProxyRequestHelper() {
		return new ProxyRequestHelper();
	}

	/**
	 * Returns request body from request.
	 *
	 * @param request
	 * @return request body
	 */
	private InputStream getRequestBody(HttpServletRequest request) {
		InputStream requestEntity = null;
		try {
			requestEntity = request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return requestEntity;
	}

	/**
	 * Invokes alternative service.
	 *
	 * @param httpclient
	 * @param verb
	 * @param route
	 * @param request
	 * @param headers
	 * @param params
	 * @param requestEntity
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * Sets the response from target service on the HTTP request context used by Zuul
	 *
	 * @param requestHelper
	 * @param response      response from target service
	 * @throws IOException
	 */
	private void setResponse(ProxyRequestHelper requestHelper, HttpResponse response) throws IOException {
		int statusCode = response.getStatusLine().getStatusCode();
		InputStream content = null;
		if (response.getEntity() != null) {
			content = response.getEntity().getContent();
		}
		MultiValueMap<String, String> headers = convertToStringHeaders(response.getAllHeaders());
		requestHelper.setResponse(statusCode, content, headers);
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


}

package com.onlinestore.zuulservice.filters;

import com.onlinestore.zuulservice.domain.RouteRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Class for checking whether routing record exists.
 */
@Component
public class RouteRecordProxy {
	/**
	 * {@value #ROUTE_RECORD_URI} is the URI of route records in alternative routes service.
	 */
	private static final String ROUTE_RECORD_URI = "http://localhost:8080/v1/route/records/{serviceName}";

	/**
	 * Spring Rest template.
	 */
	private RestTemplate restTemplate;

	@Autowired
	public RouteRecordProxy(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Calls alternativeroutesservice microservice to check whether routing record exists.
	 *
	 * @param serviceName name of the service
	 * @return Optional of RouteRecord.
	 */
	public Optional<RouteRecord> getRouteRecordInfo(String serviceName) {
		ResponseEntity<RouteRecord> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(ROUTE_RECORD_URI, HttpMethod.GET, null, RouteRecord.class, serviceName);
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) return Optional.empty();
		}
		return Optional.ofNullable(responseEntity.getBody());
	}
}

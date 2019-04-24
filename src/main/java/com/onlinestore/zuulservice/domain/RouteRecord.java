package com.onlinestore.zuulservice.domain;

/**
 * Route Record class for retrieving and storing routes information from alternative routes service.
 */
public class RouteRecord {
	/**
	 * Name of the service.
	 */
	String serviceName;
	/**
	 * Active status of route record. Can be "YES" or "NO".
	 */
	String activeStatus;
	/**
	 * Endpoint of the service.
	 */
	String endpoint;
	/**
	 * Weight of the route.
	 */
	Integer weight;

	/**
	 * Constructs new empty {@code RouteRecord} instance.
	 */
	public RouteRecord() {
	}

	public RouteRecord(String serviceName, String activeStatus, String endpoint, Integer weight) {
		this.serviceName = serviceName;
		this.activeStatus = activeStatus;
		this.endpoint = endpoint;
		this.weight = weight;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getActiveStatus() {
		return activeStatus;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}

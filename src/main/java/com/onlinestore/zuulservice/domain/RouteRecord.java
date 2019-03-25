package com.onlinestore.zuulservice.domain;

public class RouteRecord {

	String serviceName;
	/**
	 * Active status of route record. Can be "YES" or "NO".
	 */
	String activeStatus;
	String endpoint;
	Integer weight;

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

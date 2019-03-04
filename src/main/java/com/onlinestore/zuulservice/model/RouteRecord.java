package com.onlinestore.zuulservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "routerecords")
public class RouteRecord {
	@Column
	String serviceName;
	@Column
	String active;
	@Column
	String endpoint;
	@Column
	Integer weight;

	public String getServiceName() {
		return serviceName;
	}

	public String getActive() {
		return active;
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

	public void setActive(String active) {
		this.active = active;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}

package com.onlinestore.zuulservice;

import com.onlinestore.zuulservice.utils.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that bootstraps this service.
 */
@SpringBootApplication
@EnableZuulProxy
public class ZuulApplication {
	/**
	 * Creates {@code RestTemplate} and adds {@code UserContextInterceptor} to it.
	 * This {@code RestTemplate} is going to use Ribbon.
	 *
	 * @return {@code RestTemplate}
	 */
	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<>();
		}
		interceptors.add(new UserContextInterceptor());
		restTemplate.setInterceptors(interceptors);
		return restTemplate;
	}

	/**
	 * Main method, used to run this application.
	 *
	 * @param args the string array, that contains command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}
}

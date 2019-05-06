package com.onlinestore.zuulservice.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinestore.zuulservice.domain.RouteRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 8080)
public class RouteRecordProxyTest {
	private static final String ROUTS_URI = "http://alternativeroutesservice/v1/route/records/{serviceName}";

	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RouteRecordProxy routeRecordProxy;

	private String jsonRouteRecord;

	@Before
	public void setUp() throws Exception {
		restTemplate = new RestTemplate();
		routeRecordProxy = new RouteRecordProxy(restTemplate);
		jsonRouteRecord = objectMapper.writeValueAsString(new RouteRecord("customerservice", "YES", "", 1));
	}

	@Test
	public void shouldReturnRouteRecordFromAlternativeRouteService() throws Exception {
		stubFor(get("/v1/route/records/customerservice")
				.willReturn(aResponse()
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
						.withBody(jsonRouteRecord)));

		RouteRecord routeRecord = routeRecordProxy.getRouteRecordInfo("customerservice").get();
		assertThat(routeRecord).isExactlyInstanceOf(RouteRecord.class);
		assertThat(routeRecord.getServiceName()).isEqualTo("customerservice");
	}

}
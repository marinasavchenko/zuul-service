package com.onlinestore.zuulservice.filters;

import com.onlinestore.zuulservice.domain.RouteRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlternativeRoutesFilterTest {

	private AlternativeRoutesFilter alternativeRoutesFilter;

	@Mock
	private RouteRecord routeRecord;

	@Mock
	private FilterUtils filterUtils;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private Random random;

	@Before
	public void setUp() throws Exception {
		alternativeRoutesFilter = new AlternativeRoutesFilter(filterUtils, restTemplate);
	}

	@Test
	public void shouldReturnFalseWhenActiveStatusNO() throws Exception {
		when(routeRecord.getActiveStatus()).thenReturn("NO");
		assertThat(alternativeRoutesFilter.shouldUseAlternativeRoute(routeRecord)).isFalse();
	}
}
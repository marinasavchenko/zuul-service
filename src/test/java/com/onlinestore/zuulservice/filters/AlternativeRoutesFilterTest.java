package com.onlinestore.zuulservice.filters;

import com.onlinestore.zuulservice.domain.RouteRecord;
import com.onlinestore.zuulservice.utils.RandomGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
	private RouteRecordProxy routeRecordProxy;

	@Mock
	private RandomGenerator randomGenerator;

	@Before
	public void setUp() throws Exception {
		alternativeRoutesFilter = new AlternativeRoutesFilter(filterUtils, routeRecordProxy, randomGenerator);
	}

	@Test
	public void shouldReturnFalseWhenActiveStatusNO() throws Exception {
		when(routeRecord.getActiveStatus()).thenReturn("NO");
		assertThat(alternativeRoutesFilter.shouldUseAlternativeRoute(routeRecord)).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenActiveStatusYes() throws Exception {
		when(routeRecord.getActiveStatus()).thenReturn("YES");
		when(routeRecord.getWeight()).thenReturn(3);
		when(randomGenerator.getRandomInt()).thenReturn(4);

		assertThat(alternativeRoutesFilter.shouldUseAlternativeRoute(routeRecord)).isTrue();
	}
}
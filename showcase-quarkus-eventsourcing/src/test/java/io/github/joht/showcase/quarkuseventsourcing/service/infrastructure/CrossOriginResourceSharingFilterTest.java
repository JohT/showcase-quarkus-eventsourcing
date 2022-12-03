package io.github.joht.showcase.quarkuseventsourcing.service.infrastructure;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;

class CrossOriginResourceSharingFilterTest {

	@Mock
	Instance<Boolean> cors;

	@Mock	
	Instance<String> allowOrigin;

	@Mock
	Instance<String> allowCredentials;

	@Mock
	Instance<String> allowMethods;

	@Mock
	Instance<String> allowHeaders;

	@Mock
	Instance<String> exposeHeaders;

	@Mock
	ContainerRequestContext request;
	
	@Mock
	ContainerResponseContext response;
	
	@Mock
	MultivaluedMap<String, Object> responseHeaders;
	
	@InjectMocks
	CrossOriginResourceSharingFilter filterUnderTest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(response.getHeaders()).thenReturn(responseHeaders);
	}

	@Test
	@DisplayName("Do nothing if no CORS configuration is available")
	void noCorsConfigAvailable() {
		when(cors.isUnsatisfied()).thenReturn(Boolean.TRUE);
		
		filterUnderTest.filter(request, response);
		
		verifyNoMoreInteractions(request, response);
	}

	@Test
	@DisplayName("Do nothing if 'rest.cors'=null")
	void corstConfigNull() {
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(null);
		
		filterUnderTest.filter(request, response);
		
		verifyNoMoreInteractions(request, response);
	}

	@Test
	@DisplayName("Do nothing if 'rest.cors'=false")
	void corstConfigFalse() {
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.FALSE);
		
		filterUnderTest.filter(request, response);
		
		verifyNoMoreInteractions(request, response);
	}

	@Test
	@DisplayName("Do nothing if 'rest.cors'=true but no headers are configured")
	void corsWithoutHeadersActivated() {
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		
		filterUnderTest.filter(request, response);
		
		verify(response).getHeaders();
		verifyNoMoreInteractions(request, response);
	}

	@Test
	@DisplayName("skip Access-Control-Allow-Credentials if 'rest.cors.allow.origin'=null")
	void corsAllowCredentialsConfiguredButNull() {
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		when(allowCredentials.isResolvable()).thenReturn(Boolean.TRUE);
		
		filterUnderTest.filter(request, response);
		
		verify(response).getHeaders();
		verifyNoMoreInteractions(request, response);
	}
	
	@Test
	@DisplayName("add Access-Control-Allow-Origin if 'rest.cors.allow.origin' configured")
	void corsAllowOriginConfigured() {
		String expectedValue = "http://127.0.0.1:5500";
		
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		when(allowOrigin.isResolvable()).thenReturn(Boolean.TRUE);
		when(allowOrigin.get()).thenReturn(expectedValue);
		
		filterUnderTest.filter(request, response);
		
		verify(responseHeaders).add("Access-Control-Allow-Origin", expectedValue);
	}
	
	@Test
	@DisplayName("add Access-Control-Allow-Credentials if 'rest.cors.allow.credentials' configured")
	void corsAllowCredentialsConfigured() {
		String expectedValue = "true";
		
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		when(allowCredentials.isResolvable()).thenReturn(Boolean.TRUE);
		when(allowCredentials.get()).thenReturn(expectedValue);
		
		filterUnderTest.filter(request, response);
		
		verify(responseHeaders).add("Access-Control-Allow-Credentials", expectedValue);
	}
	
	@Test
	@DisplayName("add Access-Control-Allow-Methods if 'rest.cors.allow.methods' configured")
	void corsAllowMethodsConfigured() {
		String expectedValue = "GET,PUT,POST,DELETE,OPTIONS,HEAD";
		
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		when(allowMethods.isResolvable()).thenReturn(Boolean.TRUE);
		when(allowMethods.get()).thenReturn(expectedValue);
		
		filterUnderTest.filter(request, response);
		
		verify(responseHeaders).add("Access-Control-Allow-Methods", expectedValue);
	}

	
	@Test
	@DisplayName("add Access-Control-Allow-Headers if 'rest.cors.allow.headers' configured")
	void corsAllowHeadersConfigured() {
		String expectedValue = "Origin,Authorization,Location,Content-Type";
		
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		when(allowHeaders.isResolvable()).thenReturn(Boolean.TRUE);
		when(allowHeaders.get()).thenReturn(expectedValue);
		
		filterUnderTest.filter(request, response);
		
		verify(responseHeaders).add("Access-Control-Allow-Headers", expectedValue);
	}
	
	@Test
	@DisplayName("add Access-Control-Expose-Headers if 'rest.cors.expose.headers' configured")
	void corsExposeHeadersConfigured() {
		String expectedValue = "Origin,Authorization,Location,Content-Type";
		
		when(cors.isResolvable()).thenReturn(Boolean.TRUE);
		when(cors.get()).thenReturn(Boolean.TRUE);
		when(exposeHeaders.isResolvable()).thenReturn(Boolean.TRUE);
		when(exposeHeaders.get()).thenReturn(expectedValue);
		
		filterUnderTest.filter(request, response);
		
		verify(responseHeaders).add("Access-Control-Expose-Headers", expectedValue);
	}
	
}

package io.github.joht.showcase.quarkuseventsourcing.service.infrastructure;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

/**
 * Provides a {@link ContainerResponseFilter} for Cross Origin Resource Sharing
 * (CORS) using Standard MicroProfile API's (thus not Quarkus dependent).
 */
@Provider
public class CrossOriginResourceSharingFilter implements ContainerResponseFilter {

	// Note: To overcome the following warning message, 
	// "Instance" is used instead of the plain value type:
	// "Directly injecting a org.eclipse.microprofile.config.inject.ConfigProperty
	// into a javax.ws.rs.ext.Provider may lead to unexpected results"
	// 
	// See also https://github.com/quarkusio/quarkus/issues/27487

	@Inject
	@ConfigProperty(name = "rest.cors")
	Instance<Boolean> cors;

	@Inject
	@ConfigProperty(name = "rest.cors.allow.origin")
	Instance<String> allowOrigin;

	@Inject
	@ConfigProperty(name = "rest.cors.allow.credentials")
	Instance<String> allowCredentials;

	@Inject
	@ConfigProperty(name = "rest.cors.allow.methods")
	Instance<String> allowMethods;

	@Inject
	@ConfigProperty(name = "rest.cors.allow.headers")
	Instance<String> allowHeaders;

	@Inject
	@ConfigProperty(name = "rest.cors.expose.headers")
	Instance<String> exposeHeaders;

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) {
		if (!cors.isResolvable() || !Boolean.TRUE.equals(cors.get())) {
			return;
		}
		MultivaluedMap<String, Object> headers = response.getHeaders();
		addIfPresent("Access-Control-Allow-Origin", allowOrigin, headers);
		addIfPresent("Access-Control-Allow-Credentials", allowCredentials, headers);
		addIfPresent("Access-Control-Allow-Methods", allowMethods, headers);
		addIfPresent("Access-Control-Allow-Headers", allowHeaders, headers);
		addIfPresent("Access-Control-Expose-Headers", exposeHeaders, headers);
	}

	private void addIfPresent(String name, Instance<String> value, MultivaluedMap<String, Object> headers) {
		if (value.isResolvable() && value.get() != null) {
			headers.add(name, value.get().trim());
		}
	}
}

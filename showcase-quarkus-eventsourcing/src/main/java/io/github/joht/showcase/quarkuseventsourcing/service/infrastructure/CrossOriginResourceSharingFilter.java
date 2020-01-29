package io.github.joht.showcase.quarkuseventsourcing.service.infrastructure;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Provides a {@link ContainerResponseFilter} for Cross Origin Resource Sharing (CORS).
 */
@Provider
public class CrossOriginResourceSharingFilter implements ContainerResponseFilter {

    @Inject
    @ConfigProperty(name = "rest.cors")
    boolean cors;

    @Inject
    @ConfigProperty(name = "rest.cors.allow.origin")
    String allowOrigin;

    @Inject
    @ConfigProperty(name = "rest.cors.allow.credentials")
    String allowCredentials;

    @Inject
    @ConfigProperty(name = "rest.cors.allow.methods")
    String allowMethods;

    @Inject
    @ConfigProperty(name = "rest.cors.allow.headers")
    String allowHeaders;

    @Inject
    @ConfigProperty(name = "rest.cors.expose.headers")
    String exposeHeaders;

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (!cors) {
            return;
        }
        MultivaluedMap<String, Object> headers = response.getHeaders();
        headers.add("Access-Control-Allow-Credentials", allowCredentials);
        headers.add("Access-Control-Allow-Origin", allowOrigin);
        headers.add("Access-Control-Allow-Methods", allowMethods);
        headers.add("Access-Control-Allow-Headers", allowHeaders);
        headers.add("Access-Control-Expose-Headers", exposeHeaders);
    }
}

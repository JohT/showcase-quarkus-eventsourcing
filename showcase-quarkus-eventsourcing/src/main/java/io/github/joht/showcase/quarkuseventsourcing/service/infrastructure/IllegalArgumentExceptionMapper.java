package io.github.joht.showcase.quarkuseventsourcing.service.infrastructure;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }

}

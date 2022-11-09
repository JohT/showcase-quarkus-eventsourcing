package io.github.joht.showcase.quarkuseventsourcing.service.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

	private static final Logger LOGGER = Logger.getLogger(IllegalArgumentExceptionMapper.class.getName());
	
    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(IllegalArgumentException exception) {
    	LOGGER.log(Level.WARNING, exception, () -> "Will return status code " + Status.BAD_REQUEST);
        return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }

}

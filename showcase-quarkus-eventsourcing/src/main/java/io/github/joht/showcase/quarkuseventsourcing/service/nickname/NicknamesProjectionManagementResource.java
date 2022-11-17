package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionManagementService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus;
import io.github.joht.showcase.quarkuseventsourcing.query.model.nickname.NicknameProjection;

@ApplicationScoped
@Path("/nicknames/projection")
public class NicknamesProjectionManagementResource {

	@Inject
	QueryProjectionManagementService replayService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public QueryProjectionStatus getNicknameProjectionStatus() {
        return replayService.getStatus(NicknameProjection.PROCESSING_GROUP_FOR_NICKNAMES);
    }
    
	@DELETE
    public Response replayNicknameProjection() {
        replayService.replayProcessingGroup(NicknameProjection.PROCESSING_GROUP_FOR_NICKNAMES);
		return Response.noContent().build();
	}
}
package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
package io.github.joht.showcase.quarkuseventsourcing.service.account;

import static io.github.joht.showcase.quarkuseventsourcing.message.query.account.AccountNicknameQuery.queryNicknameForAccount;
import static javax.transaction.Transactional.TxType.REQUIRED;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.github.joht.showcase.quarkuseventsourcing.message.command.account.ChangeNicknameCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.command.account.CreateAccountCommand;
import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.github.joht.showcase.quarkuseventsourcing.messaging.command.boundary.CommandEmitterService;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QuerySubmitterService;

@Path("/accounts")
public class AccountResource {

	@Inject
	CommandEmitterService commands;

	@Inject
	QuerySubmitterService queries;

	@POST
	@Transactional(REQUIRED)
	public Response createAccount(@Context UriInfo uriInfo) {
		String newAccountId = UUID.randomUUID().toString();
		commands.sendAndWaitFor(new CreateAccountCommand(newAccountId));
		return Response.created(locationHeader(uriInfo, newAccountId)).build();
	}

	@GET
	@Path("{id}/nickname")
	@Produces(MediaType.APPLICATION_JSON)
	public Nickname getAccount(@PathParam("id") String id) throws InterruptedException, ExecutionException {
		String details = String.format("nickname of account with id %s", id);
		return queries.waitFor(queries.query(queryNicknameForAccount(id), Nickname.class), details);
	}

	@PUT
	@Path("{id}/nickname")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getAccount(@PathParam("id") String id, Nickname nickname) {
		commands.sendAndWaitFor(new ChangeNicknameCommand(id, nickname));
	}

	private static URI locationHeader(UriInfo uriInfo, String id) {
		return uriInfo.getAbsolutePathBuilder().path(id).build();
	}
}
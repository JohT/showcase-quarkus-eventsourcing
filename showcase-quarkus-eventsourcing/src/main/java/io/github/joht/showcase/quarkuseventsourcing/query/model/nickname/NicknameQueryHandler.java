package io.github.joht.showcase.quarkuseventsourcing.query.model.nickname;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.FetchDistinctNicknamesQuery;
import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelQueryHandler;

@ApplicationScoped
public class NicknameQueryHandler {

	@Inject
	NicknameRepository repository;

	@QueryModelQueryHandler
	public List<NicknameDetails> queryNicknames(FetchDistinctNicknamesQuery query) {
        return repository.getNicknamesContaining(query.getPartOfNickname(), query.getCreatedSince());
	}
}
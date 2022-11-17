package io.github.joht.showcase.quarkuseventsourcing.query.model.account;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;

@ApplicationScoped
public class AccountRepository {

	@PersistenceContext(unitName = "query.model")
	EntityManager entityManager;

	@Transactional(TxType.REQUIRED)
	public AccountEntity create(AccountEntityKey key) {
		AccountEntity account = new AccountEntity(key);
		entityManager.persist(account);
		return account;
	}

	@Transactional(TxType.REQUIRED)
	public void setNickname(AccountEntityKey key, Nickname nickname) {
		AccountEntity account = read(key);
		account.setNickname(nickname);
		entityManager.flush();
	}

	public AccountEntity read(AccountEntityKey key) {
		return readOptional(key).orElseThrow(() -> new IllegalStateException(String.format("Account %s doesn't exist.", key)));
	}

	private Optional<AccountEntity> readOptional(AccountEntityKey key) {
		AccountEntity account = entityManager.find(AccountEntity.class, key);
		return Optional.ofNullable(account);
	}
}
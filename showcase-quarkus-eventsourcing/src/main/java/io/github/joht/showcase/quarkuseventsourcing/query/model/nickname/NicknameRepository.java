package io.github.joht.showcase.quarkuseventsourcing.query.model.nickname;

import static java.util.stream.Collectors.toList;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import io.github.joht.showcase.quarkuseventsourcing.message.query.nickname.NicknameDetails;

@ApplicationScoped
@Transactional(TxType.REQUIRED)
public class NicknameRepository {

    private static final Logger LOGGER = Logger.getLogger(NicknameRepository.class.getName());

    @PersistenceContext(unitName = "query.model")
    EntityManager entityManager;

    /**
     * Updates the nicknames and returns <code>true</code>, <br>
     * if it was newly created or <code>false</code>, if it was already in use.
     * 
     * @param details {@link NicknameDetails}
     * @return
     */
    public boolean updateNicknameDetails(NicknameDetails details) {
        NicknameEntityKey key = NicknameEntityKey.of(details.getNickname());
        return readOptional(key)
                .map(existing -> existing.updateNicknameDetails(details))
                .orElseGet(() -> persisted(NicknameEntity.ofNicknameDetails(details))).isDistinct();
    }

    /**
     * Reads all nicknames that are like the given part.
     * <p>
     * The offset assures, that only those nicknames are returned, that were created after the given {@link Timestamp}.
     * 
     * @param partOfNickname {@link String}
     * @param createdAfter {@link Timestamp}
     * @return {@link List} of {@link NicknameDetails}.
     */
    public List<NicknameDetails> getNicknamesContaining(String partOfNickname, Instant createdAfter) {
        TypedQuery<NicknameEntity> queryNicknames = entityManager.createNamedQuery(NicknameEntity.QUERY_NICKNAMES,
                NicknameEntity.class);
        queryNicknames.setParameter("partOfNickname", "%" + partOfNickname + "%");
        queryNicknames.setParameter("sequenceStart", createdAfter.getEpochSecond());
        return queryNicknames.getResultList().stream().map(NicknameEntity::getNicknameDetails).collect(toList());
    }

    public void deleteAll() {
        int deletedEntries = entityManager.createNamedQuery(NicknameEntity.DELETE_ALL_NICKNAMES).executeUpdate();
        LOGGER.info("Deleted all " + deletedEntries + " from Nickname");
        entityManager.flush();
    }

    private <T> T persisted(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    private Optional<NicknameEntity> readOptional(NicknameEntityKey key) {
        return Optional.ofNullable(entityManager.find(NicknameEntity.class, key));
    }
}
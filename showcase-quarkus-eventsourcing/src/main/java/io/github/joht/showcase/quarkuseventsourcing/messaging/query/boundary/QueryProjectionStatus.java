package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Represents the status of a projection (more precise the tracking event processor group of it).
 * 
 * @author JohT
 */
public class QueryProjectionStatus implements Iterable<QueryProjectionStatus.Feature> {

    private final Collection<QueryProjectionStatus.Feature> features = new ArrayList<>();

    public static final QueryProjectionStatus of(QueryProjectionStatus.Feature... features) {
        return ofAll(Arrays.asList(features));
    }

    public static final QueryProjectionStatus ofAll(Collection<Feature> features) {
        return new QueryProjectionStatus(features);
    }

    @ConstructorProperties({ "features" })
    public QueryProjectionStatus(Collection<Feature> features) {
        this.features.addAll(features);
    }

    public boolean contains(QueryProjectionStatus.Feature feature) {
        return features.contains(feature);
    }

    public Collection<QueryProjectionStatus.Feature> getFeatures() {
        return Collections.unmodifiableCollection(features);
    }

    @Override
    public Iterator<QueryProjectionStatus.Feature> iterator() {
        return features.iterator();
    }

    @Override
    public String toString() {
        return "ProjectionFeatures [features=" + features + "]";
    }

    public static enum Feature {
        CAUGHT_UP,
        /**
         * Note: Currently (August 2019) it seems, that this feature stays as long as the replay token is reached. This means, that it
         * remains after replaying is done until there is a new event to handle.
         */
        REPLAYING,
        /**
         * Shows that the tracking event processor is in error state.
         */
        ERROR_STATE,
        /**
         * Shows that the tracking event processor is currently consuming events.
         */
        RUNNING,
    }
}
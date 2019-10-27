package io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary;

/**
 * Lists available processor configurations for event handlers.
 * 
 * @author JohT
 */
public enum QueryProcessor {
    TRACKING("tracking"),
    SUBSCRIBING("subscribing"),
    ;

    private final String name;

    private QueryProcessor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return equals(TRACKING);
    }
}
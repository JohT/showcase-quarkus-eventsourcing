package io.github.joht.showcase.quarkuseventsourcing.messaging.query;

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.inject.Typed;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelProjection;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryModelProjection.ProcessorAssignment;
import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProcessor;

class ProcessorAssignmentTest {

    ProcessorAssignment processorAssignment = new ProcessorAssignment();
    private Optional<QueryModelProjection> assignmentInformation = Optional.empty();

    @Test
    void assignmentShouldBePresentWhenSpecifiedByAnnotation() {
        assignmentInformation = processorAssignment.apply(ProjectionWithAssignedProcessor.class);
        assertEquals("AssignedGroup", assignmentInformation.get().processingGroup());
        assertEquals(QueryProcessor.SUBSCRIBING, assignmentInformation.get().processor());
    }

    @Test
    void assignmentShouldBeSendToConsumerWhenSpecifiedByAnnotation() {
        ProcessorAssignment.forType(ProjectionWithAssignedProcessor.class, toAssignmentInformation());
        assertEquals("AssignedGroup", assignmentInformation.get().processingGroup());
        assertEquals(QueryProcessor.SUBSCRIBING, assignmentInformation.get().processor());
    }

    @Test
    void assignmentShouldntBePresentWithoutAnnotation() {
        assignmentInformation = processorAssignment.apply(TypeWithoutAnnotaton.class);
        assertFalse(assignmentInformation.isPresent());
    }

    @Test
    void assignmentShouldntBeSendToConsumerWithoutAnnotation() {
        ProcessorAssignment.forType(TypeWithoutAnnotaton.class, toAssignmentInformation());
        assertFalse(assignmentInformation.isPresent());
    }

    // TODO reactivate isDefault
    @Disabled
    @Test
    void assignmentShouldntBePresentForDefaultProcessor() {
        assignmentInformation = processorAssignment.apply(ProjectionWithDefaultProcessor.class);
        assertFalse(assignmentInformation.isPresent());
    }

    // TODO reactivate isDefault
    @Disabled
    @Test
    void assignmentShouldntBeSendToConsumerForDefaultProcessor() {
        ProcessorAssignment.forType(ProjectionWithDefaultProcessor.class, toAssignmentInformation());
        assertFalse(assignmentInformation.isPresent());
    }

    @Test
    void assignmentShouldntBePresentWhenGroupIsMissing() {
        assertThrows(RuntimeException.class, () -> processorAssignment.apply(ProjectionWithAssignedProcessorButEmptyGroup.class));
    }

    @Test
    void assignmentShouldntBeSendToConsumerWhenGroupIsMissing() {
        assertThrows(RuntimeException.class,
                () -> ProcessorAssignment.forType(ProjectionWithAssignedProcessorButEmptyGroup.class, toAssignmentInformation()));
    }

    private Consumer<QueryModelProjection> toAssignmentInformation() {
        return assignment -> assignmentInformation = Optional.of(assignment);
    }

    @Typed
    @QueryModelProjection(processingGroup = "AssignedGroup", processor = QueryProcessor.SUBSCRIBING)
    static final class ProjectionWithAssignedProcessor {

    }

    @Typed
    @QueryModelProjection(processingGroup = "", processor = QueryProcessor.SUBSCRIBING)
    static final class ProjectionWithAssignedProcessorButEmptyGroup {

    }

    @Typed
    @QueryModelProjection(processingGroup = "DefaultGroup")
    static final class ProjectionWithDefaultProcessor {

    }

    private static final class TypeWithoutAnnotaton {
        // no content
    }
}
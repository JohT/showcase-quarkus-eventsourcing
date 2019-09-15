package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joht.showcase.quarkuseventsourcing.messaging.query.boundary.QueryProjectionStatus.Feature;
import io.quarkus.test.junit.DisabledOnSubstrate;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
@DisabledOnSubstrate
public class NicknamesProjectionManagementResourceIntegrationTest {

    private static final int OK = Status.OK.getStatusCode();
    private static final int NO_CONTENT = Status.NO_CONTENT.getStatusCode();
    private static final String CAUGHT_UP = Feature.CAUGHT_UP.toString();

    @Test
    public void replayNicknames() throws InterruptedException {
        RestAssured.given().when().delete("/nicknames/projection").then().statusCode(NO_CONTENT);
        waitUnilAllEventsAreReplayed();
        String finalState = getProjectionTrackingState();
        assertFalse(finalState.contains(Feature.ERROR_STATE.toString()), finalState.toString());
    }

    private void waitUnilAllEventsAreReplayed() throws InterruptedException {
        Set<String> projectionStatusResponses = new HashSet<>();
        int maxTimeToWaitInMs = 10 * 1000; // milliseconds
        int waitTime = 100; // milliseconds
        int maxIterations = (maxTimeToWaitInMs / waitTime);
        for (int i = 0; i <= maxIterations; i++) {
            assertTrue(i < maxIterations, "The processor didn't catch up after " + i * waitTime + " ms.");

            String responseBody = getProjectionTrackingState();
            projectionStatusResponses.add(responseBody);
            if (responseBody.contains(CAUGHT_UP)) {
                break;
            }
            Thread.sleep(waitTime);
        }
        // Note: Normally, at least two responses can be expected.
        // In-memory databases like H2 with very few entries are fast enough, to get the replay done within the first iteration
        assertFalse(projectionStatusResponses.isEmpty(), "Expected at least one response: " + projectionStatusResponses);

    }

    private String getProjectionTrackingState() {
        ValidatableResponse response = RestAssured.given().when().get("/nicknames/projection").then().statusCode(OK);
        return response.extract().body().asString();
    }
}
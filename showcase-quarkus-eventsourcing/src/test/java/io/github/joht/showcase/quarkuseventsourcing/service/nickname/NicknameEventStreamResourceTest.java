package io.github.joht.showcase.quarkuseventsourcing.service.nickname;

import static io.restassured.http.ContentType.JSON;
import static org.jboss.resteasy.util.HttpHeaderNames.LOCATION;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
public class NicknameEventStreamResourceTest {

    private static final int CREATED = Status.CREATED.getStatusCode();
    private static final int NO_CONTENT = Status.NO_CONTENT.getStatusCode();

    private static final Nickname EXPECTED_NICKNAME = Nickname.of("THE Nickname");

    private List<String> receivedDataFields = new ArrayList<>();

    @TestHTTPResource("/nicknameevents")
    URL url;

    /**
     * Creates a new account and changes the nickname to {@link #EXPECTED_NICKNAME}.
     * 
     * @throws InterruptedException
     */
    @BeforeEach
    public void nicknameChangeOnNewlyCreatedAccount() throws InterruptedException {
        String newAccount = given().when().post("/accounts").then().statusCode(CREATED).extract().header(LOCATION);
        String nicknameUrl = newAccount + "/nickname";
        String nicknameJson = JsonbBuilder.create().toJson(EXPECTED_NICKNAME);
        given().body(nicknameJson).when().when().put(nicknameUrl).then().statusCode(NO_CONTENT);
    }

    @Test
    public void containsExpectedNicknameInStream() throws IOException, InterruptedException {
        try (SseEventSource sse = createServerSentEventsSource()) {
            sse.register(this::onServerSentEventMessage);
            sse.open();

            for (int i = 0; i < 4; i++) {
                Thread.sleep(500);
                if (receivedDataFields.stream().anyMatch(this::containsExpectedNickname)) {
                    return;
                }
            }
            fail("Missing expected nickname " + EXPECTED_NICKNAME.getValue() + " in stream " + receivedDataFields);
        }
    }

    private SseEventSource createServerSentEventsSource() {
        WebTarget client = ClientBuilder.newClient().target(url.toExternalForm());
        return SseEventSource.target(client).reconnectingEvery(5, TimeUnit.SECONDS).build();
    }

    void onServerSentEventMessage(InboundSseEvent event) {
        receivedDataFields.add(event.readData());
    }

    boolean containsExpectedNickname(String data) {
        return data.contains(EXPECTED_NICKNAME.getValue());
    }

    private static RequestSpecification given() {
        return RestAssured.given().contentType(JSON);
    }
}
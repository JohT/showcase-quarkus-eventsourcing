package io.github.joht.showcase.quarkuseventsourcing.service.account;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.jboss.resteasy.util.HttpHeaderNames.LOCATION;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.github.joht.showcase.quarkuseventsourcing.message.common.Nickname;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
public class AccountResourceTest {

    private static final String EMPTY = "\"\"";

    private static final int OK = Status.OK.getStatusCode();
    private static final int CREATED = Status.CREATED.getStatusCode();
    private static final int NO_CONTENT = Status.NO_CONTENT.getStatusCode();

    private Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void accountCreated() throws InterruptedException {
        given().when().post("/accounts").then().statusCode(CREATED).header(LOCATION, containsString("accounts"));
    }

    /**
     * Creates a new account, checks that the nickname is preset with an empty value, changes the nickname and assures, that the value had
     * been set correctly.
     * 
     * @throws InterruptedException
     */
    @Test
    public void nicknameChangeOnNewlyCreatedAccount() throws InterruptedException {
        Nickname nickname = Nickname.of("The Rock " + LocalDate.now() + " " + LocalTime.now());
        String newAccount = given().when().post("/accounts").then().statusCode(CREATED).extract().header(LOCATION);
        String nicknameUrl = newAccount + "/nickname";
        given().when().get(nicknameUrl).then().statusCode(OK).body(containsString(EMPTY));
        given().body(jsonb.toJson(nickname)).when().when().put(nicknameUrl).then().statusCode(NO_CONTENT);
        given().when().get(nicknameUrl).then().statusCode(OK).body(containsString(nickname.getValue()));
    }

    private static RequestSpecification given() {
        return RestAssured.given().contentType(JSON);
    }
}
package org.rm3l.macoui.resources.management;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MacOuiManagementResourceTest {

    @Test
    public void testPingEndpoint() {
        given()
          .when().get("/management/ping")
          .then()
             .statusCode(204);
    }

    @Test
    public void testHealthEndpoint() {
        given()
            .when().get("/management/health")
            .then()
            .statusCode(204);
    }

}

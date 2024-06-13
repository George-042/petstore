package by.georgprog.petstore.api;

import by.georgprog.petstore.api.data.DataFactory;
import by.georgprog.petstore.api.specs.Specifications;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static by.georgprog.petstore.api.consts.Consts.URLs.URI;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class PetStoreApiTest {

    @Test
    @DisplayName("Positive: Get Pet Inventories by Status")
    public void testGetInventories() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        JsonPath response = given()
                .when()
                .get("/store/inventory")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("generic-schema.json"))
                .extract().response().jsonPath();
        Map<Object, Object> map = response.getMap("");
        Assertions.assertFalse(map.isEmpty(), "Response should not be empty");
    }

    @Test
    @DisplayName("Positive: Place an order for a pet")
    public void testPlaceOrder() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        Map<Object, Object> body = new HashMap<>(DataFactory.getOrderForPet());
        JsonPath response = given()
                .body(body)
                .when()
                .post("/store/order")
                .then()
                .extract().response().jsonPath();
        Map<Object, Object> map = response.getMap("");
        LocalDateTime dateTime1 = LocalDateTime.parse(body.get("shipDate").toString(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime dateTime2 = LocalDateTime.parse(map.get("shipDate").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        String withoutTimezone = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String timePart1 = dateTime1.format(DateTimeFormatter.ofPattern(withoutTimezone));
        String timePart2 = dateTime2.format(DateTimeFormatter.ofPattern(withoutTimezone));
        body.put("shipDate", timePart1);
        map.put("shipDate", timePart2);
        map.forEach((key, value) -> Assertions.assertTrue(body.containsKey(key) && body.get(key).equals(value),
                "Key and value in response should match with body map"));
    }

    @Test
    @DisplayName("Negative: Place an invalid order for a pet")
    public void testPlaceInvalidOrder() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(400));
        JsonPath response = given()
                .body("")
                .when()
                .post("/store/order")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals("No data", response.get("message"), "Invalid order message should be returned");
    }

    @Test
    @DisplayName("Positive: Find Purchase Order by ID")
    public void testFindOrderById() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        Map<Object, Object> body = new HashMap<>(DataFactory.getOrderForPet());
        given()
                .body(body)
                .when()
                .post("/store/order");
        JsonPath response = given()
                .pathParam("orderId", body.get("id"))
                .when()
                .get("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Map<Object, Object> map = response.getMap("");
        LocalDateTime dateTime1 = LocalDateTime.parse(body.get("shipDate").toString(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime dateTime2 = LocalDateTime.parse(map.get("shipDate").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        String withoutTimezone = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String timePart1 = dateTime1.format(DateTimeFormatter.ofPattern(withoutTimezone));
        String timePart2 = dateTime2.format(DateTimeFormatter.ofPattern(withoutTimezone));
        body.put("shipDate", timePart1);
        map.put("shipDate", timePart2);
        map.forEach((key, value) -> Assertions.assertTrue(body.containsKey(key) && body.get(key).equals(value),
                "Key and value in response should match with body map"));
    }

    @Test
    @DisplayName("Negative: Find Non-existing Purchase Order by ID")
    public void testFindOrderByInvalidId() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(404));
        int nonExistingOrderId = -999999;
        JsonPath response = given()
                .pathParam("orderId", nonExistingOrderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals("Order not found", response.get("message"), "Purchase Order should be null");
    }

    @Test
    @DisplayName("Positive: Delete Purchase Order by ID")
    public void testDeleteOrderById() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        Map<Object, Object> body = DataFactory.getOrderForPet();
        given()
                .body(body)
                .when()
                .post("/store/order");
        JsonPath response = given()
                .pathParam("orderId", body.get("id"))
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals(body.get("id").toString(), response.get("message").toString(),
                "ID in response should be equal to ID in body");
    }

    @Test
    @DisplayName("Negative: Delete Non-existing Purchase Order by ID")
    public void testDeleteNonExistingOrderById() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(404));
        int nonExistingOrderId = 999999;
        JsonPath response = given()
                .pathParam("orderId", nonExistingOrderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals("Order Not Found", response.get("message"), "Purchase Order should be null");
    }
}
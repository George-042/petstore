package by.georgprog.petstore.api;

import by.georgprog.petstore.api.data.DataFactory;
import by.georgprog.petstore.api.specs.Specifications;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import java.util.Map;

import static by.georgprog.petstore.api.consts.Consts.URLs.URI;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreApiTest {

    @Test
    @Order(1)
    @DisplayName("Positive: Get Pet Inventories by Status")
    public void testGetInventories() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        JsonPath response = given()
                .when()
                .get("/store/inventory")
                .then().extract().response().jsonPath();
        Map<Object, Object> map = response.getMap("");
        Assertions.assertFalse(map.isEmpty(), "Response should not be empty");
    }

    @Test
    @Order(2)
    @DisplayName("Positive: Place an order for a pet")
    public void testPlaceOrder() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        Map<Object, Object> body = DataFactory.getOrderForPet();
        JsonPath response = given()
                .body(body)
                .when()
                .post("/store/order")
                .then()
                .extract().response().jsonPath();
        Map<Object, Object> map = response.getMap("");
        String regex = ".{5}$";
        map.forEach((k, v) -> {
                    if (k.equals("shipDate")) {
                        String s = v.toString().replaceAll(regex, "");
                        Assertions.assertTrue(body.get(k).toString().contains(s));
                    } else
                        Assertions.assertTrue(body.containsKey(k) && body.get(k).equals(v),
                                "Key and value in response should match with body map");
                }
        );
    }

    @Test
    @Order(3)
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
    @Order(4)
    @DisplayName("Positive: Find Purchase Order by ID")
    public void testFindOrderById() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(200));
        Map<Object, Object> body = DataFactory.getOrderForPet();
        JsonPath response = given()
                .pathParam("orderId", body.get("id"))
                .when()
                .get("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals(response.get("id"), body.get("id"), "ID in response should be equal to ID in body");
    }

    @Test
    @Order(5)
    @DisplayName("Negative: Find Purchase Order by invalid ID")
    public void testFindNonExistingOrderById() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(400));
        JsonPath response = given()
                .pathParam("orderId", "")
                .when()
                .get("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals("Invalid ID Supplied", response.get("message"), "Invalid ID message should be returned");
    }

    @Test
    @Order(6)
    @DisplayName("Negative: Find Non-existing Purchase Order by ID")
    public void testFindOrderByInvalidId() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(404));
        int nonExistingOrderId = 999999;
        JsonPath response = given()
                .pathParam("orderId", nonExistingOrderId)
                .when()
                .get("/store/order/{orderId}")
                .then().extract().response().jsonPath();
        Assertions.assertEquals("Order not found", response.get("message"), "Purchase Order should be null");
    }

    @Test
    @Order(7)
    @DisplayName("Negative: Delete Purchase Order by invalid ID")
    public void testDeleteOrderByInvalidId() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(400));
        JsonPath response = given()
                .pathParam("orderId", "")
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
        Assertions.assertEquals("Invalid ID Supplied", response.get("message"), "Invalid ID message should be returned");
    }

    @Test
    @Order(8)
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
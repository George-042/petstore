package by.georgprog.petstore.api.client.store;

import by.georgprog.petstore.api.dto.OrderDto;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matcher;

import static io.restassured.RestAssured.given;

public class StoreClient {

    public JsonPath getInventory() {
        return given()
                .when()
                .get("/store/inventory")
                .then()
                .extract().response().jsonPath();
    }

    public JsonPath getInventory(Matcher<?> matcher) {
        return given()
                .when()
                .get("/store/inventory")
                .then()
                .body(matcher)
                .extract().response().jsonPath();
    }

    public JsonPath placeOrder(OrderDto order) {
        return given()
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .extract().response().jsonPath();
    }

    public JsonPath placeOrder(Object object) {
        return given()
                .body(object)
                .when()
                .post("/store/order")
                .then()
                .extract().response().jsonPath();
    }

    public JsonPath findOrderById(long orderId) {
        return given()
                .pathParam("orderId", orderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
    }

    public JsonPath deleteOrderById(long orderId) {
        return given()
                .pathParam("orderId", orderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .extract().response().jsonPath();
    }
}

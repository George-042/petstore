package by.georgprog.petstore.api;

import by.georgprog.petstore.api.client.store.StoreClient;
import by.georgprog.petstore.api.consts.DateTime;
import by.georgprog.petstore.api.consts.HttpStatus;
import by.georgprog.petstore.api.dto.OrderDto;
import by.georgprog.petstore.api.providers.OrderDataProvider;
import by.georgprog.petstore.api.specs.Specifications;
import by.georgprog.petstore.api.util.DateTimeUtil;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Stream;

import static by.georgprog.petstore.api.consts.Urls.URI;
import static io.restassured.http.ContentType.JSON;

public class PetStoreApiTest {

    private static StoreClient storeClient;

    @BeforeAll
    public static void setUp() {
        storeClient = new StoreClient();
    }

    @Test
    @Tag("positive")
    @DisplayName("Positive: Get Pet Inventories by Status")
    public void testGetInventories() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.OK));
        JsonPath inventory = storeClient.getInventory(
                JsonSchemaValidator.matchesJsonSchemaInClasspath("generic-schema.json"));
        Map<Object, Object> map = inventory.getMap("");
        Assertions.assertFalse(map.isEmpty(), "Expected non-empty inventory");
    }

    @ParameterizedTest
    @MethodSource("getOrderForPet")
    @Tag("positive")
    @DisplayName("Positive: Place an order for a pet")
    public void testPlaceOrder(OrderDto orderFromProvider) {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.OK));
        JsonPath jsonPath = storeClient.placeOrder(orderFromProvider);
        OrderDto orderFromResponse = jsonPath.getObject("", OrderDto.class);
        LocalDateTime dateTime1 =
                DateTimeUtil.toLocalDateTime(orderFromProvider.getShipDate(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime dateTime2 =
                DateTimeUtil.toLocalDateTime(orderFromResponse.getShipDate(), DateTime.DATE_TIME);
        String withoutZone1 = DateTimeUtil.localDateTimeToString(dateTime1, DateTime.DATE_TIME_WITHOUT_TIMEZONE);
        String withoutZone2 = DateTimeUtil.localDateTimeToString(dateTime2, DateTime.DATE_TIME_WITHOUT_TIMEZONE);
        orderFromProvider.setShipDate(withoutZone1);
        orderFromResponse.setShipDate(withoutZone2);
        Assertions.assertEquals(orderFromProvider, orderFromResponse, "Expected the same order");
    }

    @Test
    @Tag("negative")
    @DisplayName("Negative: Place an invalid order for a pet")
    public void testPlaceInvalidOrder() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.BAD_REQUEST));
        JsonPath jsonPath = storeClient.placeOrder("");
        Assertions.assertEquals("No data", jsonPath.get("message"), "Expected an error message");
    }

    @ParameterizedTest
    @MethodSource("getOrderForPet")
    @Tag("positive")
    @DisplayName("Positive: Find Purchase Order by ID")
    public void testFindOrderById(OrderDto orderFromProvider) {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.OK));
        storeClient.placeOrder(orderFromProvider);
        JsonPath jsonPath = storeClient.findOrderById(orderFromProvider.getId());
        OrderDto orderFromResponse = jsonPath.getObject("", OrderDto.class);
        LocalDateTime dateTime1 =
                DateTimeUtil.toLocalDateTime(orderFromProvider.getShipDate(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime dateTime2 =
                DateTimeUtil.toLocalDateTime(orderFromResponse.getShipDate(), DateTime.DATE_TIME);
        String withoutZone1 = DateTimeUtil.localDateTimeToString(dateTime1, DateTime.DATE_TIME_WITHOUT_TIMEZONE);
        String withoutZone2 = DateTimeUtil.localDateTimeToString(dateTime2, DateTime.DATE_TIME_WITHOUT_TIMEZONE);
        orderFromProvider.setShipDate(withoutZone1);
        orderFromResponse.setShipDate(withoutZone2);
        Assertions.assertEquals(orderFromProvider, orderFromResponse, "Expected the same order");
    }

    @Test
    @Tag("negative")
    @DisplayName("Negative: Find Non-existing Purchase Order by ID")
    public void testFindOrderByInvalidId() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.NOT_FOUND));
        long nonExistingOrderId = -999999;
        JsonPath response = storeClient.findOrderById(nonExistingOrderId);
        Assertions.assertEquals("Order not found", response.getString("message"),
                "Expected an error message");
    }

    @ParameterizedTest
    @MethodSource("getOrderForPet")
    @Tag("positive")
    @DisplayName("Positive: Delete Purchase Order by ID")
    public void testDeleteOrderById(OrderDto orderFromProvider) {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.OK));
        storeClient.placeOrder(orderFromProvider);
        JsonPath response = storeClient.deleteOrderById(orderFromProvider.getId());
        Assertions.assertEquals(orderFromProvider.getId(), response.getLong("message"),
                "Expected the same order ID");
    }

    @Test
    @Tag("negative")
    @DisplayName("Negative: Delete Non-existing Purchase Order by ID")
    public void testDeleteNonExistingOrderById() {
        Specifications.installSpecs(Specifications.reqSpec(URI, JSON), Specifications.resSpec(HttpStatus.NOT_FOUND));
        int nonExistingOrderId = 999999;
        JsonPath response = storeClient.deleteOrderById(nonExistingOrderId);
        Assertions.assertEquals("Order Not Found", response.get("message"),
                "Expected an error message");
    }

    private static Stream<OrderDto> getOrderForPet() {
        return Stream.of(OrderDataProvider.getOrderForPet());
    }
}
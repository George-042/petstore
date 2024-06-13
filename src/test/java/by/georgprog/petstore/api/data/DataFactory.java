package by.georgprog.petstore.api.data;

import java.util.Map;

public class DataFactory {

    public static Map<Object, Object> getOrderForPet() {
        return Map.of(
                "id", 4,
                "petId", 1,
                "quantity", 5,
                "shipDate", "2024-06-12T08:22:23.354Z",
                "status", "placed",
                "complete", true
        );
    }
}

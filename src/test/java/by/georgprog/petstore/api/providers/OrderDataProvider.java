package by.georgprog.petstore.api.providers;

import by.georgprog.petstore.api.dto.OrderDto;

public class OrderDataProvider {

    public static OrderDto getOrderForPet() {
        return new OrderDto(
                4L,
                1L,
                5,
                "2024-06-12T08:22:23.354Z",
                "placed",
                true
        );
    }
}

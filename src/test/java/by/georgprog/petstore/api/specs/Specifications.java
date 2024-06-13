package by.georgprog.petstore.api.specs;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specifications {

    public static RequestSpecification reqSpec(String uri, ContentType contentType) {
        return new RequestSpecBuilder().setBaseUri(uri).setContentType(contentType).build();
    }

    public static ResponseSpecification resSpec(int statusCode) {
        return new ResponseSpecBuilder().expectStatusCode(statusCode).build();
    }

    public static void installSpecs(RequestSpecification reqSpec, ResponseSpecification resSpec) {
        RestAssured.requestSpecification = reqSpec;
        RestAssured.responseSpecification = resSpec;
    }
}

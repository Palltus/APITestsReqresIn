package spec;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.URI;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class SpecMethods {
    public static RequestSpecification doReqSpec (String url) {
        return new RequestSpecBuilder()
                .setBaseUri(url)
                .setContentType(JSON)
                .log(URI)
                .build();
    }

    public static ResponseSpecification doResSpec (int statusCode){
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(BODY)
                .build();
    }

    public static void initSpecs (RequestSpecification request, ResponseSpecification response) {
        RestAssured.requestSpecification =request;
        RestAssured.responseSpecification = response;
    }
}

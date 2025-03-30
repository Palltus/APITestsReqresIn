
import config.TestConfig;
import dto.user.CreateNewUserForm;
import dto.user.User;

import dto.user.UserResponse;
import dto.user.UserUpdateResponse;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static spec.SpecByClass.reqSpec;
import static spec.SpecByClass.respSpec;


public class TestBase {
    private static final String BASE_URL = "https://reqres.in/api";
    private static final String CREATE_USER_ENDPOINT = "/users";

    @DisplayName("Получение всех юзеров со второй страницы")
    @Test
    void getAllUsersFromPage() {

        User expData = new User(7,
                "michael.lawson@reqres.in",
                "Michael",
                "Lawson",
                "https://reqres.in/img/faces/7-image.jpg");

        step("Получение ответа и формирование списка Юзеров", () -> {
            List<User> dataList = given(reqSpec)
                    .param("page", "2")
                    .when()
                    .get("users")
                    .then()
                    .spec(respSpec)
                    .body("page", equalTo(2))
                    .body("per_page", equalTo(6))
                    .body("total", equalTo(12))
                    .body("total_pages", equalTo(2))
                    .body("total_pages", equalTo(2))
                    .body("data.last_name", hasItem("Lawson"))
                    .extract().body().jsonPath().getList("data", User.class);

            step("Проверка вхождения числа ID юзера в ссылку на аватар", () -> {
                dataList.forEach(u -> Assertions.assertTrue(u.getAvatar().contains(u.getId().toString())));
            });
            step("Проверить что все эмейлы заканчиваются на reqres.in через /for", () -> {
                for (int i = 0; i < dataList.size(); i++) {
                    Assertions.assertTrue(dataList.get(i).getEmail().endsWith("@reqres.in"));
                }
            });
            step("Проверить что все эмейлы заканчиваются на reqres.in через stream", () -> {
                Assertions.assertTrue(dataList.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
            });
            step("Проверка 1-го объекта экземпляра User", () ->
                    Assertions.assertEquals(expData, dataList.get(0))
            );

            step("Проверка количества полученных юзеров", () ->
                    Assertions.assertEquals(6, dataList.size())
            );

            List<String> ids = dataList.stream().map(x -> x.getId().toString())
                    .sorted(Comparator.comparingInt(Integer::parseInt)).toList();

            step("Проверка заданной сортировки", () -> {
                List<Integer> ids1 = dataList.stream().map(User::getId).collect(Collectors.toList());
                assertThat(ids1, contains(7, 8, 9, 10, 11, 12));
            });

        });
    }

    @DisplayName("404 юзер не найден")
    @Test
    void testGetUserNotFound() {

        step("404 юзер не найден и пустое тело", () ->
                given(reqSpec)
                        .when().
                        get("users/23")
                        .then()
                        .statusCode(404)
                        .body(equalTo("{}"))
        );
    }


    @DisplayName("Проверка данных пользователя")
    @Test
    void testGetSingleUser() {

        step("Проверка данных пользователя и схемы", () ->
                given(reqSpec)
                        .when()
                        .get("users/2")
                        .then()
                        .statusCode(200)
                        .body("data.email", equalTo("janet.weaver@reqres.in"))
                        .body("data.keySet()", hasItems("id", "first_name", "last_name", "avatar"))
                        .body(matchesJsonSchemaInClasspath("userSchema.json"))
        );

    }

    @DisplayName("Создание пользователя")
    @Test
    void testCreateNewUser() {

        step("Создание пользователя", () -> {
            CreateNewUserForm user = new CreateNewUserForm("morpheus", "leader");
            UserResponse okRegistration = given(reqSpec)
                    .body(user)
                    .when()
                    .post("users")
                    .then()
                    .statusCode(201)
                    .body("name", equalTo(user.getNAME()))
                    .body("job", equalTo(user.getJOB()))
                    .body("id", notNullValue())
                    .extract().as(UserResponse.class);

            String actualTimeOfCreated = okRegistration.getCreatedAt().replaceAll(".{7}$", "");
            String expectedTimeOfCreated = Instant.now().toString().replaceAll(".{13}$", "");

            assertThat(expectedTimeOfCreated, equalTo(actualTimeOfCreated));
        });
    }

    @DisplayName("Update")
    @Test
    void updateUser() {

        String requestBody = """
                {
                    "name": "morpheus",
                    "job": "zion resident"
                }
                """;

        UserUpdateResponse response = given(reqSpec)
                .body(requestBody)
                .when()
                .put("users/2")
                .then()
                .statusCode(200)
                .extract().as(UserUpdateResponse.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getName()).isEqualTo("morpheus");
        softly.assertThat(response.getJob()).isEqualTo("zion r1esident");
        softly.assertThat(response.getUpdatedAt()).isNotNull()
                .satisfies(dateStr -> {
                    ZonedDateTime date = ZonedDateTime.parse(dateStr);
                    org.assertj.core.api.Assertions
                            .assertThat(date).isAfter(ZonedDateTime.now().minusMinutes(5));
                });
        softly.assertAll();

    }

    @Test
    void deleteUser_shouldReturn204WithEmptyBody() {
        final String DELETE_USER_ENDPOINT = "/users/2";

        given()
                .baseUri(BASE_URL)
                .when()
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .statusCode(204)  // Проверяем код ответа
                .body(emptyString());  // Проверяем, что тело ответа пустое
    }
    @DisplayName("с Config и проперти файлом")
    @Test
    void successfulRegistration_shouldReturnIdAndToken() {
        RestAssured.baseURI = TestConfig.getBaseUrl();
        String requestBody = String.format("""
        {
            "email": "%s",
            "password": "%s"
        }
        """, TestConfig.getValidEmail(), TestConfig.getValidPassword());

        given()
                .contentType(JSON)
                .body(requestBody)
                .log().uri()
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("token", notNullValue());
    }

}

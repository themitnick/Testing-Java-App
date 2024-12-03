package ci.ivb.testing.USER.ui;

import static org.junit.jupiter.api.Assertions.*;

import ci.ivb.testing.USER.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerWithTestcontainerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Container
    @ServiceConnection  //If you use this annotation, you don't need the comment part below
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
//            .withDatabaseName("")
//            .withUsername("")
//            .withPassword("");

//    @DynamicPropertySource
//    private static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//    }

    @Test
    @DisplayName("The PostgreSQL is created and is running")
    @Order(1)
    void containerIsRunningTest() {
        assertTrue(postgreSQLContainer.isCreated(), "PostgreSQL container has not created");
        assertTrue(postgreSQLContainer.isRunning(), "PostgreSQL container has not running");
    }

    @Test
    @DisplayName("User can be created")
    @Order(2)
    void createUser_whenValidUserDetailsProvide_returnsCreatedUserDetails() throws JSONException {
        //Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstname", "Konan");
        userDetailsRequestJson.put("lastname", "YAO");
        userDetailsRequestJson.put("email", "yao.konan@gmail.com");
        userDetailsRequestJson.put("password", "789654123");
        userDetailsRequestJson.put("repeatPassword", "789654123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<UserRest> createdUserDetailsEntity = testRestTemplate.postForEntity("/users", request, UserRest.class);
        UserRest createdUserDetails = createdUserDetailsEntity.getBody();

        //Assert
        assertEquals(HttpStatus.OK, createdUserDetailsEntity.getStatusCode());
        assertEquals(userDetailsRequestJson.getString("firstname"), createdUserDetails.getFirstName(), "Returned user's first name seems to be incorrect");
        assertEquals(userDetailsRequestJson.getString("lastname"), createdUserDetails.getLastName(), "Returned user's last name seems to be incorrect");
        assertEquals(userDetailsRequestJson.getString("email"), createdUserDetails.getEmail(), "Returned user's email seems to be incorrect");
        assertFalse(createdUserDetails.getUserId().trim().isEmpty(), "User id should not be empty");

    }

}

package ci.ivb.testing.USER.ui;

import ci.ivb.testing.USER.security.SecurityConstants;
import ci.ivb.testing.USER.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "/application-test.yml", properties = "server.port=8881")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    @Test
    void contextLoads() {
        System.out.println("Server port = " + serverPort);
    }

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void createUser_whenValidUserDetailsProvide_returnsCreatedUserDetails() throws JSONException {
        //Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstname", "Konan");
        userDetailsRequestJson.put("lastname", "YAO");
        userDetailsRequestJson.put("email", "yao.konan@gmail.com");
        userDetailsRequestJson.put("password", "789654123");
        userDetailsRequestJson.put("repeatPassword", "789654123");

//        String userDetailsRequestJson = """
//                {
//                    "firstname": "Konan",
//                    "lastname": "YAO",
//                    "email": "ab@gmail.com",
//                    "password": "8596244",
//                    "repeatPassword": "8596244"
//                }
//                """;

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

    @Test
    @DisplayName("GET /users requires JWT")
    @Order(2)
    void getUsers_whenMissingJWT_returns403() {
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

        //Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserRest>>() {
                }
        );

        //Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "HTTP Status 403 forbidden should have been returned");
    }

    @Test
    @DisplayName("/login works")
    @Order(3)
    void userLogin_whenValidCredentialsProvided_returnsJWTinAuthorizationHeader() throws JSONException {
        //Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "yao.konan@gmail.com");
        loginCredentials.put("password", "789654123");

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString());

        //Act
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/users/login", request, null);
        authorizationToken = response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status code should be 200");
        assertNotNull(authorizationToken, "Response should contain Authorization header with JWT");
        assertNotNull(response.getHeaders().getValuesAsList("UserID").get(0), "Response should contain UserID in a response header");

    }

    @Test
    @Order(4)
    @DisplayName("GET /users works")
    void  getUsers_whenValidJWTProvides_returnsUsers() {
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<Object> request = new HttpEntity<>(headers);

        //Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserRest>>() {
                });

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status should be 200");
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

}

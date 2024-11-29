package ci.ivb.testing.USER;

import ci.ivb.testing.USER.service.UsersService;
import ci.ivb.testing.USER.service.UsersServiceImpl;
import ci.ivb.testing.USER.shared.UserDto;
import ci.ivb.testing.USER.ui.controllers.UsersController;
import ci.ivb.testing.USER.ui.request.UserDetailsRequestModel;
import ci.ivb.testing.USER.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

@WebMvcTest(controllers = UsersController.class,
excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@AutoConfigureMockMvc(addFilters = false)
//@MockBean({UsersServiceImpl.class})
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    //@Autowired   ==> If you use this way, you should add @MockBean({UsersServiceImpl.class})
    @MockBean
    UsersService usersService;

    private UserDetailsRequestModel userDetailsRequestModel;

    @BeforeEach
    void setup() {
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Konan");
        userDetailsRequestModel.setLastName("YAO");
        userDetailsRequestModel.setEmail("konan.y@gmail.com");
        userDetailsRequestModel.setPassword("123456789");
        userDetailsRequestModel.setRepeatPassword("123456789");

    }

    @Test
    @DisplayName("User can be created")
    void createUser_whenValidUserDetailsProvide_returnsCreatedUserDetails() throws Exception {
        // Arrange
        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);

        //Assert
        assertEquals(userDetailsRequestModel.getFirstName(), createdUser.getFirstName(), "The returned user first name is most likely : ");
        assertEquals(userDetailsRequestModel.getLastName(), createdUser.getLastName(), "The returned user last name is incorrect ");
        assertEquals(userDetailsRequestModel.getEmail(), createdUser.getEmail(), "The returned user email is incorrect : ");
        assertFalse(createdUser.getUserId().isEmpty(), "userId should not be empty");
    }

    @Test
    @DisplayName("Try create user with first name is shorted than 2 characters")
    void createUser_whenInvalidUserDetailsProvide_returnsBadRequest() throws Exception {
        // Arrange
        userDetailsRequestModel.setFirstName("m");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), "Incorrect HTTP Status Code");
    }


}

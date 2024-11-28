package ci.ivb.testing.TDD;

import ci.ivb.testing.TDD.data.UserRepository;
import ci.ivb.testing.TDD.models.User;
import ci.ivb.testing.TDD.services.EmailNotificationServiceException;
import ci.ivb.testing.TDD.services.EmailVerificationServiceImpl;
import ci.ivb.testing.TDD.services.UserServiceException;
import ci.ivb.testing.TDD.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Mock
    EmailVerificationServiceImpl emailVerificationService;
    User userDetails;

    @BeforeEach()
    void beforeEachMethod() {
        userDetails = new User("Konan","YAO","konan.yao@gmail.com","785@azI95","785@azI95");
    }

    @Test
    void createUser_whenUserDetailsProvide_returnUserObjectTest() {
        //Arrange
        when(userRepository.save(any(User.class))).thenReturn(userDetails);
        //Act
        User user = userService.createUser(userDetails);

        //Assert
        assertNotNull(user, "The createUser() should not have return null");
        assertEquals("Konan", user.getFirstname(), "User firstname is incorrect.");
        assertEquals("YAO", user.getLastname(), "User lastname is incorrect.");
        assertEquals("konan.yao@gmail.com", user.getEmail(), "User email is incorrect.");
        assertNotNull(user.getId(), "User id is incorrect.");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @DisplayName("If save() method causes RuntimeException, a UserServiceException is thrown")
    @Test
    void testCreateUser_whenSaveMethodThrowsException_thenThrowsUserServiceException() {
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        assertThrows(UserServiceException.class, () -> {
            userService.createUser(userDetails);
        }, "Should have throw UserServiceException instead");
    }

    @DisplayName("EmailNotificationService is handled")
    @Test
    void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException() {
        when(userRepository.save(any(User.class))).thenReturn(userDetails);
        doThrow(EmailNotificationServiceException.class)
                .when(emailVerificationService)
                .scheduleEmailVerification(any(User.class));

        assertThrows(UserServiceException.class, ()-> {
            userService.createUser(userDetails);
        }, "Should have thrown UserServiceException instead");

        verify(emailVerificationService, times(1)).scheduleEmailVerification(any(User.class));
    }

    @DisplayName("Schedule email confirmation is executed")
    @Test
    void testCreateUser_whenUserCreated_schedulesEmailConfirmation() {
        when(userRepository.save(any(User.class))).thenReturn(userDetails);
        doCallRealMethod().when(emailVerificationService).scheduleEmailVerification(any(User.class));

        userService.createUser(userDetails);

        verify(emailVerificationService, times(1)).scheduleEmailVerification(any(User.class));
    }

    @Test
    void createUser_whenFirstnameIsEmpty_throwIllegalArgumentException() {
        String exceptedMessageException = "User's first name is empty";
        userDetails.setFirstname("");

        IllegalArgumentException throwMessage =  assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDetails);
        }, "Empty firstname should have caused an Illegal Argument Exception");

        assertEquals(exceptedMessageException, throwMessage.getMessage());
    }

    @Test
    void createUser_validate_whenPasswordMatchWithRepeatPasswordTest() {
        String password = "azerty";
        String repeatPassword = "azerty";

        boolean userValidated = userService.validatePassword(password, repeatPassword);

        assertTrue(userValidated, "User password validation should be true");
    }

    @Test
    void createUser_validate_whenPasswordNotMatchWithRepeatPasswordTest() {
        String password = "azerty";
        String repeatPassword = "azerty85";

        boolean userValidated = userService.validatePassword(password, repeatPassword);

        assertFalse(userValidated, "User password validation should be false");
    }

}

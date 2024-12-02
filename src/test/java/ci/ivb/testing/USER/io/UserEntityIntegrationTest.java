package ci.ivb.testing.USER.io;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

@DataJpaTest
public class UserEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    UserEntity user = new UserEntity();

    @BeforeEach
    void setup() {
        user.setUserId(UUID.randomUUID().toString());
        user.setFirstName("Konan");
        user.setLastName("YAO");
        user.setEmail("yao.konan@gmail.com");
        user.setEncryptedPassword("789456123");
    }

    @Test
    void userEntity_when_ValidUserDetailsProvide_shouldReturnsStoredUserDetails() {
        //Arrange

        //Act
        UserEntity userStored = testEntityManager.persistAndFlush(user);

        //Assert
        assertEquals(userStored.getUserId(), user.getUserId());
        assertEquals(1, userStored.getId());
        assertEquals(userStored.getFirstName(), user.getFirstName());
    }

    @Test
    void userEntity_whenFirstnameIsTooLOng_shouldThrowException() {
        //Arrange
        user.setFirstName("uyfhdoidlkgn866515165ffhfuifgbsdfbdfuyye568525erfhhyuhjrgfguyfjhvb9811578sg987fg6456fg6df67");

        //Assert & Act
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(user);
        }, "Was excepting a PersistenceException to be thrown");

    }
}

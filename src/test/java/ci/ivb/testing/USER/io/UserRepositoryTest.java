package ci.ivb.testing.USER.io;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    UsersRepository usersRepository;
    UserEntity user = new UserEntity();

    @BeforeEach
    void setup() {
        user.setUserId(UUID.randomUUID().toString());
        user.setFirstName("Konan");
        user.setLastName("YAO");
        user.setEmail("yao.konan@gmail.com");
        user.setEncryptedPassword("789456123");

        testEntityManager.persistAndFlush(user);
    }

    @Test
    void findByEmail_whenGivenCorrectEmail_returnsUserEntity() {
        //Act
        UserEntity userEntity = usersRepository.findByEmail(user.getEmail());

        //Assert
        assertEquals(user.getEmail(), userEntity.getEmail(), "The returned email address does not match the excepted value");
    }

    @Test
    void findByUserId_whenGivenCorrectUserId_returnsUserEntity() {
        //Act
        UserEntity userEntity = usersRepository.findByUserId(user.getUserId());

        //Assert
        assertEquals(userEntity.getUserId(), user.getUserId(), "The UserId should be match");
    }

    @Test
    void findUsersWithEmailEndWith_whenGivenEmailDomain_returnsUserWithGivenDomain() {
        //Arrange
        String emailDomain = "@gmail.com";

        //Act
        List<UserEntity> users = usersRepository.findUsersWithEmailEndingWith(emailDomain);

        //Assert
        assertEquals(1, users.size(), "There should be only one user in the list");
        assertTrue(users.getFirst().getEmail().endsWith(emailDomain));
    }
}

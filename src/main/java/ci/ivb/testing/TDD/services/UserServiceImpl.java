package ci.ivb.testing.TDD.services;

import ci.ivb.testing.TDD.data.UserRepository;
import ci.ivb.testing.TDD.models.User;

public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    EmailVerificationService emailVerificationService;
    public UserServiceImpl(
            UserRepository userRepository,
            EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public User createUser(User user) {
        if (user.getFirstname().isEmpty() || user.getFirstname() == null) {
            throw new IllegalArgumentException("User's first name is empty");
        }
//        if (validatePassword(user.getPassword(), user.getRepeatPassword())) {
//            throw new IllegalArgumentException("The password not match this repeated password");
//        }

        User isCreatedUser;
        try {
             isCreatedUser = userRepository.save(user);
        } catch (RuntimeException ex) {
            throw new UserServiceException(ex.getMessage());
        }
        if (isCreatedUser == null) throw new UserServiceException("Could not create user");

        try {
            emailVerificationService.scheduleEmailVerification(user);
        } catch (RuntimeException ex) {
            throw new UserServiceException(ex.getMessage());
        }

        return isCreatedUser;
    }

    public Boolean validatePassword(String password, String repeatPassword) {
        return password.equals(repeatPassword);
    }

    public void demo() {
        System.out.println("Demo metho");
    }
}

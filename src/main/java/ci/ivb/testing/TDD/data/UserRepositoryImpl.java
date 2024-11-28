package ci.ivb.testing.TDD.data;

import ci.ivb.testing.TDD.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    Map<String, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }
}

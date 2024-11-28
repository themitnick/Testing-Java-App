package ci.ivb.testing.TDD.data;

import ci.ivb.testing.TDD.models.User;

public interface UserRepository {
    User save(User user);
}

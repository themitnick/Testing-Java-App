package ci.ivb.testing.TDD.services;

import ci.ivb.testing.TDD.models.User;

public interface EmailVerificationService {
    void scheduleEmailVerification(User user);
}

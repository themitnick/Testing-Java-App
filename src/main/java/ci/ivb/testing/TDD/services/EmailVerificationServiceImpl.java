package ci.ivb.testing.TDD.services;

import ci.ivb.testing.TDD.models.User;

public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Override
    public void scheduleEmailVerification(User user) {
        // Put here different information
        System.out.println("Send email");
    }
}

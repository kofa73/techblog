package org.kovacstelekes;

import static org.kovacstelekes.AuthenticationResult.FAILED;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestableAuthenticationService {
    private Logger logger = LoggerFactory
            .getLogger(TestableAuthenticationService.class);
    private List<AuthenticationProvider> providers;

    @Autowired
    public void setProviders(List<AuthenticationProvider> providers) {
        this.providers = providers;
    }

    /**
     * Asks all providers to attempt authentication.
     *
     * @param userId
     *            the user ID
     * @param password
     *            the password
     * @return true if authenticated, false if not
     */
    public AuthenticationResult authenticate(String userId, String password) {
        AuthenticationResult result = FAILED;
        for (AuthenticationProvider provider : providers) {
            result = provider.authenticate(userId, password);
            if (result.isFinal()) {
                break;
            }
        }
        logger.info("Result of authenticating {}: {}", userId, result);
        return result;
    }

    void setLogger(Logger theLogger) {
        logger = theLogger;
    }
}

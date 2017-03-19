package org.kovacstelekes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kovacstelekes.AuthenticationResult.BLOCKED;
import static org.kovacstelekes.AuthenticationResult.FAILED;
import static org.kovacstelekes.AuthenticationResult.SUCCESSFUL;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

public class TestableAuthenticationServiceTest {

    private Logger logger;

    private TestableAuthenticationService service = new TestableAuthenticationService();

    private static final String USER = "user";
    private static final String DUMMY_PASSWORD = "";

    @Before
    public void setUp() throws Exception {
        logger = mock(Logger.class);
        service.setLogger(logger);
    }

    @Test
    public void testSuccessEvenIfFirstFailed() {
        AuthenticationResult result = authenticateWith(FAILED, SUCCESSFUL);

        assertThat(result, is(SUCCESSFUL));
        assertLogContains(USER, SUCCESSFUL);
    }

    @Test
    public void testBlockedStopsProcessing() {
        AuthenticationResult result = authenticateWith(BLOCKED, SUCCESSFUL);

        assertThat(result, is(BLOCKED));
        assertLogContains(USER, BLOCKED);
    }

    @Test
    public void testSuccessfulStopsProcessing() {
        AuthenticationResult result = authenticateWith(SUCCESSFUL, FAILED);

        assertThat(result, is(SUCCESSFUL));
        assertLogContains(USER, SUCCESSFUL);
    }

    @Test
    public void testNoProvidersMeansFailed() {
        AuthenticationResult result = authenticateWith();

        assertThat(result, is(FAILED));
        assertLogContains(USER, FAILED);
    }

    private AuthenticationResult authenticateWith(
            AuthenticationResult... results) {
        List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
        for (AuthenticationResult result : results) {
            AuthenticationProvider provider = mock(AuthenticationProvider.class);
            when(provider.authenticate(anyString(), anyString())).thenReturn(
                    result);
            providers.add(provider);
        }
        service.setProviders(providers);

        return service.authenticate(USER, DUMMY_PASSWORD);
    }

    private void assertLogContains(String user, AuthenticationResult result) {
        ArgumentCaptor<Object> param1Captor = ArgumentCaptor
                .forClass(Object.class);
        ArgumentCaptor<Object> param2Captor = ArgumentCaptor
                .forClass(Object.class);
        verify(logger).info(anyString(), param1Captor.capture(),
                param2Captor.capture());
        List<Object> logParams = Arrays.asList(param1Captor.getValue(),
                param2Captor.getValue());
        assertTrue(logParams.contains(user));
        assertTrue(logParams.contains(result));
    }
}

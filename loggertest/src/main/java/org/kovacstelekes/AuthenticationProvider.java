package org.kovacstelekes;

public interface AuthenticationProvider {

	AuthenticationResult authenticate(String userId, String password);

}

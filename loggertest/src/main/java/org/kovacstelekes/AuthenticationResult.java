package org.kovacstelekes;

public enum AuthenticationResult {
	FAILED(false), BLOCKED(true), SUCCESSFUL(true);

	private final boolean finalResult;
	private AuthenticationResult(boolean finalResult) {
		this.finalResult = finalResult;
	}
	public boolean isFinal() {
		return finalResult;
	}
}

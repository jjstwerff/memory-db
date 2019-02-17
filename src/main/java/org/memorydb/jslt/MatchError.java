package org.memorydb.jslt;

public class MatchError {
	private final String message;

	public MatchError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

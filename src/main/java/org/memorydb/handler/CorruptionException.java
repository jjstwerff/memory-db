package org.memorydb.handler;

public class CorruptionException extends RuntimeException {
	public CorruptionException(String message) {
		super(message);
	}

	public CorruptionException(Exception e) {
		super(e);
	}

	private static final long serialVersionUID = 1L;
}

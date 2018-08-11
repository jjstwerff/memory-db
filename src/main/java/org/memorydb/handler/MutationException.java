package org.memorydb.handler;

public class MutationException extends RuntimeException {
	public MutationException(String message) {
		super(message);
	}

	public MutationException(Exception e) {
		super(e);
	}

	private static final long serialVersionUID = 1L;
}

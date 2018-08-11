package org.memorydb.generate;

public class GenerateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public GenerateException(String message) {
		super(message);
	}

	public GenerateException(Exception e) {
		super(e);
	}
}

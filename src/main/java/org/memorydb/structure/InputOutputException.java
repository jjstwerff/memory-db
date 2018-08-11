package org.memorydb.structure;

public class InputOutputException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InputOutputException(Exception e) {
		super(e);
	}
}

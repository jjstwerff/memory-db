package org.memorydb.json;

class JsonException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final int line;
	private final int pos;
	private final JsonStack typeStack;

	public JsonException(String message, int line, int pos, JsonStack typeStack) {
		super(message);
		this.line = line;
		this.pos = pos;
		this.typeStack = typeStack;
	}

	public JsonException(String message) {
		super(message);
		typeStack = null;
		line = -1;
		pos = -1;
	}

	@Override
	public String toString() {
		return "JSON exception " + getMessage() + (typeStack == null ? "" : " at " + typeStack.toString() + " " + line + ":" + pos);
	}
}

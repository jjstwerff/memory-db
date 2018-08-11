package org.memorydb.json;

import java.io.Serializable;
import java.util.Arrays;

class JsonStack implements Serializable {
	private static final long serialVersionUID = 1L;
	private int[] stack;
	private int pos;
	private final JString string;

	private static final int STACK_TYPE = 0;
	private static final int STACK_POSITION = 1;
	private static final int STACK_LENGTH = 2;
	private static final int STACK_INDEX = 3;
	private static final int STACK_SIZE = 4;

	public JsonStack(JString string) {
		stack = new int[40];
		pos = 0;
		this.string = string;
	}

	public void clear() {
		pos = 0;
	}

	public void push(JsonType object, int position, int length) {
		if (pos > stack.length)
			stack = Arrays.copyOf(stack, stack.length * 2);
		stack[pos + STACK_TYPE] = object.ordinal();
		stack[pos + STACK_POSITION] = position;
		stack[pos + STACK_LENGTH] = length;
		stack[pos + STACK_INDEX] = 0;
		pos += STACK_SIZE;
	}

	public JsonType getType() {
		if (pos < STACK_SIZE)
			return JsonType.END;
		return getType(pos - STACK_SIZE);
	}
			
	private JsonType getType(int index) {
		return JsonType.values()[stack[index + STACK_TYPE]];
	}

	public int getPosition() {
		return stack[pos - STACK_SIZE + STACK_POSITION];
	}
	
	public int getLength() {
		return stack[pos - STACK_SIZE + STACK_LENGTH];
	}

	public int getIndex() {
		if (pos == 0)
			return 0;
		return stack[pos - STACK_SIZE + STACK_INDEX];
	}

	public void pop() {
		if (pos == 0)
			throw new JsonException("Stack already empty");
		pos -= STACK_SIZE;
	}

	public int size() {
		return pos / STACK_SIZE;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("$");
		for (int i = 0; i < pos; i += STACK_SIZE) {
			JsonType type = getType(i);
			if (type == JsonType.OBJECT) {
				res.append(".");
				string.setPosition(stack[i + STACK_POSITION], stack[i + STACK_LENGTH]);
				string.append(res);
			}
			res.append("[").append(stack[i + STACK_INDEX]).append("]");
		}
		return res.toString();
	}

	public String toPath() {
		StringBuilder res = new StringBuilder();
		res.append("$");
		for (int i = 0; i < pos; i += STACK_SIZE) {
			JsonType type = getType(i);
			if (type == JsonType.OBJECT) {
				res.append(".");
				string.setPosition(stack[i + STACK_POSITION], stack[i + STACK_LENGTH]);
				string.append(res);
			} else if (type == JsonType.ARRAY) {
				res.append("[").append(stack[i + STACK_INDEX]).append("]");
			}
		}
		return res.toString();
	}

	public void increaseIndex() {
		stack[pos - STACK_SIZE + STACK_INDEX] += 1;
	}

	public void setPosition(int fieldStart, int fieldLength) {
		if (getType() != JsonType.OBJECT)
			throw new JsonException("Expect an object");
		stack[pos - STACK_SIZE + STACK_POSITION] = fieldStart;
		stack[pos - STACK_SIZE + STACK_LENGTH] = fieldLength;
	}
}

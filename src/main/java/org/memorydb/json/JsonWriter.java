package org.memorydb.json;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonWriter {
	private static final char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private final SimpleDateFormat format;
	private final StringBuffer writer;
	private final JsonStack stack;
	private final char[] numberResult = new char[20];

	public JsonWriter() {
		writer = new StringBuffer();
		stack = new JsonStack(new JString(writer));
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	public void write(Object value) {
		write(null, value);
	}

	public void write(String key, Object value) {
		if (stack.size() > 0) {
			currentType(key);
			if (stack.getIndex() > 0)
				writer.append(',');
		}
		int fieldStart = writer.length();
		int fieldLength = -1;
		if (key != null) {
			escape(key);
			fieldLength = key.length();
			writer.append(':');
		}
		writeStuffs(value);
		if (stack.size() > 0) {
			stack.increaseIndex();
			if (fieldLength > -1)
				stack.setPosition(fieldStart + 1, fieldLength);
		}
	}

	private void writeStart(String key) {
		if (stack.getIndex() > 0)
			writer.append(',');
		int fieldStart = writer.length();
		int fieldLength = -1;
		if (key != null) {
			escape(key);
			fieldLength = key.length();
			writer.append(':');
		}
		if (fieldLength > -1)
			stack.setPosition(fieldStart + 1, fieldLength);
	}

	private void currentType(String key) {
		if (stack.size() == 0)
			throw new JsonException("Start an Array of Object first");
		JsonType type = stack.getType();
		if (type == JsonType.ARRAY && key != null)
			throw new JsonException("Putting key-value pairs in a list isn't allowed. Start an Object instead.");
	}

	public void startObject() {
		internalStartObject(null);
	}

	public void startObject(String key) {
		if (key == null)
			throw new JsonException("Use 'startObject()' instead of 'startObject(null)'.");
		internalStartObject(key);
	}

	private void internalStartObject(String key) {
		int fieldStart = writer.length();
		int fieldLength = -1;
		if (key != null)
			fieldLength = key.length();
		writeStart(key);
		writer.append('{');
		stack.push(JsonType.OBJECT, fieldStart + 1, fieldLength);
	}

	public void startArray() {
		internalStartArray(null);
	}

	public void startArray(String key) {
		if (key == null)
			throw new JsonException("Use 'startArray()' instead of 'startArray(null)'.");
		internalStartArray(key);
	}

	private void internalStartArray(String key) {
		int fieldStart = writer.length();
		int fieldLength = -1;
		if (key != null)
			fieldLength = key.length();
		writeStart(key);
		writer.append('[');
		stack.push(JsonType.ARRAY, fieldStart + 1, fieldLength);
	}

	public void end() {
		if (stack.size() == 0)
			throw new JsonException("Reached top level element already.");
		JsonType pop = stack.getType();
		stack.pop();
		if (pop == JsonType.ARRAY)
			writer.append("]");
		else if (pop == JsonType.OBJECT)
			writer.append("}");
		if (stack.size() > 0)
			stack.increaseIndex();
	}

	public void end(int levels) {
		for (int i = 0; i < levels; i++)
			end();
	}

	public void finish() {
		if (stack.size() != 0)
			throw new JsonException("Unclosed elements left on stack. (" + stack.size() + ")");
	}

	private void showDouble(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d))
			writer.append("null");
		else {
			writer.append(d);
		}
	}

	private void writeStuffs(Object o) {
		if (o instanceof Double) {
			showDouble((Double) o);
		} else if (o instanceof Date) {
			format.format((Date) o, writer, new FieldPosition(0));
		} else if (o == null) {
			writer.append("null");
		} else if (o instanceof Integer) {
			writer.append(((Integer) o).intValue());
		} else if (o instanceof Boolean) {
			writer.append((Boolean) o ? "true" : "false");
		} else if (o instanceof Long) {
			writer.append(((Long) o).longValue());
		} else
			escape(o.toString());
	}

	private void escape(String string) {
		if (string == null || string.length() == 0) {
			writer.append("\"\"");
			return;
		}
		char ch = 0;
		char old = 0;
		writer.append('"');
		for (int i = 0; i < string.length(); i += 1) {
			char c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				writer.append('\\');
				writer.append(c);
				break;
			case '/':
				if (old == '<')
					writer.append('\\');
				writer.append(c);
				break;
			case '\b':
				writer.append("\\b");
				break;
			case '\t':
				writer.append("\\t");
				break;
			case '\n':
				writer.append("\\n");
				break;
			case '\f':
				writer.append("\\f");
				break;
			case '\r':
				writer.append("\\r");
				break;
			default:
				hexChar(c);
			}
			old = ch;
		}
		writer.append('"');
	}

	private void hexChar(char c) {
		if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
			writer.append("\\u");
			int i = 4;
			for (int j = 0; j < 4; j++)
				numberResult[j] = '0';
			while (c > 0) {
				numberResult[i--] = HEXCHAR[c & 15];
				c >>>= 4;
			}
			writer.append(numberResult);
		} else
			writer.append(c);
	}

	public StringBuffer getWriter() {
		return writer;
	}

	public String getPath() {
		return stack.toString();
	}
}

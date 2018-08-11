package org.memorydb.json;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.structure.InputOutputException;

public class JsonReader implements Iterator<JsonType> {
	private String data;
	private final JsonStack stack;
	private int pos;
	private int line;
	private int linePos;
	private int fieldStart;
	private int fieldLength;
	private final JString field;
	private final SimpleDateFormat format;

	public JsonReader(String data) {
		stack = new JsonStack(new JString(data));
		field = new JString(data);
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		setData(data);
	}

	private void setData(String data) {
		this.data = data;
		this.pos = 0;
		stack.clear();
		fieldStart = 0;
		fieldLength = 0;
		line = 1;
		linePos = 0;
	}

	public void error(String text) {
		throw new JsonException(text, line, pos - linePos, stack);
	}

	public int parseInt() {
		return parseInt(10);
	}

	private int parseInt(int radix) {
		int result = 0;
		boolean negative = false;
		int limit = -Integer.MAX_VALUE;
		int multmin;
		int digit;

		char firstChar = data.charAt(pos);
		if (firstChar < '0') { // Possible leading "+" or "-"
			if (firstChar == '-') {
				negative = true;
				limit = Integer.MIN_VALUE;
			} else if (firstChar != '+')
				throw new NumberFormatException();
			pos++;
			if (isAfterNumber(pos)) // Cannot have lone "+" or "-"
				throw new NumberFormatException();
		}
		multmin = limit / radix;
		while (!isAfterNumber(pos)) {
			// Accumulating negatively avoids surprises near MAX_VALUE
			digit = Character.digit(data.charAt(pos++), radix);
			if (digit < 0)
				throw new NumberFormatException();
			if (result < multmin)
				throw new NumberFormatException();
			result *= radix;
			if (result < limit + digit)
				throw new NumberFormatException();
			result -= digit;
		}
		return negative ? result : -result;
	}

	private boolean isAfterNumber(int chpos) {
		if (chpos >= data.length())
			return true;
		char ch = data.charAt(chpos);
		return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r' || ch == ',' || ch == ']' || ch == '}';
	}

	public long parseLong() {
		return parseLong(10);
	}

	public boolean isRoundNumber() {
		int npos = pos;
		while (!isAfterNumber(npos)) {
			char charAt = data.charAt(npos);
			if (charAt == '.' || charAt == 'e')
				return false;
			npos++;
		}
		return true;
	}

	private long parseLong(int radix) {
		long result = 0;
		boolean negative = false;
		long limit = -Long.MAX_VALUE;
		long multmin;
		int digit;

		char firstChar = data.charAt(pos);
		if (firstChar < '0') { // Possible leading "+" or "-"
			if (firstChar == '-') {
				negative = true;
				limit = Long.MIN_VALUE;
			} else if (firstChar != '+')
				throw new NumberFormatException();
			pos++;
			if (isAfterNumber(pos)) // Cannot have lone "+" or "-"
				throw new NumberFormatException();
		}
		multmin = limit / radix;
		while (!isAfterNumber(pos)) {
			// Accumulating negatively avoids surprises near MAX_VALUE
			digit = Character.digit(data.charAt(pos++), radix);
			if (digit < 0)
				throw new NumberFormatException();
			if (result < multmin)
				throw new NumberFormatException();
			result *= radix;
			if (result < limit + digit)
				throw new NumberFormatException();
			result -= digit;
		}
		return negative ? result : -result;
	}

	public double parseDouble() {
		int min = pos;
		int max = pos;
		while (!isAfterNumber(max))
			max++;
		pos = max;
		return Double.parseDouble(data.substring(min, max));
	}

	private void skipWhiteSpace() {
		if (pos == data.length())
			return;
		char charAt = data.charAt(pos);
		while (charAt == ' ' || charAt == '\n' || charAt == '\t' || charAt == '\r') {
			if (charAt == '\n') {
				line++;
				linePos = pos + 1;
			}
			pos += 1;
			if (pos == data.length())
				return;
			charAt = data.charAt(pos);
		}
	}

	public Date parseDate() {
		Date result = format.parse(data, new ParsePosition(pos));
		while (pos < data.length() && data.charAt(pos) != '"')
			pos++;
		return result;
	}

	private int scan() {
		int i = pos;
		while (true) {
			char ch = data.charAt(i++);
			if (ch == '"')
				return i;
			if (ch == '\\')
				i++;
		}
	}

	char charAt(int index) {
		return data.charAt(index);
	}

	public void parseString(StringBuilder into) {
		pos = parseString(data, pos, into);
	}

	public String parseString() {
		StringBuilder into = new StringBuilder();
		parseString(into);
		return into.toString();
	}

	static int parseString(CharSequence data, int index, Appendable into) {
		if (index >= data.length())
			return index;
		while (true) {
			char ch = data.charAt(index++);
			if (ch == '"')
				return index;
			if (ch == '\\') {
				if (index >= data.length())
					throw new JsonException("Unknown token");
				char tokenCh = data.charAt(index++);
				if (tokenCh == 'u') {
					int val = 0;
					for (int k = 0; k < 4; k++) {
						if (index >= data.length())
							throw new NumberFormatException("Cannot interpret JSON hex character");
						char hex = data.charAt(index++);
						val = hexChar(val, hex);
					}
					ch = (char) val;
				} else
					ch = getToken(tokenCh);
			}
			try {
				into.append(ch);
			} catch (IOException e) {
				throw new InputOutputException(e);
			}
		}
	}

	private static int hexChar(int val, char hex) {
		if (hex >= '0' && hex <= '9')
			val = (val << 4) + hex - '0';
		else if (hex >= 'A' && hex <= 'F')
			val = (val << 4) + hex - 'A' + 10;
		else if (hex >= 'a' && hex <= 'f')
			val = (val << 4) + hex - 'a' + 10;
		else
			throw new NumberFormatException("Cannot interpret JSON hex character");
		return val;
	}

	private static char getToken(char tok) {
		switch (tok) {
		case '\\':
		case '/':
		case '"':
			return tok;
		case 'b':
			return '\b';
		case 't':
			return '\t';
		case 'r':
			return '\r';
		case 'n':
			return '\n';
		case 'f':
			return '\f';
		default:
			throw new JsonException("Unknown token");
		}
	}

	public Iterable<JsonType> startObject() {
		stack.push(JsonType.OBJECT, fieldStart, fieldLength);
		skipWhiteSpace();
		if (data.charAt(pos++) != '{')
			throw new JsonException("Did not parse an object");
		return () -> JsonReader.this;
	}

	public Iterable<JsonType> startArray() {
		stack.push(JsonType.ARRAY, fieldStart, fieldLength);
		skipWhiteSpace();
		if (data.charAt(pos++) != '[')
			throw new JsonException("Did not parse an array");
		return () -> JsonReader.this;
	}

	public void end() {
		skipWhiteSpace();
		JsonType pop = stack.getType();
		stack.pop();
		char ch = data.charAt(pos++);
		if (pop == JsonType.OBJECT) {
			if (ch != '}')
				error("Expected an object end");
		} else if (pop == JsonType.ARRAY) {
			if (ch != ']')
				error("Expected an array end");
		} else
			throw new JsonException("Unknown stack element");
		field.setPosition(0, -1);
	}

	private JsonType getFieldType() {
		skipWhiteSpace();
		if (data.charAt(pos++) != '"')
			error("Expect a field inside an object");
		fieldStart = pos;
		fieldLength = scan() - fieldStart - 1;
		field.setPosition(fieldStart, fieldLength);
		stack.setPosition(fieldStart, fieldLength);
		pos += fieldLength + 1;
		skipWhiteSpace();
		if (data.charAt(pos++) != ':')
			error("Expect a colon after a field name");
		return getType();
	}

	public JString getField() {
		return field;
	}

	public JsonType getType() {
		skipWhiteSpace();
		if (pos == data.length())
			return JsonType.END;
		char charAt = data.charAt(pos);
		if (charAt == '"') {
			pos++;
			return JsonType.STRING;
		}
		if (data.startsWith("true", pos)) {
			pos += 4;
			return JsonType.TRUE;
		}
		if (data.startsWith("false", pos)) {
			pos += 5;
			return JsonType.FALSE;
		}
		if (data.startsWith("null", pos)) {
			pos += 4;
			return JsonType.NULL;
		}
		if (charAt == '{') {
			return JsonType.OBJECT;
		}
		if (charAt == '[') {
			return JsonType.ARRAY;
		}
		if (charAt == '-' || (charAt >= '0' && charAt <= '9'))
			return JsonType.NUMBER;
		error("Syntax error");
		return JsonType.END;
	}

	public void finish() {
		skipWhiteSpace();
		if (stack.size() != 0)
			error("Unclosed elements left on stack. (" + stack.size() + ")");
		if (pos != data.length())
			error("Still data left on input");
	}

	@Override
	public boolean hasNext() {
		skipWhiteSpace();
		return pos < data.length() && data.charAt(pos) != ']' && data.charAt(pos) != '}';
	}

	@Override
	public JsonType next() {
		if (pos == data.length())
			throw new NoSuchElementException();
		if (data.charAt(pos) == ']' || data.charAt(pos) == '}')
			return JsonType.END;
		skipWhiteSpace();
		if (pos < data.length() && data.charAt(pos) == ',') {
			pos++;
			stack.increaseIndex();
		}
		if (stack.getType() == JsonType.OBJECT)
			return getFieldType();
		field.setPosition(0, -1);
		return getType();
	}

	public String getPath() {
		return stack.toString();
	}
}

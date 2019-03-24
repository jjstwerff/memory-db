package org.memorydb.file;

import java.io.IOException;
import java.time.LocalDateTime;

import org.memorydb.structure.DateTime;
import org.memorydb.structure.MemoryRecord;

public class Write {
	private static final int MAX_LENGTH = 120;
	private static final int MAX_STRING = 80;
	private final Appendable writer;
	private int len; // length of current row
	private int indent;
	private StringBuilder buffer = new StringBuilder();

	public Write(Appendable writer) {
		this.writer = writer;
		len = 0;
		indent = 0;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public int getIndent() {
		return indent;
	}

	public Write field(String name, int value) {
		if (value != Integer.MIN_VALUE)
			strField(name, Integer.toString(value));
		return this;
	}

	public Write field(String name, MemoryRecord value) {
		if (value != null && value.rec() != 0) {
			strField(name, "");
			append("{");
			String keys = value.keys();
			for (int i = 0; i < keys.length(); i++)
				writeChar(keys.charAt(i), true);
			append("}");
		}
		return this;
	}

	public Write field(String name, Object value) {
		if (value == null || (value instanceof Double && Double.isNaN((double) value)) || (value instanceof Long && (long) value == Long.MIN_VALUE)) {
			return this; // skip null fields
		}
		if (value instanceof Long || value instanceof Short || value instanceof Double || value instanceof Byte) {
			strField(name, value.toString());
			return this;
		}
		buffer.setLength(0);
		buffer.append(value.toString());
		return field(name, buffer);
	}

	public Write field(String name, LocalDateTime value) {
		buffer.setLength(0);
		DateTime.toString(value, buffer);
		strField(name, buffer.toString());
		return this;
	}

	public void strField(String name, String value) {
		lineStart();
		int valLength = value == null ? 0 : value.length();
		singleField(name, value, valLength);
	}

	public Write field(String name, StringBuilder value) {
		lineStart();
		if (value.length() > MAX_STRING || isMulti(value))
			multiLine(name, value);
		else
			singleField(name, value, value.length());
		return this;
	}

	private void append(String str) {
		try {
			writer.append(str);
		} catch (IOException e) {
			// fall on the ground
		}
	}

	private void append(char str) {
		try {
			writer.append(str);
		} catch (IOException e) {
			// fall on the ground
		}
	}

	private void lineStart() {
		if (len == 0) {
			for (int i = 0; i < indent; i++)
				append("  ");
			len = indent * 2;
		} else {
			append(", ");
			len += 2;
		}
	}

	private void multiLine(String name, StringBuilder value) {
		append(name);
		append("=\n");
		int c = 0;
		int p = lineEnd(value, 0);
		while (true) {
			for (int i = 0; i <= indent; i++)
				append("  ");
			if (p == -1) {
				for (int i = c; i < value.length(); i++)
					writeChar(value.charAt(i), false);
			} else {
				for (int i = c; i < p; i++)
					writeChar(value.charAt(i), false);
			}
			c = p + 1;
			p = lineEnd(value, p + 1);
			if (c >= p)
				break;
			append("\n");
		}
		len = 0;
	}

	private void singleField(String name, CharSequence value, int valLength) {
		if (len + name.length() + 1 + valLength > MAX_LENGTH) {
			append("\n");
			for (int i = 0; i < indent; i++)
				append("  ");
			append("& ");
			len = indent * 2 + 2;
		} else if (len == 0) {
			for (int i = 0; i < indent; i++)
				append("  ");
			len = indent * 2;
		}
		append(name);
		if (value == null)
			append("!");
		else {
			append("=");
			for (int i = 0; i < valLength; i++)
				writeChar(value.charAt(i), true);
		}
		len += name.length() + 1 + valLength;
	}

	private int lineEnd(StringBuilder value, int pos) {
		int end = value.indexOf("\n", pos);
		return end == -1 ? value.length() : end;
	}

	private void writeChar(char ch, boolean full) {
		if (ch == '\t') {
			append("\\t");
			return;
		}
		if (ch == '\r') {
			append("\\r");
			return;
		}
		if (ch == '\f') {
			append("\\f");
			return;
		}
		if (ch == '\b') {
			append("\\b");
			return;
		}
		if (full && (ch == ',' || ch == '\\' || ch == '}' || ch == '{'))
			append("\\");
		append(ch);
	}

	private boolean isMulti(StringBuilder value) {
		for (int i = 0; i < value.length(); i++)
			if (value.charAt(i) == '\n')
				return true;
		return false;
	}

	public void endRecord() {
		append("\n");
		len = 0;
	}

	@Override
	public String toString() {
		return writer.toString();
	}

	public void sub(String string) {
		strField(string, "[\n");
		indent++;
		len = 0;
	}

	public void endSub() {
		indent--;
		for (int i = 0; i < indent; i++)
			append("  ");
		append("]");
		len = indent * 2 + 1;
	}
}

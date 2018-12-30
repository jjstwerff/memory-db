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

	public Write field(String name, int value) throws IOException {
		if (value != Integer.MIN_VALUE)
			strField(name, Integer.toString(value));
		return this;
	}

	public Write field(String name, MemoryRecord value) throws IOException {
		if (value != null && value.getRec() != 0) {
			strField(name, "");
			writer.append("{");
			String keys = value.keys();
			for (int i = 0; i < keys.length(); i++)
				writeChar(keys.charAt(i), true);
			writer.append("}");
		}
		return this;
	}

	public Write field(String name, Object value) throws IOException {
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

	public Write field(String name, LocalDateTime value) throws IOException {
		buffer.setLength(0);
		DateTime.toString(value, buffer);
		strField(name, buffer.toString());
		return this;
	}

	public void strField(String name, String value) throws IOException {
		lineStart();
		int valLength = value == null ? 0 : value.length();
		singleField(name, value, valLength);
	}

	public Write field(String name, StringBuilder value) throws IOException {
		lineStart();
		if (value.length() > MAX_STRING || isMulti(value))
			multiLine(name, value);
		else
			singleField(name, value, value.length());
		return this;
	}

	private void lineStart() throws IOException {
		if (len == 0) {
			for (int i = 0; i < indent; i++)
				writer.append("  ");
			len = indent * 2;
		} else {
			writer.append(", ");
			len += 2;
		}
	}

	private void multiLine(String name, StringBuilder value) throws IOException {
		writer.append(name).append("=\n");
		int c = 0;
		int p = lineEnd(value, 0);
		while (true) {
			for (int i = 0; i <= indent; i++)
				writer.append("  ");
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
			writer.append("\n");
		}
		len = 0;
	}

	private void singleField(String name, CharSequence value, int valLength) throws IOException {
		if (len + name.length() + 1 + valLength > MAX_LENGTH) {
			writer.append("\n");
			for (int i = 0; i < indent; i++)
				writer.append("  ");
			writer.append("& ");
			len = indent * 2 + 2;
		} else if (len == 0) {
			for (int i = 0; i < indent; i++)
				writer.append("  ");
			len = indent * 2;
		}
		writer.append(name);
		if (value == null)
			writer.append("!");
		else {
			writer.append("=");
			for (int i = 0; i < valLength; i++)
				writeChar(value.charAt(i), true);
		}
		len += name.length() + 1 + valLength;
	}

	private int lineEnd(StringBuilder value, int pos) {
		int end = value.indexOf("\n", pos);
		return end == -1 ? value.length() : end;
	}

	private void writeChar(char ch, boolean full) throws IOException {
		if (ch == '\t') {
			writer.append("\\t");
			return;
		}
		if (ch == '\r') {
			writer.append("\\r");
			return;
		}
		if (ch == '\f') {
			writer.append("\\f");
			return;
		}
		if (ch == '\b') {
			writer.append("\\b");
			return;
		}
		if (full && (ch == ',' || ch == '\\' || ch == '}' || ch == '{'))
			writer.append("\\");
		writer.append(ch);
	}

	private boolean isMulti(StringBuilder value) {
		for (int i = 0; i < value.length(); i++)
			if (value.charAt(i) == '\n')
				return true;
		return false;
	}

	public void endRecord() throws IOException {
		writer.append("\n");
		len = 0;
	}

	@Override
	public String toString() {
		return writer.toString();
	}

	public void sub(String string) throws IOException {
		strField(string, "[\n");
		indent++;
		len = 0;
	}

	public void endSub() throws IOException {
		indent--;
		for (int i = 0; i < indent; i++)
			writer.append("  ");
		writer.append("]");
		len = indent * 2 + 1;
	}
}

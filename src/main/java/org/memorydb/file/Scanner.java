package org.memorydb.file;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import org.memorydb.structure.InputOutputException;

public class Scanner implements AutoCloseable {
	private final Path file;
	private final FileChannel channel;
	private int linenr;
	private MappedByteBuffer buffer;
	private int pos;
	private int lineStart;
	private int capacity;
	private boolean error;

	public Scanner(Path file) {
		this.file = file;
		try {
			this.channel = FileChannel.open(file);
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
		this.capacity = buffer.capacity();
		this.pos = 0;
		this.linenr = 1;
		this.lineStart = 0;
		error = false;
	}

	@Override
	public void close() {
		try {
			channel.close();
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
	}

	public void error(String message) {
		error = true;
		throw new StructureError(message + " in " + file.toString() + ":" + linenr + "/" + (1 + pos - lineStart));
	}

	public int getLinenr() {
		return linenr;
	}

	public int getCharPos() {
		return pos - lineStart;
	}

	public Path getFile() {
		return file;
	}

	public boolean isError() {
		return error;
	}

	public FileChannel getChannel() {
		return channel;
	}

	public boolean eof() {
		return pos >= capacity;
	}

	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		int oldPos = pos;
		int back = 0;
		if (pos > 0 && (pos >= capacity || buffer.get(pos) == 10))
			pos--;
		back = move2LinesBack(back);
		gather5Lines(data, oldPos, back);
		pos = oldPos;
		return data.toString();
	}

	private int move2LinesBack(int back) {
		for (int i = 0; i < 3; i++) {
			while (pos > 0 && buffer.get(pos) != '\n')
				pos--;
			if (i < 2 && pos > 0) {
				pos--; // move back over the newline
				back++;
			}
		}
		if (back > 0 && pos != 0)
			pos++;
		return back;
	}

	private void gather5Lines(StringBuilder data, int oldPos, int back) {
		for (int i = 2 - back; i < 5; i++) {
			data.append(String.format("%05d:%05d ", linenr + i - 2, pos));
			while (pos < capacity && buffer.get(pos) != '\n')
				data.append(getChar());
			if (pos < capacity)
				data.append(getChar()); // eat the line end
			if (i == 2) { // under the center (=current) line
				data.append(".....:..... ");
				for (int p = 0; p < oldPos - lineStart; p++)
					data.append(" ");
				data.append("^\n");
			}
		}
	}

	public char getChar() {
		if (pos >= capacity)
			return '\0';
		int b0 = buffer.get(pos++) & 0xFF;
		if (b0 < 0xC0 || b0 >= 0xF8 || pos == capacity)
			return (char) b0;
		int b1 = buffer.get(pos++) & 0x3F;
		if (b0 < 0xE0 || pos == capacity)
			return (char) (((b0 & 0x1F) << 6) + b1);
		int b2 = buffer.get(pos++) & 0x3F;
		if (b0 < 0xF0 || pos == capacity)
			return (char) (((b0 & 0x0F) << 12) + (b1 << 6) + b2);
		int b3 = buffer.get(pos++) & 0x3F;
		return (char) (((b0 & 0x07) << 18) + (b1 << 12) + (b2 << 6) + b3);
	}

	public boolean field(StringBuilder res) {
		int length = res.length();
		skipWhiteSpace();
		if (pos >= capacity)
			return false;
		char ch = (char) buffer.get(pos);
		if ((ch >= '0' && ch <= '9') || ch == '=')
			error("Expecting an identifier");
		while (true) {
			if (pos == capacity)
				break;
			int oldPos = pos;
			ch = getChar();
			if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_')) {
				pos = oldPos;
				break;
			}
			res.append(ch);
		}
		return res.length() > length;
	}

	public void data(StringBuilder str) {
		while (true) {
			if (pos == capacity)
				break;
			int oldPos = pos;
			char ch = getChar();
			if (ch == '\\') {
				char next = getChar();
				scanToken(str, next);
			} else {
				if (ch == ',' || ch == '{' || ch == '}' || ch == '\n') {
					pos = oldPos;
					break;
				}
				str.append(ch);
			}
		}
	}

	private void scanToken(StringBuilder str, char next) {
		switch (next) {
		case '\0': // end of data
			error("Escape token at the end of the text");
			break;
		case '\\': // escape the backslash itself
			str.append('\\');
			break;
		case ',': // comma token is the separator to the next field
			str.append(',');
			break;
		case '}': // special token for relations
			str.append('}');
			break;
		case '{': // special token for relations
			str.append('{');
			break;
		case 't': // tab token
			str.append('\t');
			break;
		case 'r': // carriage return token
			str.append('\r');
			break;
		case 'n': // newline token, in writing this will use a multi-line string
			str.append('\n');
			break;
		case 'b': // backspace token
			str.append('\b');
			break;
		case 'f': // form feed token
			str.append('\f');
			break;
		default:
			error("Unknown escaped token");
		}
	}

	public void multiLine(StringBuilder builder, int indent) {
		if (peek("\n"))
			skip();
		boolean line = true;
		while (true) {
			if (line) {
				if (!checkIndent(indent)) {
					if (builder.length() > 0)
						builder.setLength(builder.length() - 1);
					break;
				}
				line = false;
			}
			if (pos >= capacity)
				return;
			char ch = getChar();
			addChar(builder, ch);
			if (ch == '\n')
				line = true;
		}
	}

	private boolean checkIndent(int indent) {
		for (int ind = 0; ind < indent * 2; ind++) {
			if (pos >= capacity || buffer.get(pos) != ' ')
				return false;
			pos++;
		}
		return true;
	}

	private void addChar(StringBuilder builder, char ch) {
		if (ch == '\\') {
			char next = getChar();
			scanToken(builder, next);
		} else
			builder.append(ch);
	}

	public boolean matches(String string) {
		assert !string.contains("\n");
		skipWhiteSpace();
		return internPeek(string);
	}

	public void expect(String string) {
		if (!matches(string))
			error("Expecting: " + string);
	}

	public void expect(String string, String msg) {
		if (!matches(string))
			error("Expect a '" + string + "' " + msg);
	}

	public boolean peek(String string) {
		int orig = pos;
		if (internPeek(string)) {
			pos = orig;
			return true;
		}
		return false;
	}

	public boolean hasNumber() {
		int orig = pos;
		char ch = getChar();
		boolean result = ch >= '0' && ch <= '9';
		pos = orig;
		return result;
	}

	public long parseLong() {
		if (matches("0x"))
			return parseLong(16);
		if (matches("0o"))
			return parseLong(8);
		if (matches("0b"))
			return parseLong(2);
		return parseLong(10);
	}

	public int parseSimpleNumber() {
		int result = 0;
		int last = pos;
		char ch = getChar();
		while ((ch >= '0' && ch <= '9')) {
			result = result * 10 + Character.digit(ch, 10);
			last = pos;
			ch = getChar();
		}
		pos = last;
		return result;
	}

	public long parseLong(int radix) {
		long result = 0;
		boolean negative = false;
		long limit = -Long.MAX_VALUE;
		long multmin;
		int digit;
		int last = pos;
		char ch = getChar();
		if (ch < '0') { // Possible leading "+" or "-"
			if (ch == '-') {
				negative = true;
				limit = Long.MIN_VALUE;
			} else if (ch != '+')
				error("Number formating error");
			ch = getChar();
			if (ch < '0' || ch > '9')
				error("Number formating error");
		}
		multmin = limit / radix;
		while (ch >= '0' && ch <= '9' || ch == '_'
				|| radix == 16 && (ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F')) {
			// Accumulating negatively avoids surprises near MAX_VALUE
			digit = Character.digit(ch, radix);
			if (digit < 0 || result < multmin)
				error("Number formatting error");
			result *= radix;
			if (result < limit + digit)
				error("Number formatting error");
			result -= digit;
			last = pos;
			ch = getChar();
		}
		pos = last;
		return negative ? result : -result;
	}

	public double parseFloat() {
		StringBuilder val = new StringBuilder();
		int last = pos;
		char ch = getChar();
		while ((ch >= '0' && ch <= '9') || ch == 'e' || ch == '_' || ch == '.' || ch == '-') {
			if (ch != '_')
				val.append(ch);
			last = pos;
			ch = getChar();
		}
		pos = last;
		return Double.parseDouble(val.toString());
	}

	public String parseIdentifier() {
		skipWhiteSpace();
		StringBuilder val = new StringBuilder();
		int last = pos;
		char ch = getChar();
		while (ch == '_' || Character.isLetterOrDigit(ch)) {
			val.append(ch);
			last = pos;
			ch = getChar();
		}
		pos = last;
		if (val.length() == 0)
			error("Expect identifier");
		return val.toString();
	}

	public boolean hasIdentifier() {
		int orig = pos;
		char ch = getChar();
		while (ch == ' ' || ch == '\t' || ch == '\n')
			ch = getChar();
		boolean result = ch == '_' || Character.isLetter(ch);
		pos = orig;
		return result;
	}

	private boolean internPeek(String string) {
		int orig = pos;
		for (int nr = 0; nr < string.length(); nr++) {
			char ch = string.charAt(nr);
			if (pos < capacity && buffer.get(pos) == ch)
				pos++;
			else if (pos >= capacity || getChar() != ch) {
				pos = orig;
				return false;
			}
		}
		return true;
	}

	private boolean peek(int indent) {
		if (indent > 0 && pos > 0 && buffer.get(pos - 1) == ' ')
			indent--; // allow for spaces to be already skipped
		if (pos + indent >= capacity)
			return false;
		for (int sp = 0; sp < indent; sp++) {
			if (buffer.get(pos + sp) != ' ')
				return false;
		}
		return true;
	}

	public boolean peek(int indent, char token) {
		if (!peek(indent))
			return false;
		if (pos == capacity)
			return false;
		return buffer.get(pos + indent) == token;
	}

	public void skip() {
		pos++;
	}

	public void line(StringBuilder str) {
		while (pos < capacity) {
			char ch = getChar();
			if (ch == '\n')
				return;
			str.append(ch);
		}
	}

	public void skipWhiteSpace() {
		while (!eof() && (peek(" ") || peek("\t")))
			pos++;
	}

	public void newLine() {
		newLine(false);
	}

	public int getIndent() {
		skipWhiteSpace();
		return pos - lineStart;
	}

	public void newLine(String msg) {
		skipWhiteSpace();
		if (peek("\n")) {
			pos++;
			lineStart = pos;
			linenr++;
		} else
			error("Expecting a newline " + msg);
	}

	public void newLine(boolean optional) {
		skipWhiteSpace();
		if (peek("\n")) {
			pos++;
			lineStart = pos;
			linenr++;
		} else if (!optional)
			error("Expecting a newline");
	}

	public boolean finished() {
		return pos == capacity;
	}

	public void skipRemarks() {
		skipWhiteSpace();
		while (peek("#") || peek("\n")) {
			if (peek("\n")) { // skip empty lines
				newLine(false);
				continue;
			}
			while (!peek("\n"))
				pos++;
			skipWhiteSpace();
		}
	}

	public static class State {
		private int pos;
		private int lineNr;
		private int lineStart;

		public int getLineNr() {
			return lineNr;
		}

		@Override
		public String toString() {
			return "{pos=" + pos + ", lineNr=" + lineNr + ", lineStart=" + lineStart + "}";
		}
	}

	public State getState() {
		State state = new State();
		state.pos = this.pos;
		state.lineNr = this.linenr;
		state.lineStart = this.lineStart;
		return state;
	}

	public void setState(State state) {
		if (state == null) {
			this.pos = -1;
			this.linenr = -1;
			this.lineStart = 0;
		} else {
			this.pos = state.pos;
			this.linenr = state.lineNr;
			this.lineStart = state.lineStart;
		}
	}
}

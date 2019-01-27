package org.memorydb.handler;

import java.nio.file.Path;

public class StringText implements Text {
	private IntArray positions = new IntArray();
	private int pos = 0;
	private final String str;

	public StringText(String str) {
		this.str = str;
	}

	@Override
	public void include(Path file, String name) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void restore() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean end() {
		return pos == str.length();
	}

	@Override
	public char readChar() {
		if (end())
			return 0;
		return str.charAt(pos++);
	}

	@Override
	public int addPos() {
		int res = positions.size();
		positions.add(pos);
		return res;
	}

	@Override
	public void toPos(int anchor) {
		int toSize = positions.size() - 1;
		if (toSize != anchor)
			throw new RuntimeException("Not freed in order");
		pos = positions.get(toSize);
		positions.trim(toSize);
	}

	@Override
	public void freePos(int anchor) {
		int toSize = positions.size() - 1;
		if (toSize != anchor)
			throw new RuntimeException("Not freed in order");
		positions.trim(toSize);
	}

	@Override
	public String substring(int from) {
		if (from < 0 || from >= positions.size())
			return null;
		return str.substring(positions.get(from), pos);
	}

	@Override
	public String tail() {
		return str.substring(pos);
	}

	@Override
	public boolean match(String string) {
		int old = addPos();
		for (int i = 0; i < string.length(); i++) {
			if (readChar() != string.charAt(i)) {
				toPos(old);
				return false;
			}
		}
		freePos(old);
		return true;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append(str).append("\n");
		for (int p = 0; p < pos; p++)
			bld.append(" ");
		bld.append("^");
		return bld.toString();
	}

	@Override
	public String position() {
		return Integer.toString(pos);
	}
}

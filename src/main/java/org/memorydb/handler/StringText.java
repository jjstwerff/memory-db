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
			throw new RuntimeException("Not freed in order " + anchor + " != " + toSize);
		pos = positions.get(toSize);
		positions.trim(toSize);
	}

	@Override
	public void freePos(int anchor) {
		int toSize = positions.size() - 1;
		if (toSize != anchor)
			throw new RuntimeException("Not freed in order " + anchor + " != " + toSize);
		positions.trim(toSize);
	}

	@Override
	public String substring(int from) {
		if (from < 0 || from >= positions.size())
			return null;
		return str.substring(positions.get(from), pos);
	}

	@Override
	public String substring(int from, int till) {
		if (from < 0 || from >= positions.size() || till < 0 || till >= positions.size())
			return null;
		return str.substring(positions.get(from), positions.get(till));
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
		int line = 1;
		int lpos = 1;
		int cpos = pos;
		bld.append(String.format("%06d ", line));
		for (int i=0; i < str.length(); i++) {
			char charAt = str.charAt(i);
			if (charAt == '\n') {
				line++;
				if (i >= cpos) {
					cpos = Integer.MAX_VALUE;
					bld.append("\n...... ");
					for (int l=1; l < lpos; l++)
						bld.append(" ");
					bld.append("^");
				}
				bld.append("\n").append(String.format("%06d ", line));
				lpos = 1;
			} else {
				if (i < cpos)
					lpos++;
				bld.append(charAt);
			}
		}
		if (cpos != Integer.MAX_VALUE) {
			bld.append("\n...... ");
			for (int l=1; l < lpos; l++)
				bld.append(" ");
			bld.append("^");
		}
		bld.append("\n");
		return bld.toString();
	}

	@Override
	public String position() {
		return Integer.toString(pos);
	}
}

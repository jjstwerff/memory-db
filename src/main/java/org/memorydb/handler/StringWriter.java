package org.memorydb.handler;

public class StringWriter implements Writer {
	StringBuilder bld;
	boolean[] first;
	int depth;
	boolean append;

	public StringWriter() {
		bld = new StringBuilder();
		first = new boolean[100];
		depth = -1;
		append = false;
	}

	@Override
	public void startArray() {
		if (!append) {
			nextElement();
			bld.append("[");
			depth++;
			first[depth] = true;
		}
	}

	@Override
	public void endArray() {
		if (!append) {
			bld.append("]");
			depth--;
		}
	}

	@Override
	public void startObject() {
		nextElement();
		bld.append("{");
		depth++;
		first[depth] = true;
	}

	@Override
	public void field(String name) {
		if (depth == -1)
			throw new RuntimeException("Start an object first");
		if (!first[depth])
			bld.append(",");
		bld.append("\"").append(name).append("\":");
		first[depth] = true;
	}

	@Override
	public void element(Object value) {
		nextElement();
		value(value);
	}

	private void value(Object value) {
		if (value == null) {
			bld.append("null");
		} else if (value instanceof Boolean || value instanceof Integer || value instanceof Long || value instanceof Double) {
			bld.append(value.toString());
		} else {
			if (!append)
				bld.append("\"");
			bld.append(value.toString());
			if (!append)
				bld.append("\"");
		}
	}

	private void nextElement() {
		if (!append && depth != -1 && !first[depth])
			bld.append(",");
		if (depth >= 0)
			first[depth] = false;
	}

	@Override
	public void append(boolean start) {
		append = start;
	}

	@Override
	public void endObject() {
		bld.append("}\n");
		depth--;
	}

	@Override
	public String toString() {
		return bld.toString();
	}
}

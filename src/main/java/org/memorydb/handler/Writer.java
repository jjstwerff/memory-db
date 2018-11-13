package org.memorydb.handler;

public interface Writer {
	void field(String name);

	void element(Object value);

	void append(boolean start);

	void startArray();

	void endArray();

	void startObject();

	void endObject();
}

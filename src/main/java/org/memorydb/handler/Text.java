package org.memorydb.handler;

import java.nio.file.Path;

public interface Text {
	void include(Path file, String name);

	void restore();

	boolean end();

	char readChar();

	int addPos();

	void toPos(int pos);

	void freePos(int pos);

	String substring(int from);

	String tail();

	boolean match(String string);

	String position();
}

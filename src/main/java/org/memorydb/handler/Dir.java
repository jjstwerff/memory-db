package org.memorydb.handler;

import java.nio.file.Files;
import java.nio.file.Path;

public class Dir {
	private final Path base;

	public Dir(Path base) {
		this.base = base;
	}

	public TextFile file(String name) {
		Path file = base.resolve(name);
		if (name.startsWith("../") || name.indexOf("/../") >= 0 || !Files.isRegularFile(file, java.nio.file.LinkOption.NOFOLLOW_LINKS))
			return null;
		return new TextFile(file, name);
	}
}

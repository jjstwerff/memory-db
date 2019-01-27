package org.memorydb.handler;

import java.nio.file.Paths;

import org.junit.Test;

import junit.framework.Assert;

public class TestDir {
	@Test
	public void testDir() {
		Dir dir = new Dir(Paths.get(getClass().getResource("/files").getFile()));
		Assert.assertNull(dir.file("../files/start.tsv")); // filename starts with ../
		Assert.assertNull(dir.file("dir/../start.tsv")); // filename contains /../
		Assert.assertNull(dir.file("bad_start.tsv")); // unknown file or symbolic link
		TextFile file = dir.file("start.tsv");
		Assert.assertEquals('N', file.readChar());
	}
}

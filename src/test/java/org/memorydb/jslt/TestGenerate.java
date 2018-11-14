package org.memorydb.jslt;

import org.junit.Test;
import org.memorydb.file.DBParser;
import org.memorydb.file.Parser;
import org.memorydb.meta.Meta;
import org.memorydb.structure.Store;

public class TestGenerate {
	@Test
	public void testGenerate() {
		Store store = new Store(10);
		try (Meta meta = new Meta(store);
				Parser parser = new DBParser(getClass().getResource("/generate/meta.db").getFile())) {
			meta.addProject().parse(parser);
			System.out.println(meta);
		}
	}
}

package org.memorydb.jslt;

import org.junit.Test;
import org.memorydb.file.DBParser;
import org.memorydb.file.Parser;
import org.memorydb.meta.ChangeProject;
import org.memorydb.meta.Meta;
import org.memorydb.structure.Store;
import org.memorydb.table.MemoryTests;

public class TestGenerate extends MemoryTests {
	@Test
	public void testGenerate() {
		Store store = new Store(10);
		try (Meta meta = new Meta(store)) {
			ChangeProject project = meta.addProject();
			try (Parser parser = new DBParser(getClass().getResource("/generate/meta.db").getFile())) {
				project.parse(parser);
			}
			StringBuilder code = new StringBuilder();
			jslt(project, "$.pack", "\"org.memorydb.meta\"", code);
			jslt(project, "@.name in $.records", "[\"Field\",\"OrderField\",\"Project\",\"Record\",\"Value\"]", code);
			jslt(project, "@.name in $.records[?@.name=='Field']", "[\"Field\"]", code);
			jslt(project, "@.name in @.fields in $.records[?@.name=='Field']", "[[\"name\",\"type\",\"key\",\"mandatory\",\"values\",\"related\",\"record\",\"content\",\"child\",\"to\",\"order\",\"minimum\",\"maximum\",\"format\",\"decimals\",\"default\",\"condition\",\"description\"]]", code);
		}
	}
}
package org.memorydb.table;

import org.junit.Assert;
import org.junit.Test;
import org.memorydb.jslt.ChangeMacro;
import org.memorydb.jslt.MatchStep.Type;
import org.memorydb.jslt.MatchingArray;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

public class TestStructure extends MemoryTests {
	@Test
	public void testArray() {
		Store store = new Store(12);
		try (ChangeMacro macro = new ChangeMacro(store, 0)) {
			macro.setName("main");
			MatchingArray match = macro.addMatching();
			match.setType(Type.JUMP);
			match.setJump(10);
			match = macro.addMatching();
			match.setType(Type.TEST_BOOLEAN);
			match.setMboolean(true);
			match.setMnfalse(11);
			Assert.assertEquals("name=main, alternatives=[\n], matching=[\n  type=JUMP, jump=10\n  type=TEST_BOOLEAN, mboolean=true, mbfalse=0\n]\n", macro.toString());
			StringBuilder bld = new StringBuilder();
			MatchingArray matchArray = macro.getMatching();
			RecordInterface elm = matchArray.start();
			int pos = 0;
			while (elm != null) {
				bld.append(pos + ":" + elm.type() + " " + elm.java());
				elm = elm.next();
				pos++;
			}
			Assert.assertEquals("1:OBJECT type=JUMP, jump=10\n2:OBJECT type=TEST_BOOLEAN, mboolean=true, mbfalse=0\n", bld.toString());
		}
	}
}

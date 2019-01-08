package org.memorydb.meta;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.memorydb.file.TestRead;
import org.memorydb.table.MemoryTests;

public class TestInterfaces extends MemoryTests {
	@Test
	@Ignore("Metadata is just too different from this. Rewrite it in the future")
	public void testWriting() {
		Project parse = TestRead.parse("name=Struct, records=[", //
				"  name=maintenance,fields=[]", //
				"  & size=10,description=Maintenance cost", //
				"  name=discount,size=13,full=true,description=Discount table", //
				"]");
		jslt(parse, "$.name", "\"Struct\"");
		jslt(parse, "$", "{\"name\":\"Struct\",\"indexes\":[],\"records\":["
				+ "{\"name\":\"maintenance\",\"fields\":[],\"fieldOnName\":[],\"freeBits\":[],\"size\":10,\"related\":false,\"full\":false,\"description\":\"Maintenance cost\"},"
				+ "{\"name\":\"discount\",\"fields\":[],\"fieldOnName\":[],\"freeBits\":[],\"size\":13,\"related\":false,\"full\":true,\"description\":\"Discount table\"}]}");
		jslt(parse, "{dir:\"foo\",name:\"bar\"}+$-{records:null,indexes:null}+{description:\"nothing\",something:true,bits:@.size * 8}", "{\"name\":\"Struct\"}");
		jslt(parse, "$-{indexes:null,name:null}+{records:{*:{foo:\"foo\"}}-{records[]:{*:{description:null,fields:null,freeBits:null}}}", "{\"name\":\"Struct\",\"indexes\":[]}");
		// read it back .. traversing it.. reading individual fields
		// write some fields .. add some records.. read back the complete data to verify it's full state
		// remove some records.. read back the complete data to verify it's full state
		Assert.assertEquals("name=Struct, indexes=[\n" //
				+ "], records=[\n" //
				+ "  name=maintenance, fields=[\n  ], fieldOnName=[\n  ], setIndexes=[\n  ], freeBits=[\n" //
				+ "  ], size=10, related=false, full=false, description=Maintenance cost\n" //
				+ "  name=discount, fields=[\n  ], fieldOnName=[\n  ], setIndexes=[\n  ], freeBits=[\n" //
				+ "  ], size=13, related=false, full=true, description=Discount table\n" + "]\n", parse.toString());
	}
}

package org.memorydb.file;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.memorydb.handler.MutationException;
import org.memorydb.meta.Field;
import org.memorydb.meta.Meta;
import org.memorydb.meta.Project;

public class TestRead extends ErrorCheck {
	@Test
	public void testMetaRecord() {
		Assert.assertEquals("name=MetaStructure, indexes=[\n], records=[\n]\n", parse("name=MetaStructure").toString());
	}

	@Test
	public void testRecord() {
		Assert.assertEquals("name=Struct, indexes=[\n" //
				+ "], records=[\n" //
				+ "  name=maintenance, fields=[\n" //
				+ "  ], fieldOnName=[\n" //
				+ "  ], setIndexes=[\n" //
				+ "  ], freeBits=[\n" //
				+ "  ], size=10, related=false, full=false, description=Maintenance cost\n" //
				+ "  name=discount, fields=[\n" //
				+ "  ], fieldOnName=[\n" //
				+ "  ], setIndexes=[\n" //
				+ "  ], freeBits=[\n" //
				+ "  ], size=13, related=false, full=false, description=Discount table\n" //
				+ "]\n",
				parse("# simple records", "", //
						"name=Struct, records=[", //
						"  name=maintenance,fields=[]", //
						"  & size=10,description=Maintenance cost", //
						"  name=discount,size=13,description=Discount table", //
						"]", "# after the fact").toString());
	}

	@Test
	public void testFieldAndIndex() {
		try (Meta meta = new Meta()) {
			Project project = meta.addProject();
			try (Parser parser = new DBParser(file("record2.txt", new String[] { //
					"name=fields, records=[", //
					"  name=maintenance,fieldOnName=[", //
					"    name=Name, type=STRING, pos=0, key=true", //
					"    name=Moment, type=DATE, pos=1", //
					"    name=Value, type=INTEGER, pos=3", //
					"  ]", //
					"],indexes=[", //
					"  name=Timeline, indexFields=[", //
					"    str=Moment", //
					"    str=Name", //
					"  ]", //
					"]" }))) {
				Assert.assertEquals("name=fields, indexes=[\n" //
						+ "  name=Timeline, indexFields=[\n" //
						+ "    str=Moment\n" //
						+ "    str=Name\n" //
						+ "  ], fieldPos=0, flagPos=0, parentPos=0, primary=false\n" //
						+ "], records=[\n" //
						+ "  name=maintenance, fields=[\n" //
						+ "  ], fieldOnName=[\n" //
						+ "    name=Moment, type=DATE, auto=false, pos=1, values=[\n" //
						+ "    ], key=false, mandatory=false\n" //
						+ "    name=Name, type=STRING, auto=false, pos=0, values=[\n" //
						+ "    ], key=true, mandatory=false\n" //
						+ "    name=Value, type=INTEGER, auto=false, pos=3, values=[\n" //
						+ "    ], key=false, mandatory=false\n" //
						+ "  ], setIndexes=[\n" //
						+ "  ], freeBits=[\n" //
						+ "  ], size=0, related=false, full=false\n" //
						+ "]\n", project.parse(parser).toString());
			}
			// TODO prevent double index field in Timeline
			try (Parser parser = new DBParser(file("record3.txt", new String[] { //
					"name=fields, records=[", //
					"  name=maintenance,fieldOnName=[", //
					"    !name=Moment", //
					"    name=Value, pos=1", //
					"  ]", //
					"],indexes=[", //
					"  name=Timeline, indexFields=[", //
					"    str=Name", //
					"  ]", //
					"]" }))) {
				Assert.assertEquals("name=fields, indexes=[\n" //
						+ "  name=Timeline, indexFields=[\n" //
						+ "    str=Moment\n" //
						+ "    str=Name\n" //
						+ "    str=Name\n" //
						+ "  ], fieldPos=0, flagPos=0, parentPos=0, primary=false\n" //
						+ "], records=[\n" //
						+ "  name=maintenance, fields=[\n" //
						+ "  ], fieldOnName=[\n" //
						+ "    name=Name, type=STRING, auto=false, pos=0, values=[\n" //
						+ "    ], key=true, mandatory=false\n" //
						+ "    name=Value, type=INTEGER, auto=false, pos=1, values=[\n" //
						+ "    ], key=false, mandatory=false\n" //
						+ "  ], setIndexes=[\n" //
						+ "  ], freeBits=[\n" //
						+ "  ], size=0, related=false, full=false\n" //
						+ "]\n", project.parse(parser).toString());
			}
			Assert.assertEquals("used: 2kb records:8 size: 156kb\n" //
					+ "free: 154kb spaces:2\n" //
					+ "strings number:6 average:6\n" //
					+ "data:0kb total:0kb\n" //
					+ "hash:0kb total:0kb", project.getStore().analyze());
			Assert.assertEquals("{Moment=18, Name=26, Timeline=8, Value=45, fields=0, maintenance=32}", project.getStore().getStrings().toString());
		}
	}

	@Test
	public void testComments() {
		Assert.assertEquals("name=nothing, indexes=[\n], records=[\n]\n", parse("name=nothing,records=[", " # nothing to see", "", "  # here", "] # really!").toString());
	}

	@Test
	public void testMultiLayersAndArray() {
		Assert.assertEquals("name=problem, indexes=[\n], records=[\n" //
				+ "  name=table, fields=[\n  ], fieldOnName=[\n" //
				+ "    name=Name, type=STRING, auto=false, pos=0, values=[\n    ], key=false, mandatory=false\n" //
				+ "  ], setIndexes=[\n  ], freeBits=[\n  ], size=0, related=false, full=false\n]\n",
				parse("name=problem,records=[", //
						"  name=table, fieldOnName=[", //
						"    name=Name, type=STRING, pos=0", "  ]\n]").toString());
	}

	@Test
	public void testMultiLine() {
		Project meta = parse("records=[", "  name=ct_table, description=", "    what is", "    that?", "]");
		Assert.assertEquals("what is\nthat?", meta.getRecords("ct_table").getDescription());
		Assert.assertEquals("name=ct_table, fields=[\n], fieldOnName=[\n], setIndexes=[\n], freeBits=[\n], size=0, related=false, full=false, description=\n  what is\n  that?\n",
				meta.getRecords("ct_table").toString());
		Assert.assertEquals(
				"name=ct_table, fields=[\n], fieldOnName=[\n], setIndexes=[\n], freeBits=[\n], size=0, related=false, full=false, description=\n  what, is\n   {that}?\n",
				parse("records=[", "  name=ct_table, description=", "    what, is", "     {that}?", "]").getRecords("ct_table").toString());
		Assert.assertEquals("name=ct_table, fields=[\n], fieldOnName=[\n], setIndexes=[\n], freeBits=[\n], size=0, related=false, full=false, description=\\{\\}\n",
				parse("records=[", "  name=ct_table, description=\\{\\}", "]").getRecords("ct_table").toString());
	}

	@Test
	public void testEscaping() {
		Assert.assertEquals("\\ what is that?", parse("records=[", "  name=ct_table, description=\\\\ what is that?", "]").getRecords("ct_table").getDescription());
		Assert.assertEquals(",} what is that?", parse("records=[", "  name=ct_table, description=\\,\\} what is that?", "]").getRecords("ct_table").getDescription());
		Assert.assertEquals("\f what is that?", parse("records=[", "  name=ct_table, description=\\f what is that?", "]").getRecords("ct_table").getDescription());
		Assert.assertEquals("\t what is that?", parse("records=[", "  name=ct_table, description=\\t what is that?", "]").getRecords("ct_table").getDescription());
		Assert.assertEquals("é×€§ what is that?", parse("records=[", "  name=ct_table, description=é×€§ what is that?", "]").getRecords("ct_table").getDescription());
	}

	@Test
	public void testNumberNull() {
		expectMessage("Mandatory 'pos' field in /tmp/problem.txt:3/25");
		parseField("something", "name=something, pos!");
	}

	@Test
	public void testIncompleteData() {
		expectMessage("Expect a '=' after a field in /tmp/problem.txt:1/8");
		parse("records");
	}

	@Test
	public void testMissingField() {
		expectMessage("Expecting an identifier in /tmp/problem.txt:1/1");
		parse("=");
	}

	@Test
	public void testSetProblem() {
		expectMessage("Expect a '[' at the start of a set in /tmp/problem.txt:1/8");
		parse("records=");
	}

	@Test
	public void testSetProblem2() {
		expectMessage("Expecting a newline in /tmp/problem.txt:1/10");
		parse("records=[name=");
	}

	@Test
	public void testSetProblem3() {
		expectMessage("Expect indent of 2 or ']' at the start of the line in /tmp/problem.txt:2/1");
		parse("records=[");
	}

	@Test
	public void testName() {
		expectMessage("Unknown field 'record' in /tmp/problem.txt:1/7");
		parse("record=[]");
	}

	@Test
	public void testComma() {
		expectMessage("Incorrect number in /tmp/problem.txt:3/25");
		parseField("something", "pos=1 name=something");
	}

	@Test
	@Ignore("To be implemented")
	public void testDuplicateKey() {
		expectMessage("Duplicate record in /tmp/problem.txt:3/3");
		parse("records=[", "  name=ct_table", "  name=ct_table", "]");
	}

	@Test
	public void testComma2() {
		expectMessage("Incorrect field data after field: name in /tmp/problem.txt:3/26");
		parseField("something", "name=something index={name=anything}");
	}

	@Test
	public void testIndention() {
		expectMessage("Expect indent of 2 or ']' at the start of the line in /tmp/problem.txt:2/1");
		parse("records=[", " name=ct_table", "]");
	}

	@Test
	public void testIndention2() {
		expectMessage("Expect indent of 2 or ']' at the start of the line in /tmp/problem.txt:2/3");
		parse("records=[", "   name=ct_table", " ]");
	}

	@Test
	public void testIndention3() {
		expectMessage("Expect indent of 4 or ']' at the start of the line in /tmp/problem.txt:3/3");
		parse("records=[", "  name=ct_table, fields=[], setIndexes=[", "   name=idx", "  ]\n]");
	}

	@Test
	public void testIndention4() {
		expectMessage("Expect indent of 2 or ']' at the start of the line in /tmp/problem.txt:2/5");
		parse("records=[", "    name=ct_table", " ]");
	}

	@Test
	public void testBrokenRelation() {
		expectMessage("Expect a '=' after a field in /tmp/problem.txt:3/40");
		parseField("something", "name=something, index={not_existing}");
	}

	@Test
	public void testRelationSyntax() {
		expectMessage("Expecting: } in /tmp/problem.txt:3/45");
		parseField("something", "name=something, index={name=not_existing");
	}

	@Test
	public void testRelationContent() {
		expectMessage("Could not find corresponding record in /tmp/problem.txt:3/45");
		parseField("something", "name=something, index={name=not_existing}");
	}

	@Test
	public void testRelation() {
		parse("indexes=[", "  name=first", "], records=[", "  name=ct_table, fieldOnName=[", "    name=one, index={name=first}", "  ]", "]");
	}

	@Test
	@Ignore("To be implemented")
	public void testRelationNull() {
		expectMessage("Null value not allowed on field IndexField.field in /tmp/problem.txt:4/13");
		file("/tmp/problem.txt", new String[] { "records=[", //
				"  id=1, name=ct_table, fields=[], indexes=[", //
				"    name=idx, fields=[", //
				"      field!", //
				"    ]\n  ]\n]" });
	}

	@Test
	public void testBooleanProblem() {
		expectMessage("Expect 'true' or 'false' after a boolean field in /tmp/problem.txt:2/29");
		parse("records=[", "  name=ct_table, related=bla", "]\n]");
	}

	@Test
	public void testBooleanNull() {
		expectMessage("Mandatory 'mandatory' field in /tmp/problem.txt:3/39");
		parse("records=[", //
				"  name=ct_table, fieldOnName=[", //
				"    name=Name, type=STRING, mandatory!", //
				"  ]\n]");
	}

	@Test
	public void testEnum() {
		expectMessage("Cannot parse 'Strings' for field Field.type in /tmp/problem.txt:3/28");
		parseField("Name", "name=Name, type=Strings");
	}

	@Test
	public void testEnumNull() {
		expectMessage("Mandatory 'type' field in /tmp/problem.txt:3/21");
		parseField("Name", "name=Name, type!");
	}

	@Test
	@Ignore("To be implemented")
	public void testNullEnum() {
		expectMessage("Null value not allowed in field Field.type in /tmp/problem.txt:3/5");
		file("/tmp/problem.txt", new String[] { "set=[", //
				"  id=1, name=ct_table, fields=[", //
				"    id=1, name=Name", //
				"  ]\n]" });
	}

	@Test
	public void testEscapeProblem() {
		expectMessage("Unknown escaped token in /tmp/problem.txt:2/38");
		parse("records=[", "  id=1, name=ct_table, description=\\p what is that?", "]");
	}

	@Test
	@Ignore("To be implemented")
	public void testStringNull() {
		expectMessage("Null value not allowed in field Record.name in /tmp/problem.txt:2/14");
		file("/tmp/problem.txt", new String[] { "set=[", "  id=1, name!", "]" });
	}

	private Field parseField(String fieldName, String data) {
		Project meta = parse("records=[", "  name=ct__table, fieldOnName=[", "    " + data, "  ]", "]");
		return meta.getRecords("ct__table").getFieldName(fieldName);
	}

	public static Project parse(String... lines) {
		try (Meta meta = new Meta()) {
			Project project = meta.addProject();
			try (Parser parser = new DBParser(file("problem.txt", lines))) {
				try {
					project.parse(parser);
				} catch (MutationException e) {
					parser.error(e.getMessage());
					throw e;
				}
			}
			return project;
		}
	}
}
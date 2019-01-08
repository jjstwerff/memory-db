package org.memorydb.jslt;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.memorydb.file.Scanner;
import org.memorydb.jslt.JsltParser.Parser;
import org.memorydb.structure.Store;
import org.memorydb.table.MemoryTests;

public class TestJslt extends MemoryTests {
	@Test
	public void testSimple() {
		jslt("0", "0");
		jslt("-11", "-11");
		jslt("-0.27", "-0.27");
		jslt("[\"a\", 2]", "[\"a\",2]");
		jslt("{a:1, \"b\":[true]}", "{\"a\":1,\"b\":[true]}");
		jslt("1 + 2", "3");
		jslt("15 - 2", "13");
		jslt("10 * 2", "20");
		jslt("10.1 * 2", "20.2");
		jslt("10.1 + 2", "12.1");
		jslt("\"a\" * 2", "\"aa\"");
		jslt("65 % 10", "5");
		jslt("2 == 3", "false");
		jslt("1 > 2", "false");
		jslt("not(1.0 > 2.1)", "true");
		jslt("\"a\" + 1", "\"a1\"");
		jslt("[1] + 3", "[1,3]");
		jslt("[1] + 3 + 'a'", "[1,3,\"a\"]");
		jslt("[] + 1 + 3 + 6 + 8", "[1,3,6,8]");
		jslt("[1, 0] + [3, 2]", "[1,0,3,2]");
		jslt("[1, 0] + [3, 2] + 'a'", "[1,0,3,2,\"a\"]");
		jslt("{A:3 * 2 - 5, C:123} + {C:true}", "{\"A\":1,\"C\":true}");
		jslt("{A:3 * 2 - 5, C:123} + {A:2}", "{\"C\":123,\"A\":2}");
		jslt("{\"A\":1} + {\"C\":true}", "{\"A\":1,\"C\":true}");
		jslt("\"a\" != \"aa\"", "true");
		jslt("\"a\" != \"aa\" or false", "true");
		jslt("\"aa\" == \"aa\" and false", "false");
		jslt("\"al\" < \"ao\"", "true");
		jslt("true ? 1 : 2", "1");
		jslt("[1, 0, 3, 2][2]", "3");
		jslt("[1, 0, 3, 2][-1]", "2");
		jslt("[1, 0, 3, 2][2, 2]", "[3,3]");
		jslt("[1, 0, 3, 2][2, 1:]", "[3,0,3,2]");
		jslt("[1, 0, 3, 2][1:3]", "[0,3]");
		jslt("[1, 0, 3, 2][-2:]", "[3,2]");
		jslt("[1, 0, 3, 2][2:]", "[3,2]");
		jslt("[1, 0, 3, 2][2:-1]", "[3]");
		jslt("[1, 0, 3, 2][:-2]", "[1,0]");
		jslt("[1, 0, 3, 2][::-1]", "[2,3,0,1]");
		jslt("[1, 0, 3, 2][1::-1]", "[0,1]");
		jslt("[1, 0, 3, 2][:-3:-1]", "[2,3]");
		jslt("[1, 0, 3, 2][::2]", "[1,3]");
		jslt("@ + \"c\" in [\"b\",\"a\"]", "[\"bc\",\"ac\"]");
		jslt("{\"val\":{first:@.name, val:@.data} in [{name:\"Tim\", data:26 + 20}, {name:\"Dan\", data:3 > 2}]}",
				"{\"val\":[{\"first\":\"Tim\",\"val\":46},{\"first\":\"Dan\",\"val\":true}]}");
		jslt("@.data in [{name:\"Tim\", data:46}, {name:\"Dan\", data:true}][/@.name]", "[true,46]");
		jslt("[{name:\"Tim\", data:46}, {name:\"Dan\", data:true}].length()", "2");
		jslt("@.name in [{name:\"Tim\", data:false}, {name:\"Dan\", data:true}][?@.data]", "[\"Dan\"]");
		jslt("{name:@} in [\"Tim\", \"Dan\"]", "[{\"name\":\"Tim\"},{\"name\":\"Dan\"}]");
		jslt("[\"Tim\", \"Dan\"][\\@]", "[\"Tim\",\"Dan\"]");
		jslt("\"thiș is dätà\".length()", "12");
		jslt("{is:true, this:2, true:\"ok\"}.length()", "3");
		jslt("{length:123}.length", "123");
		jslt("{name:@.name(), length:@.length(), type:@.type(), value:@} in {name:\"Tim\", data:46}",
				"[{\"name\":\"name\",\"length\":3,\"type\":\"STRING\",\"value\":\"Tim\"},{\"name\":\"data\",\"length\":null,\"type\":\"NUMBER\",\"value\":46}]");
		jslt("[[].type(), {}.type(), null.type(), false.type()]", "[\"ARRAY\",\"OBJECT\",\"NULL\",\"BOOLEAN\"]");
		jslt("\"123&{2 + 2}5\"", "\"12345\"");
		jslt("@ in \"123\"", "[\"1\",\"2\",\"3\"]");
		jslt("@ in 3", "[0,1,2]");
		jslt("'123'", "\"123\"");
		jslt("\"123\"[1]", "\"2\"");
		jslt("\"123\"[-2]", "\"2\"");
		jslt("\"1234\"[1:-1]", "\"23\"");
		jslt("\"1234\"[::-1]", "\"4321\"");
		jslt("\"1234\"[::2]", "\"13\"");
		jslt("\"1234\"[-3:]", "\"234\"");
		jslt("\"1234\".index(\"23\")", "1");
		jslt("\"1234\".index(\"231\")", "null");
		jslt("{value:@, index:@.index(), first:@.first(), last:@.last()} in \"abc\"",
				"[{\"value\":\"a\",\"index\":0,\"first\":true,\"last\":false},"
						+ "{\"value\":\"b\",\"index\":1,\"first\":false,\"last\":false},{\"value\":\"c\",\"index\":2,\"first\":false,\"last\":true}]");
		jslt("\"count &{[@ + 1, @.last()?'':', '] in 3}\"", "\"count 1, 2, 3\"");
		jslt("[pow(2, 4), string(1), boolean(\"true\"), number(\"123\"), float(\"2.1e4\")]",
				"[16,\"1\",true,123,21000.0]");
		jslt("[[1,2],[1,3],[0,2],[0,1]][/@]", "[[0,1],[0,2],[1,2],[1,3]]");
		jslt("[[1,\"aa\"]==[1,\"aaa\"], [1,'aa']==[1,'aa'], [2]*2, {\"this\":1, \"too\":2} - \"too\", [2] > [1]]",
				"[false,true,[2,2],{\"this\":1},true]");
		jslt("each([''], @ == '\t' ? # + '' : #[:-1] + (#[-1] + @)) for 'abc\tdef\t\tg\t'", "[\"abc\",\"def\",\"\",\"g\",\"\"]");
		jslt("each([['']], @ == '\\n' ? # + [] : @ == '\\t' ? # + '' : #[:-1] + (#[-1] + @)) for 'abc\\tdef\\t\\tg\\t\\na\\tb'", "[[\"abc\",\"def\",\"\",\"g\",\"\"],[\"a\",\"b\"]]");
		jslt("{ sum:each(0, # + @), array:each([], # + @*2), maximum:each(0, @ > # ? @ : #) } for [1, 2, 3]",
				"{\"sum\":6,\"array\":[2,4,6],\"maximum\":3}");
	}

	@Test
	public void testInput() {
		jslt("[{\"name\":\"Tim\", \"value\":[12,[3]]}, {\"name\":null, \"value\":true}]", "$",
				"[{\"name\":\"Tim\",\"value\":[12,[3]]},{\"name\":null,\"value\":true}]");
		jslt("{\"name\":\"Tim\", \"value\":123}", "$.name", "\"Tim\"");
		jslt("{\"a\":{\"name\":\"Tom\", \"value\":123}}", "$.a.name", "\"Tom\"");
		jslt("{\"a\":{\"name\":\"Tom\", \"value\":123}}", "$.a", "{\"name\":\"Tom\",\"value\":123}");
		jslt("[1,[2],3]", "$[0]", "1");
		jslt("[1,[2],3]", "$[1 + 1]", "3");
		jslt("[1,[2],3]", "$[1][0]", "2");
		jslt("[{\"name\":\"Tim\", \"value\":123}, {\"name\":null, \"value\":true}]",
				"[$[1],$[1].value,$[0].name,$[0].name]", "[{\"name\":null,\"value\":true},true,\"Tim\",\"Tim\"]");
		jslt("[2,5,3,0]", "$[/@]", "[0,2,3,5]");
		jslt("[2,5,3,0]", "$[\\@]", "[5,3,2,0]");
		jslt("[{\"name\":\"Tim\", \"value\":123}, {\"name\":\"Dan\"}]", "@.name in $[?not @.value or @.value == 123]",
				"[\"Tim\",\"Dan\"]");
		// jslt("[{\"name\":\"Tim\", \"value\":123}, [1,{\"data\":[{\"name\":\"Dan\",
		// \"value\":true}]}]]", "@.name in $..[?@.value]", "[\"Tim\",\"Dan\"]");
		jslt("[{\"a\":3,\"b\":0},{\"a\":3,\"b\":1},{\"a\":1}]", "$[/@.a][\\@.b]",
				"[{\"a\":1},{\"a\":3,\"b\":1},{\"a\":3,\"b\":0}]");
	}

	@Test
	public void testJSLT() throws IOException {
		Set<Path> files = new TreeSet<>();
		try (DirectoryStream<Path> dir = Files
				.newDirectoryStream(Paths.get(getClass().getResource("/jslt").getFile()))) {
			for (Path file : dir) {
				String fileName = file.getFileName().toString();
				if (fileName.endsWith(".jslt") && fileName.matches("[0-9]?.*")) {
					files.add(file);
				}
			}
		}
		for (Path file : files) {
			Store jsltStore = new Store(3);
			new Parser(new Scanner(file), jsltStore).parse();
			String into = JsltInterpreter.interpret(jsltStore, null);
			String result = file.getFileName().toString();
			StringBuilder code = new StringBuilder();
			if (!result.equals(""))
				for (Macro m : new Macro(jsltStore).new IndexMacros())
					code.append(m.toString());
			compare(result + ".txt", code.toString());
			Assert.assertEquals(result, "\"\"", into);
		}
	}
}

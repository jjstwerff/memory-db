package org.memorydb.jslt;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import org.memorydb.file.Scanner;
import org.memorydb.jslt.JsltParser.Parser;
import org.memorydb.json.JsonParser;
import org.memorydb.structure.Store;
import org.memorydb.table.MemoryTests;

public class TestJslt extends MemoryTests {
	StringBuilder bld = new StringBuilder();

	@Test
	public void testSimple() {
		jslt(null, "0", "0");
		jslt(null, "-11", "-11");
		jslt(null, "-0.27", "-0.27");
		jslt(null, "[\"a\", 2]", "[\"a\",2]");
		jslt(null, "{a:1, \"b\":[true]}", "{\"a\":1,\"b\":[true]}");
		jslt(null, "1 + 2", "3");
		jslt(null, "15 - 2", "13");
		jslt(null, "10 * 2", "20");
		jslt(null, "10.1 * 2", "20.2");
		jslt(null, "10.1 + 2", "12.1");
		jslt(null, "\"a\" * 2", "\"aa\"");
		jslt(null, "65 % 10", "5");
		jslt(null, "2 == 3", "false");
		jslt(null, "1 > 2", "false");
		jslt(null, "!(1.0 > 2.1)", "true");
		jslt(null, "\"a\" + 1", "\"a1\"");
		jslt(null, "[1] + 3", "[1,3]");
		jslt(null, "[1] + 3 + 'a'", "[1,3,\"a\"]");
		jslt(null, "[1, 0] + [3, 2]", "[1,0,3,2]");
		jslt(null, "[1, 0] + [3, 2] + 'a'", "[1,0,3,2,\"a\"]");
		jslt(null, "{A:3 * 2 - 5, C:123} + {C:true}", "{\"A\":1,\"C\":true}");
		jslt(null, "{A:3 * 2 - 5, C:123} + {A:2}", "{\"C\":123,\"A\":2}");
		jslt(null, "{\"A\":1} + {\"C\":true}", "{\"A\":1,\"C\":true}");
		jslt(null, "\"a\" != \"aa\"", "true");
		jslt(null, "\"a\" != \"aa\" or false", "true");
		jslt(null, "\"aa\" == \"aa\" and false", "false");
		jslt(null, "\"al\" < \"ao\"", "true");
		jslt(null, "true ? 1 : 2", "1");
		jslt(null, "[1, 0, 3, 2][2]", "3");
		jslt(null, "[1, 0, 3, 2][-1]", "2");
		jslt(null, "[1, 0, 3, 2][2, 2]", "[3,3]");
		jslt(null, "[1, 0, 3, 2][2, 1:]", "[3,0,3,2]");
		jslt(null, "[1, 0, 3, 2][1:3]", "[0,3]");
		jslt(null, "[1, 0, 3, 2][-2:]", "[3,2]");
		jslt(null, "[1, 0, 3, 2][2:]", "[3,2]");
		jslt(null, "[1, 0, 3, 2][2:-1]", "[3]");
		jslt(null, "[1, 0, 3, 2][:-2]", "[1,0]");
		jslt(null, "[1, 0, 3, 2][::-1]", "[2,3,0,1]");
		jslt(null, "[1, 0, 3, 2][1::-1]", "[0,1]");
		jslt(null, "[1, 0, 3, 2][:-3:-1]", "[2,3]");
		jslt(null, "[1, 0, 3, 2][::2]", "[1,3]");
		jslt(null, "@ + \"c\" in [\"b\",\"a\"]", "[\"bc\",\"ac\"]");
		jslt(null, "{\"val\":{first:@.name, val:@.data} in [{name:\"Tim\", data:26 + 20}, {name:\"Dan\", data:3 > 2}]}",
				"{\"val\":[{\"first\":\"Tim\",\"val\":46},{\"first\":\"Dan\",\"val\":true}]}");
		jslt(null, "@.data in [{name:\"Tim\", data:46}, {name:\"Dan\", data:true}][/@.name]", "[true,46]");
		jslt(null, "[{name:\"Tim\", data:46}, {name:\"Dan\", data:true}].length()", "2");
		jslt(null, "@.name in [{name:\"Tim\", data:46}, {name:\"Dan\", data:true}][?@.data]", "[\"Dan\"]");
		jslt(null, "{name:@} in [\"Tim\", \"Dan\"]", "[{\"name\":\"Tim\"},{\"name\":\"Dan\"}]");
		jslt(null, "[\"Tim\", \"Dan\"][\\@]", "[\"Tim\",\"Dan\"]");
		jslt(null, "\"thiș is dätà\".length()", "12");
		jslt(null, "{is:true, this:2, true:\"ok\"}.length()", "3");
		jslt(null, "{length:123}.length", "123");
		jslt(null, "{name:@.name(), length:@.length(), type:@.type(), value:@} in {name:\"Tim\", data:46}",
				"[{\"length\":null,\"name\":\"data\",\"type\":\"NUMBER\",\"value\":46},{\"length\":3,\"name\":\"name\",\"type\":\"STRING\",\"value\":\"Tim\"}]");
		jslt(null, "[[].type(), {}.type(), null.type(), false.type()]", "[\"ARRAY\",\"OBJECT\",\"NULL\",\"BOOLEAN\"]");
		jslt(null, "\"123&{2 + 2}5\"", "\"12345\"");
		jslt(null, "@ in \"123\"", "[\"1\",\"2\",\"3\"]");
		jslt(null, "@ in 3", "[0,1,2]");
		jslt(null, "'123'", "\"2\"");
		jslt(null, "\"123\"[1]", "\"2\"");
		jslt(null, "\"123\"[-2]", "\"2\"");
		jslt(null, "\"1234\"[1:-1]", "\"23\"");
		jslt(null, "\"1234\"[::-1]", "\"4321\"");
		jslt(null, "\"1234\"[::2]", "\"13\"");
		jslt(null, "\"1234\"[-3:]", "\"234\"");
		jslt(null, "\"1234\".index(\"23\")", "1");
		jslt(null, "\"1234\".index(\"231\")", "null");
		jslt(null, "{value:@, index:@.index(), first:@.first(), last:@.last()} in \"abc\"", "[{\"first\":true,\"index\":0,\"last\":false,\"value\":\"a\"},"
				+ "{\"first\":false,\"index\":1,\"last\":false,\"value\":\"b\"},{\"first\":false,\"index\":2,\"last\":true,\"value\":\"c\"}]");
		jslt(null, "\"count &{[@ + 1, @.last()?\"\":\", \"] in 3}\"", "\"count 1, 2, 3\"");
		jslt(null, "[pow(2, 4), string(1), boolean(\"true\"), number(\"123\"), float(\"2.1e4\")]", "[16,\"1\",true,123,21000.0]");
		jslt(null, "[\"aa\",\"bbb\",\"c\",\"\"][@.length() - 2,0:2]", "[\"c\",\"aa\",\"bbb\"]");
		jslt(null, "[[1,\"aa\"]==[1,\"aaa\"], [2]*2, [2,3] - 3, [2,3,4] - [2,4], {\"this\":1, \"too\":2} - \"too\", [2] > [1]]", "[false,[2,2],[2],[3],{\"this\":1},true]");
		jslt(null, "[[1,2],[1,3],[0,2],[0,1]][/@]", "[[0,1],[0,2],[1,2],[1,3]]");
		compare("code.txt", bld.toString());
		bld.setLength(0);
	}

	@Test
	public void testInput() {
		jslt("[{\"name\":\"Tim\", \"value\":[12,[3]]}, {\"name\":null, \"value\":true}]", "$", "[{\"name\":\"Tim\",\"value\":[12,[3]]},{\"name\":null,\"value\":true}]");
		jslt("{\"name\":\"Tim\", \"value\":123}", "$.name", "\"Tim\"");
		jslt("{\"a\":{\"name\":\"Tom\", \"value\":123}}", "$.a.name", "\"Tom\"");
		jslt("{\"a\":{\"name\":\"Tom\", \"value\":123}}", "$.a", "{\"name\":\"Tom\",\"value\":123}");
		jslt("[1,[2],3]", "$[0]", "1");
		jslt("[1,[2],3]", "$[1 + 1]", "3");
		jslt("[1,[2],3]", "$[1][0]", "2");
		jslt("[{\"name\":\"Tim\", \"value\":123}, {\"name\":null, \"value\":true}]", "[$[1],$[1].value,$[0].name,$[0].name]", "[{\"name\":null,\"value\":true},true,\"Tim\",\"Tim\"]");
		jslt("[{\"name\":\"Tim\", \"value\":123}, [1,{\"data\":[{\"name\":\"Dan\", \"value\":true}]}]]", "$..[?@.value or @.value == 123].name", "[\"Tim\",\"Dan\"]");
		jslt("[2,5,3,0]", "$[/@]", "[0,2,3,5]");
		jslt("[2,5,3,0]", "$[\\@]", "[5,3,2,0]");
		jslt("[{\"a\":3,\"b\":0},{\"a\":3,\"b\":1},{\"a\":1}]", "$[?@.a, /@.a, \\@.b]", "[{\"a\":1},{\"a\":3,\"b\":1},{\"a\":3,\"b\":0}]");
		compare("testInput.txt", bld.toString());
		bld.setLength(0);
	}

	private void jslt(String source, String jslt, String result) {
		jslt(source == null ? null : JsonParser.parse(source), jslt, result, bld);
	}

	@Test
	public void testJSLT() throws IOException {
		try (DirectoryStream<Path> dir = Files.newDirectoryStream(Paths.get(getClass().getResource("/jslt").getFile()))) {
			for (Path file : dir) {
				if (file.getFileName().toString().endsWith(".jslt")) {
					Store jsltStore = new Store(3);
					new Parser(new Scanner(file), jsltStore).parse();
					String into = JsltInterpreter.interpret(jsltStore, null);
					Assert.assertEquals(file.getFileName().toString(), "\"\"", into);
				}
			}
		}
	}
}
